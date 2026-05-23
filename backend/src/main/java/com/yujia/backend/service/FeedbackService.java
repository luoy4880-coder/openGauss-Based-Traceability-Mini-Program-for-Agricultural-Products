package com.yujia.backend.service;

import com.yujia.backend.common.auth.AuthContext;
import com.yujia.backend.common.exception.BusinessException;
import com.yujia.backend.common.response.PageResponse;
import com.yujia.backend.dto.feedback.FeedbackHandleRequest;
import com.yujia.backend.dto.feedback.FeedbackRecallRequest;
import com.yujia.backend.dto.feedback.FeedbackSubmitRequest;
import com.yujia.backend.entity.RecallRecord;
import com.yujia.backend.entity.SysUser;
import com.yujia.backend.entity.SystemTask;
import com.yujia.backend.entity.UserFeedback;
import com.yujia.backend.mapper.ProductItemMapper;
import com.yujia.backend.mapper.SystemTaskMapper;
import com.yujia.backend.mapper.TraceCodeMapper;
import com.yujia.backend.mapper.UserFeedbackMapper;
import com.yujia.backend.vo.FeedbackOverviewVO;
import com.yujia.backend.vo.FeedbackTaskVO;
import com.yujia.backend.vo.StaffOptionVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final UserFeedbackMapper userFeedbackMapper;
    private final SysUserService sysUserService;
    private final FeedbackAiClassifierService feedbackAiClassifierService;
    private final SystemTaskMapper systemTaskMapper;
    private final RecallRecordService recallRecordService;
    private final ProductBatchService productBatchService;
    private final ProductItemMapper productItemMapper;
    private final TraceCodeMapper traceCodeMapper;
    private final CompanyScopeService companyScopeService;

    @Transactional
    public Long submit(FeedbackSubmitRequest request) {
        var authUser = AuthContext.get();
        if (authUser == null) {
            throw new BusinessException(401, "未登录");
        }

        FeedbackAiClassifierService.AiClassifyResult aiResult = feedbackAiClassifierService.classify(
                request.getType(),
                request.getContent()
        );
        Long companyId = resolveFeedbackCompanyId(request.getBatchId(), request.getTraceId());
        Long assigneeUserId = pickDefaultAssigneeUserId(companyId);

        UserFeedback feedback = new UserFeedback();
        feedback.setUserId(authUser.getUserId());
        feedback.setType(request.getType());
        feedback.setContent(request.getContent());
        feedback.setContact(request.getContact());
        feedback.setTraceId(request.getTraceId());
        feedback.setBatchId(request.getBatchId());
        feedback.setCompanyId(companyId);
        feedback.setAiCategory(aiResult.category());
        feedback.setAiPriority(aiResult.priority());
        feedback.setRiskLevel(aiResult.riskLevel());
        feedback.setUrgentFlag("HIGH".equals(aiResult.riskLevel()) ? 1 : 0);
        feedback.setAiSummary(aiResult.summary());
        feedback.setAssigneeUserId(assigneeUserId);
        feedback.setHandleNote("系统自动分类");
        feedback.setStatus(0);
        userFeedbackMapper.insert(feedback);

        if ("HIGH".equals(aiResult.riskLevel())) {
            Long linkedTaskId = createOrRefreshHighRiskTask(feedback, assigneeUserId);
            feedback.setLinkedTaskId(linkedTaskId);
            feedback.setHandleNote("系统判定为高风险反馈，已自动生成待办任务");
            userFeedbackMapper.updateHandle(feedback);
        }
        return feedback.getId();
    }

    public List<UserFeedback> myLatest(Integer limit) {
        var authUser = AuthContext.get();
        if (authUser == null) {
            throw new BusinessException(401, "未登录");
        }
        int safeLimit = limit == null || limit < 1 ? 20 : Math.min(limit, 50);
        return userFeedbackMapper.selectLatestByUserId(authUser.getUserId(), safeLimit);
    }

    public PageResponse<FeedbackTaskVO> page(String keyword,
                                             String category,
                                             Integer priority,
                                             String riskLevel,
                                             Integer status,
                                             Long assigneeUserId,
                                             Integer pageNum,
                                             Integer pageSize) {
        int safePageNum = pageNum == null || pageNum < 1 ? 1 : pageNum;
        int safePageSize = pageSize == null || pageSize < 1 ? 10 : Math.min(pageSize, 100);
        Long companyId = companyScopeService.currentCompanyScopeOrNull();
        long total = userFeedbackMapper.countPage(companyId, keyword, category, priority, riskLevel, status, assigneeUserId);
        List<FeedbackTaskVO> records = userFeedbackMapper.selectPage(
                companyId,
                keyword,
                category,
                priority,
                riskLevel,
                status,
                assigneeUserId,
                (long) (safePageNum - 1) * safePageSize,
                safePageSize
        );
        return PageResponse.<FeedbackTaskVO>builder()
                .records(records)
                .total(total)
                .pageNum(safePageNum)
                .pageSize(safePageSize)
                .build();
    }

    public FeedbackOverviewVO overview() {
        Long companyId = companyScopeService.currentCompanyScopeOrNull();
        FeedbackOverviewVO vo = new FeedbackOverviewVO();
        vo.setPendingCount(userFeedbackMapper.countPending(companyId));
        vo.setHighRiskPendingCount(userFeedbackMapper.countHighRiskPending(companyId));
        vo.setUrgentPendingCount(userFeedbackMapper.countUrgentPending(companyId));
        vo.setLatestHighRiskRecords(userFeedbackMapper.selectLatestHighRisk(companyId, 5));
        return vo;
    }

    @Transactional
    public void handle(Long id, FeedbackHandleRequest request) {
        if (request.getStatus() == null || request.getStatus() < 0 || request.getStatus() > 2) {
            throw new BusinessException(400, "status取值非法");
        }

        UserFeedback feedback = userFeedbackMapper.selectById(id);
        if (feedback == null) {
            throw new BusinessException(404, "反馈记录不存在");
        }
        companyScopeService.assertAccessibleCompany(feedback.getCompanyId());

        var authUser = AuthContext.get();
        if (authUser == null) {
            throw new BusinessException(401, "未登录");
        }

        Long assigneeUserId = request.getAssigneeUserId();
        if (assigneeUserId != null && !isAssignableStaff(assigneeUserId, feedback.getCompanyId())) {
            throw new BusinessException(400, "分派处理人不存在、已禁用或不属于当前公司");
        }
        assertCanHandle(feedback, assigneeUserId, authUser.getUserId());

        feedback.setStatus(request.getStatus());
        feedback.setAssigneeUserId(assigneeUserId);
        feedback.setHandleNote(request.getHandleNote());
        applyRecallIfNeeded(feedback, request.getRecall());
        userFeedbackMapper.updateHandle(feedback);
        syncLinkedTask(feedback);
    }

    public List<StaffOptionVO> assignees() {
        return sysUserService.listActiveStaffEntitiesByCompanyId(companyScopeService.currentCompanyScopeOrNull()).stream()
                .map(this::toStaffOption)
                .toList();
    }

    public Long pickSuggestedAssigneeUserId() {
        return pickSuggestedAssigneeUserId(companyScopeService.currentCompanyScopeOrNull());
    }

    private StaffOptionVO toStaffOption(SysUser sysUser) {
        StaffOptionVO option = new StaffOptionVO();
        option.setId(sysUser.getId());
        option.setUsername(sysUser.getUsername());
        option.setRealName(sysUser.getRealName());
        return option;
    }

    private Long pickSuggestedAssigneeUserId(Long companyId) {
        if (companyId == null) {
            return null;
        }
        List<SysUser> staffs = sysUserService.listActiveStaffEntitiesByCompanyId(companyId);
        if (staffs.isEmpty()) {
            return null;
        }
        return staffs.get(0).getId();
    }

    private Long pickDefaultAssigneeUserId(Long companyId) {
        return null;
    }

    private boolean isAssignableStaff(Long assigneeUserId, Long companyId) {
        if (companyId == null) {
            return sysUserService.isActiveStaff(assigneeUserId);
        }
        return sysUserService.listActiveStaffEntitiesByCompanyId(companyId).stream()
                .anyMatch(staff -> assigneeUserId.equals(staff.getId()));
    }

    private void assertCanHandle(UserFeedback feedback, Long nextAssigneeUserId, Long currentUserId) {
        if (companyScopeService.isAdmin()) {
            return;
        }
        if (feedback.getAssigneeUserId() != null && !feedback.getAssigneeUserId().equals(currentUserId)) {
            throw new BusinessException(403, "该反馈已分派给其他人员，不能直接处理");
        }
        if (nextAssigneeUserId != null && !nextAssigneeUserId.equals(currentUserId)) {
            throw new BusinessException(403, "只能将反馈分派给自己");
        }
    }

    private Long resolveFeedbackCompanyId(Long batchId, String traceId) {
        Long resolvedBatchId = batchId != null ? batchId : resolveBatchIdByTraceId(traceId);
        if (resolvedBatchId != null) {
            Long batchCompanyId = productBatchService.detail(resolvedBatchId).getCompanyId();
            Long currentCompanyId = companyScopeService.currentCompanyIdOrNull();
            if (currentCompanyId != null) {
                companyScopeService.assertAccessibleCompany(batchCompanyId);
            }
            return batchCompanyId;
        }

        Long currentCompanyId = companyScopeService.currentCompanyIdOrNull();
        if (currentCompanyId != null) {
            return currentCompanyId;
        }
        return null;
    }

    private Long resolveBatchIdByTraceId(String traceId) {
        if (traceId == null || traceId.isBlank()) {
            return null;
        }
        var productItem = productItemMapper.selectByTraceId(traceId);
        if (productItem != null) {
            return productItem.getBatchId();
        }
        var traceCode = traceCodeMapper.selectByTraceId(traceId);
        return traceCode == null ? null : traceCode.getBatchId();
    }

    private Long createOrRefreshHighRiskTask(UserFeedback feedback, Long suggestedAssigneeUserId) {
        SystemTask existing = systemTaskMapper.selectByUnique("HIGH_RISK_FEEDBACK", "FEEDBACK", feedback.getId());
        if (existing != null) {
            existing.setTitle(buildFeedbackTaskTitle(feedback));
            existing.setDescription(buildFeedbackTaskDescription(feedback));
            existing.setPriority(1);
            existing.setStatus(existing.getStatus() != null && existing.getStatus() == 2 ? 2 : 0);
            existing.setAssigneeUserId(existing.getStatus() != null && existing.getStatus() > 0 ? existing.getAssigneeUserId() : null);
            if (existing.getAssigneeUserId() == null) {
                existing.setClaimedAt(null);
            }
            existing.setSourceType("FEEDBACK_AI");
            existing.setDueAt(LocalDateTime.now().plusHours(12));
            systemTaskMapper.update(existing);
            return existing.getId();
        }

        SystemTask task = new SystemTask();
        task.setTaskType("HIGH_RISK_FEEDBACK");
        task.setBizType("FEEDBACK");
        task.setBizId(feedback.getId());
        task.setTitle(buildFeedbackTaskTitle(feedback));
        task.setDescription(buildFeedbackTaskDescription(feedback));
        task.setPriority(1);
        task.setStatus(0);
        task.setAssigneeUserId(null);
        task.setClaimedAt(null);
        task.setCompletedByUserId(null);
        task.setSourceType("FEEDBACK_AI");
        task.setDueAt(LocalDateTime.now().plusHours(12));
        task.setCompletedAt(null);
        systemTaskMapper.insert(task);
        return task.getId();
    }

    private void syncLinkedTask(UserFeedback feedback) {
        if (feedback.getLinkedTaskId() == null) {
            return;
        }
        SystemTask task = systemTaskMapper.selectById(feedback.getLinkedTaskId());
        if (task == null) {
            return;
        }

        task.setAssigneeUserId(feedback.getAssigneeUserId());
        if (feedback.getStatus() == 2) {
            task.setStatus(2);
            task.setCompletedAt(LocalDateTime.now());
            if (task.getCompletedByUserId() == null) {
                var authUser = AuthContext.get();
                task.setCompletedByUserId(authUser == null ? null : authUser.getUserId());
            }
            if (task.getClaimedAt() == null) {
                task.setClaimedAt(LocalDateTime.now());
            }
        } else if (feedback.getStatus() == 1) {
            task.setStatus(1);
            task.setCompletedAt(null);
            task.setCompletedByUserId(null);
            if (task.getClaimedAt() == null) {
                task.setClaimedAt(LocalDateTime.now());
            }
        } else {
            task.setStatus(0);
            task.setCompletedAt(null);
            task.setCompletedByUserId(null);
        }
        systemTaskMapper.update(task);
    }

    private String buildFeedbackTaskTitle(UserFeedback feedback) {
        return "处理高风险反馈#" + feedback.getId();
    }

    private String buildFeedbackTaskDescription(UserFeedback feedback) {
        String traceId = feedback.getTraceId() == null || feedback.getTraceId().isBlank() ? "未关联溯源码" : feedback.getTraceId();
        return "AI 判定为高风险反馈，请优先处理。分类=" + safe(feedback.getAiCategory())
                + "，溯源码=" + traceId
                + "，内容=" + safe(feedback.getContent());
    }

    private String safe(String value) {
        return value == null ? "-" : value.trim();
    }

    private void applyRecallIfNeeded(UserFeedback feedback, FeedbackRecallRequest recallRequest) {
        if (recallRequest == null || !Boolean.TRUE.equals(recallRequest.getEnabled())) {
            return;
        }
        if (feedback.getLinkedRecallId() != null) {
            return;
        }

        Long batchId = recallRequest.getBatchId();
        if (batchId == null) {
            batchId = userFeedbackMapper.selectBatchIdByFeedbackId(feedback.getId());
        }
        if (batchId == null) {
            throw new BusinessException(400, "当前反馈未关联到批次，无法直接发起召回");
        }

        String reason = recallRequest.getReason();
        if (reason == null || reason.isBlank()) {
            reason = "由反馈#" + feedback.getId() + " 触发召回，" + safe(feedback.getContent());
        }
        RecallRecord recallRecord = recallRecordService.createFromFeedback(batchId, recallRequest.getRecallLevel(), reason);
        feedback.setLinkedRecallId(recallRecord.getId());
    }
}

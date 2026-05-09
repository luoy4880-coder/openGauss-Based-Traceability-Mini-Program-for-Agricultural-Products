package com.yujia.backend.service;

import com.yujia.backend.common.auth.AuthContext;
import com.yujia.backend.common.exception.BusinessException;
import com.yujia.backend.common.response.PageResponse;
import com.yujia.backend.entity.SystemTask;
import com.yujia.backend.mapper.SystemTaskMapper;
import com.yujia.backend.mapper.UserFeedbackMapper;
import com.yujia.backend.vo.SystemTaskVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class SystemTaskService {

    private final ProductBatchService productBatchService;
    private final BatchArchiveService batchArchiveService;
    private final BatchInsightService batchInsightService;
    private final FeedbackService feedbackService;
    private final SystemTaskMapper systemTaskMapper;
    private final UserFeedbackMapper userFeedbackMapper;
    private final CompanyScopeService companyScopeService;

    public PageResponse<SystemTaskVO> page(Integer status, Long assigneeUserId, String keyword, Integer pageNum, Integer pageSize) {
        refreshAutoTasks();
        int safePageNum = pageNum == null || pageNum < 1 ? 1 : pageNum;
        int safePageSize = pageSize == null || pageSize < 1 ? 10 : Math.min(pageSize, 100);
        Long companyId = companyScopeService.currentCompanyScopeOrNull();
        long total = systemTaskMapper.countPage(companyId, status, assigneeUserId, keyword);
        var records = systemTaskMapper.selectPage(
                companyId,
                status,
                assigneeUserId,
                keyword,
                (long) (safePageNum - 1) * safePageSize,
                safePageSize
        );
        return PageResponse.<SystemTaskVO>builder()
                .records(records)
                .total(total)
                .pageNum(safePageNum)
                .pageSize(safePageSize)
                .build();
    }

    public void claim(Long id) {
        SystemTask task = detail(id);
        var authUser = AuthContext.get();
        if (authUser == null) {
            throw new BusinessException(401, "未登录");
        }
        assertCanClaim(task, authUser.getUserId());
        task.setAssigneeUserId(authUser.getUserId());
        task.setClaimedAt(LocalDateTime.now());
        task.setCompletedByUserId(null);
        task.setCompletedAt(null);
        task.setStatus(1);
        systemTaskMapper.update(task);
    }

    public void complete(Long id) {
        SystemTask task = detail(id);
        var authUser = AuthContext.get();
        if (authUser == null) {
            throw new BusinessException(401, "未登录");
        }
        assertCanProcess(task, authUser.getUserId());
        if (task.getAssigneeUserId() == null) {
            task.setAssigneeUserId(authUser.getUserId());
        }
        if (task.getClaimedAt() == null) {
            task.setClaimedAt(LocalDateTime.now());
        }
        task.setCompletedByUserId(authUser.getUserId());
        task.setCompletedAt(LocalDateTime.now());
        task.setStatus(2);
        systemTaskMapper.update(task);
    }

    public void reopen(Long id) {
        SystemTask task = detail(id);
        var authUser = AuthContext.get();
        if (authUser == null) {
            throw new BusinessException(401, "未登录");
        }
        assertCanProcess(task, authUser.getUserId());
        task.setStatus(0);
        task.setCompletedByUserId(null);
        task.setCompletedAt(null);
        systemTaskMapper.update(task);
    }

    public void refreshAutoTasks() {
        productBatchService.list(null, null, null).forEach(batch -> {
            var archive = batchArchiveService.archive(batch.getId());
            var insight = batchInsightService.insight(archive);
            if (archive.getProductionRecords().isEmpty()) {
                upsertTask("MISSING_PRODUCTION", batch.getId(), batch.getBatchCode(),
                        "补录生产记录", "该批次尚未录入生产记录，会影响追溯完整度。", 2, null);
            }
            if (archive.getInspectionReports().isEmpty()) {
                upsertTask("MISSING_INSPECTION", batch.getId(), batch.getBatchCode(),
                        "补上传质检报告", "该批次缺少质检报告，消费者无法查看质量证明。", 1, null);
            }
            if (archive.getLogisticsRecords().isEmpty()) {
                upsertTask("MISSING_LOGISTICS", batch.getId(), batch.getBatchCode(),
                        "补录物流节点", "该批次暂无流通记录，建议至少补齐一个物流节点。", 2, null);
            }
            if (archive.getHighPriorityFeedbackCount() > 0) {
                upsertTask("HIGH_PRIORITY_FEEDBACK", batch.getId(), batch.getBatchCode(),
                        "处理高优反馈", "该批次存在高优先级反馈，需要尽快跟进处理。", 1,
                        feedbackService.pickSuggestedAssigneeUserId());
            }
            if (archive.getAbnormalScanCount() >= 3) {
                upsertTask("ABNORMAL_SCAN", batch.getId(), batch.getBatchCode(),
                        "排查异常扫码", "该批次异常扫码次数偏高，请核验防伪和流通状态。", 1, null);
            }
            if (insight.getCompletenessScore() < 70) {
                upsertTask("LOW_COMPLETENESS", batch.getId(), batch.getBatchCode(),
                        "提升档案完整度", "该批次档案完整度低于 70%，建议优先补充关键材料。", 2, null);
            }
        });
    }

    private void upsertTask(String taskType, Long batchId, String batchCode, String title, String description,
                            int priority, Long suggestedAssigneeUserId) {
        SystemTask existing = systemTaskMapper.selectByUnique(taskType, "BATCH", batchId);
        if (existing == null) {
            SystemTask task = new SystemTask();
            task.setTaskType(taskType);
            task.setBizType("BATCH");
            task.setBizId(batchId);
            task.setTitle(title + " - " + batchCode);
            task.setDescription(description);
            task.setPriority(priority);
            task.setStatus(0);
            task.setAssigneeUserId(suggestedAssigneeUserId);
            task.setClaimedAt(suggestedAssigneeUserId == null ? null : LocalDateTime.now());
            task.setCompletedByUserId(null);
            task.setSourceType("SYSTEM");
            task.setDueAt(LocalDateTime.now().plusDays(priority == 1 ? 1 : 3));
            systemTaskMapper.insert(task);
            return;
        }

        Integer nextStatus = existing.getStatus() != null && existing.getStatus() == 2 ? 2 : 0;
        existing.setTitle(title + " - " + batchCode);
        existing.setDescription(description);
        existing.setPriority(priority);
        existing.setStatus(nextStatus);
        if (nextStatus == 0 && existing.getAssigneeUserId() == null) {
            existing.setAssigneeUserId(suggestedAssigneeUserId);
            if (suggestedAssigneeUserId != null && existing.getClaimedAt() == null) {
                existing.setClaimedAt(LocalDateTime.now());
            }
        }
        existing.setSourceType("SYSTEM");
        existing.setDueAt(LocalDateTime.now().plusDays(priority == 1 ? 1 : 3));
        systemTaskMapper.update(existing);
    }

    private SystemTask detail(Long id) {
        SystemTask task = systemTaskMapper.selectById(id);
        if (task == null) {
            throw new BusinessException(404, "任务不存在");
        }
        if ("BATCH".equalsIgnoreCase(task.getBizType())) {
            companyScopeService.assertAccessibleCompany(productBatchService.detail(task.getBizId()).getCompanyId());
        } else if ("FEEDBACK".equalsIgnoreCase(task.getBizType())) {
            var feedback = userFeedbackMapper.selectById(task.getBizId());
            if (feedback == null) {
                throw new BusinessException(404, "任务关联的反馈不存在");
            }
            companyScopeService.assertAccessibleCompany(feedback.getCompanyId());
        }
        return task;
    }

    private void assertCanClaim(SystemTask task, Long currentUserId) {
        if (companyScopeService.isAdmin()) {
            return;
        }
        if (task.getAssigneeUserId() != null && !task.getAssigneeUserId().equals(currentUserId)) {
            throw new BusinessException(403, "该任务已分派给其他人员，不能直接认领");
        }
    }

    private void assertCanProcess(SystemTask task, Long currentUserId) {
        if (companyScopeService.isAdmin()) {
            return;
        }
        if (task.getAssigneeUserId() != null && !task.getAssigneeUserId().equals(currentUserId)) {
            throw new BusinessException(403, "只能处理分派给自己的任务");
        }
    }
}

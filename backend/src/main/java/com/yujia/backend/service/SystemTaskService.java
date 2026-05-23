package com.yujia.backend.service;

import com.yujia.backend.common.auth.AuthContext;
import com.yujia.backend.common.exception.BusinessException;
import com.yujia.backend.common.response.PageResponse;
import com.yujia.backend.entity.InspectionReport;
import com.yujia.backend.entity.SystemTask;
import com.yujia.backend.entity.UserFeedback;
import com.yujia.backend.mapper.InspectionReportMapper;
import com.yujia.backend.mapper.SystemTaskMapper;
import com.yujia.backend.mapper.UserFeedbackMapper;
import com.yujia.backend.vo.SystemTaskVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class SystemTaskService {

    private static final int STATUS_PENDING = 0;
    private static final int STATUS_PROCESSING = 1;
    private static final int STATUS_COMPLETED = 2;
    private static final int STATUS_INVALID = 3;

    private final ProductBatchService productBatchService;
    private final BatchArchiveService batchArchiveService;
    private final BatchInsightService batchInsightService;
    private final FeedbackService feedbackService;
    private final SystemTaskMapper systemTaskMapper;
    private final UserFeedbackMapper userFeedbackMapper;
    private final InspectionReportMapper inspectionReportMapper;
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
        ensureTaskActive(task);
        var authUser = AuthContext.get();
        if (authUser == null) {
            throw new BusinessException(401, "未登录");
        }
        assertCanClaim(task, authUser.getUserId());
        task.setAssigneeUserId(authUser.getUserId());
        task.setClaimedAt(LocalDateTime.now());
        task.setCompletedByUserId(null);
        task.setCompletedAt(null);
        task.setStatus(STATUS_PROCESSING);
        systemTaskMapper.update(task);
    }

    public void complete(Long id) {
        SystemTask task = detail(id);
        ensureTaskActive(task);
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
        task.setStatus(STATUS_COMPLETED);
        systemTaskMapper.update(task);
    }

    public void reopen(Long id) {
        SystemTask task = detail(id);
        if (task.getStatus() != null && task.getStatus() == STATUS_INVALID) {
            throw new BusinessException(400, "已失效任务不能重新打开");
        }
        var authUser = AuthContext.get();
        if (authUser == null) {
            throw new BusinessException(401, "未登录");
        }
        assertCanProcess(task, authUser.getUserId());
        task.setStatus(STATUS_PENDING);
        task.setCompletedByUserId(null);
        task.setCompletedAt(null);
        systemTaskMapper.update(task);
    }

    public void refreshAutoTasks() {
        Long companyId = companyScopeService.currentCompanyScopeOrNull();
        Set<String> activeBatchTaskKeys = new HashSet<>();

        productBatchService.list(null, null, null).forEach(batch -> {
            var archive = batchArchiveService.archive(batch.getId());
            var insight = batchInsightService.insight(archive);
            if (archive.getProductionRecords().isEmpty()) {
                upsertTask(activeBatchTaskKeys, "MISSING_PRODUCTION", batch.getId(), batch.getBatchCode(),
                        "补录生产记录", "该批次尚未录入生产记录，会影响追溯完整度。", 2, null);
            }
            if (archive.getInspectionReports().isEmpty()) {
                upsertTask(activeBatchTaskKeys, "MISSING_INSPECTION", batch.getId(), batch.getBatchCode(),
                        "补上传质检报告", "该批次缺少质检报告，消费者无法查看质量证明。", 1, null);
            }
            if (archive.getLogisticsRecords().isEmpty()) {
                upsertTask(activeBatchTaskKeys, "MISSING_LOGISTICS", batch.getId(), batch.getBatchCode(),
                        "补录物流节点", "该批次暂无流通记录，建议至少补齐一个物流节点。", 2, null);
            }
            if (archive.getHighPriorityFeedbackCount() > 0) {
                upsertTask(activeBatchTaskKeys, "HIGH_PRIORITY_FEEDBACK", batch.getId(), batch.getBatchCode(),
                        "处理高优反馈", "该批次存在高优先级反馈，需要尽快跟进处理。", 1,
                        feedbackService.pickSuggestedAssigneeUserId());
            }
            if (archive.getAbnormalScanCount() >= 3) {
                upsertTask(activeBatchTaskKeys, "ABNORMAL_SCAN", batch.getId(), batch.getBatchCode(),
                        "排查异常扫码", "该批次异常扫码次数偏高，请核验防伪和流通状态。", 1, null);
            }
            if (insight.getCompletenessScore() < 70) {
                upsertTask(activeBatchTaskKeys, "LOW_COMPLETENESS", batch.getId(), batch.getBatchCode(),
                        "提升档案完整度", "该批次档案完整度低于 70%，建议优先补充关键信息。", 2, null);
            }
        });

        invalidateResolvedAutoBatchTasks(companyId, activeBatchTaskKeys);
        reconcileBusinessTasks(companyId);
    }

    private void upsertTask(Set<String> activeBatchTaskKeys, String taskType, Long batchId, String batchCode, String title,
                            String description, int priority, Long suggestedAssigneeUserId) {
        activeBatchTaskKeys.add(buildTaskKey(taskType, batchId));
        SystemTask existing = systemTaskMapper.selectByUnique(taskType, "BATCH", batchId);
        if (existing == null) {
            SystemTask task = new SystemTask();
            task.setTaskType(taskType);
            task.setBizType("BATCH");
            task.setBizId(batchId);
            task.setTitle(title + " - " + batchCode);
            task.setDescription(description);
            task.setPriority(priority);
            task.setStatus(STATUS_PENDING);
            task.setAssigneeUserId(suggestedAssigneeUserId);
            task.setClaimedAt(suggestedAssigneeUserId == null ? null : LocalDateTime.now());
            task.setCompletedByUserId(null);
            task.setSourceType("SYSTEM");
            task.setDueAt(LocalDateTime.now().plusDays(priority == 1 ? 1 : 3));
            task.setCompletedAt(null);
            systemTaskMapper.insert(task);
            return;
        }

        int nextStatus = existing.getStatus() != null && existing.getStatus() == STATUS_COMPLETED
                ? STATUS_COMPLETED
                : existing.getStatus() != null && existing.getStatus() == STATUS_PROCESSING
                ? STATUS_PROCESSING
                : STATUS_PENDING;
        existing.setTitle(title + " - " + batchCode);
        existing.setDescription(description);
        existing.setPriority(priority);
        existing.setStatus(nextStatus);
        if (nextStatus == STATUS_PENDING && existing.getAssigneeUserId() == null) {
            existing.setAssigneeUserId(suggestedAssigneeUserId);
            if (suggestedAssigneeUserId != null && existing.getClaimedAt() == null) {
                existing.setClaimedAt(LocalDateTime.now());
            }
        }
        existing.setSourceType("SYSTEM");
        existing.setDueAt(LocalDateTime.now().plusDays(priority == 1 ? 1 : 3));
        if (nextStatus != STATUS_COMPLETED) {
            existing.setCompletedByUserId(null);
            existing.setCompletedAt(null);
        }
        systemTaskMapper.update(existing);
    }

    private void invalidateResolvedAutoBatchTasks(Long companyId, Set<String> activeBatchTaskKeys) {
        systemTaskMapper.selectByBizTypeAndSourceType(companyId, "BATCH", "SYSTEM").forEach(task -> {
            if (task.getBizId() == null) {
                return;
            }
            if (activeBatchTaskKeys.contains(buildTaskKey(task.getTaskType(), task.getBizId()))) {
                return;
            }
            if (task.getStatus() != null && task.getStatus() == STATUS_INVALID) {
                return;
            }
            task.setStatus(STATUS_INVALID);
            task.setCompletedByUserId(null);
            task.setCompletedAt(null);
            systemTaskMapper.update(task);
        });
    }

    private void reconcileBusinessTasks(Long companyId) {
        reconcileFeedbackTasks(companyId);
        reconcileBatchRiskTasks(companyId, "IMPORT_RISK_REVIEW");
        reconcileBatchRiskTasks(companyId, "REPORT_RISK_REVIEW");
    }

    private void reconcileFeedbackTasks(Long companyId) {
        systemTaskMapper.selectByTaskType(companyId, "HIGH_RISK_FEEDBACK").forEach(task -> {
            if (task.getBizId() == null) {
                invalidateTask(task);
                return;
            }

            UserFeedback feedback = userFeedbackMapper.selectById(task.getBizId());
            if (feedback == null) {
                invalidateTask(task);
                return;
            }

            task.setAssigneeUserId(feedback.getAssigneeUserId());
            if (feedback.getStatus() != null && feedback.getStatus() == STATUS_COMPLETED) {
                completeTaskFromBusiness(task);
            } else if (feedback.getStatus() != null && feedback.getStatus() == STATUS_PROCESSING) {
                activateTask(task, STATUS_PROCESSING);
            } else {
                activateTask(task, STATUS_PENDING);
            }
        });
    }

    private void reconcileBatchRiskTasks(Long companyId, String taskType) {
        systemTaskMapper.selectByTaskType(companyId, taskType).forEach(task -> {
            if (task.getBizId() == null) {
                invalidateTask(task);
                return;
            }

            List<InspectionReport> reports = inspectionReportMapper.selectList(null, task.getBizId(), null);
            if (reports.isEmpty()) {
                invalidateTask(task);
                return;
            }

            InspectionReport latestReport = reports.get(0);
            boolean resolved = latestReport.getResultStatus() != null && latestReport.getResultStatus() == 1;
            if (resolved) {
                if (task.getStatus() == null || task.getStatus() != STATUS_COMPLETED) {
                    invalidateTask(task);
                }
                return;
            }

            task.setTitle(buildBatchRiskTaskTitle(taskType, latestReport.getReportNo()));
            task.setDescription(buildBatchRiskTaskDescription(taskType, latestReport));
            task.setPriority(1);
            task.setSourceType("IMPORT_RISK_REVIEW".equals(taskType) ? "QUICK_IMPORT" : "REPORT_IMPORT");
            task.setDueAt(LocalDateTime.now().plusHours(12));
            activateTask(task, task.getStatus() != null && task.getStatus() == STATUS_PROCESSING
                    ? STATUS_PROCESSING
                    : STATUS_PENDING);
        });
    }

    private String buildBatchRiskTaskTitle(String taskType, String reportNo) {
        String safeReportNo = reportNo == null || reportNo.isBlank() ? "-" : reportNo.trim();
        if ("IMPORT_RISK_REVIEW".equals(taskType)) {
            return "处理异常导入批次 - " + safeReportNo;
        }
        return "复核异常质检报告 - " + safeReportNo;
    }

    private String buildBatchRiskTaskDescription(String taskType, InspectionReport report) {
        String conclusion = report.getConclusion() == null || report.getConclusion().isBlank()
                ? "检测结果异常，请尽快复核。"
                : report.getConclusion().trim();
        if ("IMPORT_RISK_REVIEW".equals(taskType)) {
            return "快速导入的批次质检结果异常，请优先复核。报告编号="
                    + safeText(report.getReportNo()) + "；结论：" + conclusion;
        }
        return conclusion;
    }

    private String safeText(String value) {
        return value == null || value.isBlank() ? "-" : value.trim();
    }

    private void completeTaskFromBusiness(SystemTask task) {
        task.setStatus(STATUS_COMPLETED);
        if (task.getClaimedAt() == null && task.getAssigneeUserId() != null) {
            task.setClaimedAt(LocalDateTime.now());
        }
        if (task.getCompletedAt() == null) {
            task.setCompletedAt(LocalDateTime.now());
        }
        systemTaskMapper.update(task);
    }

    private void activateTask(SystemTask task, int nextStatus) {
        task.setStatus(nextStatus);
        task.setCompletedByUserId(null);
        task.setCompletedAt(null);
        if (nextStatus == STATUS_PENDING) {
            task.setClaimedAt(task.getAssigneeUserId() == null ? null : coalesceClaimedAt(task.getClaimedAt()));
        } else if (task.getClaimedAt() == null) {
            task.setClaimedAt(LocalDateTime.now());
        }
        systemTaskMapper.update(task);
    }

    private LocalDateTime coalesceClaimedAt(LocalDateTime claimedAt) {
        return claimedAt == null ? LocalDateTime.now() : claimedAt;
    }

    private void invalidateTask(SystemTask task) {
        if (task.getStatus() != null && task.getStatus() == STATUS_INVALID) {
            return;
        }
        task.setStatus(STATUS_INVALID);
        task.setCompletedByUserId(null);
        task.setCompletedAt(null);
        systemTaskMapper.update(task);
    }

    private String buildTaskKey(String taskType, Long bizId) {
        return taskType + ":" + bizId;
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

    private void ensureTaskActive(SystemTask task) {
        if (task.getStatus() != null && task.getStatus() == STATUS_INVALID) {
            throw new BusinessException(400, "该任务已失效");
        }
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

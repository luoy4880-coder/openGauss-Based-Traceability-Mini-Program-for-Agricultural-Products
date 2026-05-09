package com.yujia.backend.service;

import com.yujia.backend.mapper.ScanLogMapper;
import com.yujia.backend.mapper.SystemTaskMapper;
import com.yujia.backend.mapper.UserFeedbackMapper;
import com.yujia.backend.vo.RiskBatchVO;
import com.yujia.backend.vo.RiskOverviewVO;
import com.yujia.backend.vo.RiskSourceVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RiskOverviewService {

    private final ProductBatchService productBatchService;
    private final BatchArchiveService batchArchiveService;
    private final BatchInsightService batchInsightService;
    private final SystemTaskMapper systemTaskMapper;
    private final ScanLogMapper scanLogMapper;
    private final UserFeedbackMapper userFeedbackMapper;
    private final CompanyScopeService companyScopeService;

    public RiskOverviewVO overview() {
        Long companyId = companyScopeService.currentCompanyScopeOrNull();

        List<BatchRiskHolder> batchRiskHolders = productBatchService.list(null, null, null).stream()
                .map(batch -> {
                    var archive = batchArchiveService.archive(batch.getId());
                    var insight = batchInsightService.insight(archive);
                    RiskBatchVO vo = new RiskBatchVO();
                    vo.setBatchId(batch.getId());
                    vo.setBatchCode(batch.getBatchCode());
                    vo.setProductName(batch.getProductName());
                    vo.setBaseName(batch.getBaseName());
                    vo.setCompletenessScore(insight.getCompletenessScore());
                    vo.setRiskScore(insight.getRiskScore());
                    vo.setRiskLevel(insight.getRiskLevel());
                    vo.setSummary(insight.getAiSummary());
                    return new BatchRiskHolder(vo, archive.getHighPriorityFeedbackCount());
                })
                .sorted(Comparator.<BatchRiskHolder>comparingInt(holder -> holder.risk().getRiskScore()).reversed())
                .toList();

        List<RiskBatchVO> batchRisks = batchRiskHolders.stream()
                .map(BatchRiskHolder::risk)
                .toList();

        int avgCompleteness = batchRisks.isEmpty() ? 0
                : (int) Math.round(batchRisks.stream()
                        .mapToInt(RiskBatchVO::getCompletenessScore)
                        .average()
                        .orElse(0));

        List<RiskBatchVO> lowCompleteness = batchRisks.stream()
                .filter(batch -> batch.getCompletenessScore() < 70)
                .sorted(Comparator.comparingInt(RiskBatchVO::getCompletenessScore))
                .limit(8)
                .toList();

        List<RiskBatchVO> abnormalScan = productBatchService.list(null, null, null).stream()
                .map(batch -> {
                    int abnormalCount = (int) scanLogMapper.countAbnormalByBatchId(batch.getId());
                    if (abnormalCount <= 0) {
                        return null;
                    }
                    RiskBatchVO vo = new RiskBatchVO();
                    vo.setBatchId(batch.getId());
                    vo.setBatchCode(batch.getBatchCode());
                    vo.setProductName(batch.getProductName());
                    vo.setBaseName(batch.getBaseName());
                    vo.setCompletenessScore(0);
                    vo.setRiskScore(abnormalCount);
                    vo.setRiskLevel(abnormalCount >= 5 ? "HIGH" : abnormalCount >= 3 ? "MEDIUM" : "LOW");
                    vo.setSummary("异常扫码 " + abnormalCount + " 次");
                    return vo;
                })
                .filter(Objects::nonNull)
                .sorted(Comparator.comparingInt(RiskBatchVO::getRiskScore).reversed())
                .limit(8)
                .toList();

        RiskOverviewVO overview = new RiskOverviewVO();
        overview.setOpenTaskCount((int) systemTaskMapper.countOpenTasks(companyId));
        overview.setHighRiskBatchCount((int) batchRisks.stream().filter(batch -> batch.getRiskScore() >= 70).count());
        overview.setLowCompletenessBatchCount(
                (int) batchRisks.stream().filter(batch -> batch.getCompletenessScore() < 70).count());
        overview.setAverageCompletenessScore(avgCompleteness);
        overview.setAbnormalScanCount((int) scanLogMapper.countAllAbnormal(companyId));
        overview.setPendingFeedbackCount((int) userFeedbackMapper.countPending(companyId));
        overview.setHighPriorityFeedbackBatchCount((int) batchRiskHolders.stream()
                .filter(holder -> holder.highPriorityFeedbackCount() > 0)
                .count());
        overview.setTopRiskBatches(batchRisks.stream().limit(8).toList());
        overview.setTopAbnormalScanBatches(abnormalScan);
        overview.setLowCompletenessBatches(lowCompleteness);
        overview.setRiskSources(List.of(
                new RiskSourceVO("缺质检", (int) systemTaskMapper.countOpenByTaskType(companyId, "MISSING_INSPECTION")),
                new RiskSourceVO("缺物流", (int) systemTaskMapper.countOpenByTaskType(companyId, "MISSING_LOGISTICS")),
                new RiskSourceVO("高优反馈", (int) systemTaskMapper.countOpenByTaskType(companyId, "HIGH_PRIORITY_FEEDBACK")),
                new RiskSourceVO("异常扫码", (int) systemTaskMapper.countOpenByTaskType(companyId, "ABNORMAL_SCAN"))));
        return overview;
    }

    private record BatchRiskHolder(RiskBatchVO risk, int highPriorityFeedbackCount) {
    }
}

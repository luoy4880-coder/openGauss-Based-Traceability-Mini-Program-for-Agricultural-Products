package com.yujia.backend.vo;

import lombok.Data;

import java.util.List;

@Data
public class RiskOverviewVO {

    private int openTaskCount;
    private int highRiskBatchCount;
    private int lowCompletenessBatchCount;
    private int averageCompletenessScore;
    private int abnormalScanCount;
    private int pendingFeedbackCount;
    private int highPriorityFeedbackBatchCount;
    private List<RiskBatchVO> topRiskBatches;
    private List<RiskBatchVO> topAbnormalScanBatches;
    private List<RiskBatchVO> lowCompletenessBatches;
    private List<RiskSourceVO> riskSources;
}

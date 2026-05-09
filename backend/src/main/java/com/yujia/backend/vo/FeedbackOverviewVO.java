package com.yujia.backend.vo;

import lombok.Data;

import java.util.List;

@Data
public class FeedbackOverviewVO {

    private long pendingCount;
    private long highRiskPendingCount;
    private long urgentPendingCount;
    private List<FeedbackTaskVO> latestHighRiskRecords;
}

package com.yujia.backend.vo;

import lombok.Data;

import java.util.List;

@Data
public class BatchInsightVO {

    private Long batchId;
    private int completenessScore;
    private int riskScore;
    private String riskLevel;
    private List<String> missingItems;
    private List<String> warnings;
    private List<String> nextActions;
    private String aiSummary;
}

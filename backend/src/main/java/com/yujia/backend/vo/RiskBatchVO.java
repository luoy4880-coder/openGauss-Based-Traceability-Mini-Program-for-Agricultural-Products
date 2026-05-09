package com.yujia.backend.vo;

import lombok.Data;

@Data
public class RiskBatchVO {

    private Long batchId;
    private String batchCode;
    private String productName;
    private String baseName;
    private int completenessScore;
    private int riskScore;
    private String riskLevel;
    private String summary;
}

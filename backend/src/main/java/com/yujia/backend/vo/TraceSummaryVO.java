package com.yujia.backend.vo;

import lombok.Data;

import java.util.List;

@Data
public class TraceSummaryVO {

    private String summaryTitle;
    private String summaryText;
    private String safetyLevel;
    private int trustScore;
    private List<String> highlights;
    private List<String> riskTips;
    private String qualityInterpretation;
    private List<String> actionTips;
}

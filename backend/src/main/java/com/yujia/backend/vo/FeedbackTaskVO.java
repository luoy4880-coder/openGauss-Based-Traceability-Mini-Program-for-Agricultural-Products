package com.yujia.backend.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class FeedbackTaskVO {

    private Long id;
    private Long userId;
    private String type;
    private String content;
    private String contact;
    private String traceId;
    private Long batchId;

    private String aiCategory;
    private Integer aiPriority;
    private String riskLevel;
    private Integer urgentFlag;
    private String aiSummary;

    private Integer status;
    private Long assigneeUserId;
    private String assigneeName;
    private Long linkedTaskId;
    private Long linkedRecallId;
    private String handleNote;

    private LocalDateTime createdAt;
    private LocalDateTime handledAt;
    private LocalDateTime updatedAt;
}

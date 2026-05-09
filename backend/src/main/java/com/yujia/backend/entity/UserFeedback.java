package com.yujia.backend.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserFeedback {

    private Long id;

    private Long userId;

    private String type;

    private String content;

    private String contact;

    private String traceId;

    private Long batchId;

    private Long companyId;

    private String aiCategory;

    private Integer aiPriority;

    private String riskLevel;

    private Integer urgentFlag;

    private String aiSummary;

    private Long assigneeUserId;

    private Long linkedTaskId;

    private Long linkedRecallId;

    private String handleNote;

    private Integer status;

    private LocalDateTime createdAt;

    private LocalDateTime handledAt;

    private LocalDateTime updatedAt;
}

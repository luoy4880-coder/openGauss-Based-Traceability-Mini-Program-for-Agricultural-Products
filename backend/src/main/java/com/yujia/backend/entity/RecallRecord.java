package com.yujia.backend.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RecallRecord {

    private Long id;

    private Long batchId;

    private Integer recallLevel;

    private String reason;

    private Integer recallStatus;

    private LocalDateTime noticeTime;

    private LocalDateTime closedAt;

    private LocalDateTime createdAt;
}

package com.yujia.backend.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SystemTaskVO {

    private Long id;
    private String taskType;
    private String bizType;
    private Long bizId;
    private String title;
    private String description;
    private Integer priority;
    private Integer status;
    private Long assigneeUserId;
    private String assigneeName;
    private LocalDateTime claimedAt;
    private Long completedByUserId;
    private String completedByName;
    private String sourceType;
    private LocalDateTime dueAt;
    private LocalDateTime completedAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

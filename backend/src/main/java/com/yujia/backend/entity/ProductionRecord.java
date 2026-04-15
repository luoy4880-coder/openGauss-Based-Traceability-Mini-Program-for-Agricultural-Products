package com.yujia.backend.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProductionRecord {

    private Long id;

    private Long batchId;

    private String recordType;

    private LocalDateTime operationTime;

    private String operatorName;

    private String materialName;

    private String dosage;

    private String content;

    private String attachmentUrl;

    private LocalDateTime createdAt;
}

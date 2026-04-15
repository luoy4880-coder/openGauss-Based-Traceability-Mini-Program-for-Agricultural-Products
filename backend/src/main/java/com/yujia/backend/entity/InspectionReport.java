package com.yujia.backend.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InspectionReport {

    private Long id;

    private Long batchId;

    private String reportNo;

    private String agencyName;

    private String inspectorName;

    private LocalDateTime inspectionTime;

    private Integer resultStatus;

    private String conclusion;

    private String reportUrl;

    private LocalDateTime createdAt;
}

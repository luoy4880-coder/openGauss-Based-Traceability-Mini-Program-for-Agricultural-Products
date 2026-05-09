package com.yujia.backend.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LogisticsRecord {

    private Long id;

    private Long batchId;
    private Long itemId;
    private String logisticsCode;
    private String nodeType;
    private String nodeName;
    private LocalDateTime operationTime;
    private String operatorName;
    private String contactPhone;
    private String location;
    private String temperature;
    private String humidity;
    private String attachmentUrl;
    private String remark;
    private LocalDateTime createdAt;
}

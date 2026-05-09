package com.yujia.backend.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ScanLog {

    private Long id;

    private String traceId;

    private Long itemId;

    private Long batchId;

    private String scanSource;

    private String ipAddress;

    private String userAgent;

    private Integer verifyResult;

    private String riskMessage;

    private LocalDateTime scannedAt;
}

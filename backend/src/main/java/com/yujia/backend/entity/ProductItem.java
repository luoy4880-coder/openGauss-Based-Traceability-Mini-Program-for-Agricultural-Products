package com.yujia.backend.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProductItem {

    private Long id;

    private Long batchId;

    private String itemCode;

    private String traceId;

    private String qrContent;

    private String signValue;

    private Integer itemStatus;

    private Integer scanCount;

    private LocalDateTime firstScannedAt;

    private LocalDateTime lastScannedAt;

    private LocalDateTime generatedAt;

    private LocalDateTime updatedAt;
}

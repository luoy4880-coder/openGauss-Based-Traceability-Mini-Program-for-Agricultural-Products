package com.yujia.backend.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TraceCode {

    private Long id;

    private String traceId;

    private Long batchId;

    private String qrContent;

    private String signValue;

    private Integer codeStatus;

    private LocalDateTime generatedAt;
}

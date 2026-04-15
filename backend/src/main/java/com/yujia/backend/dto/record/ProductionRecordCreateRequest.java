package com.yujia.backend.dto.record;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProductionRecordCreateRequest {

    @NotNull(message = "批次ID不能为空")
    private Long batchId;

    @NotBlank(message = "记录类型不能为空")
    private String recordType;

    @NotNull(message = "操作时间不能为空")
    private LocalDateTime operationTime;

    private String operatorName;

    private String materialName;

    private String dosage;

    @NotBlank(message = "记录内容不能为空")
    private String content;

    private String attachmentUrl;
}

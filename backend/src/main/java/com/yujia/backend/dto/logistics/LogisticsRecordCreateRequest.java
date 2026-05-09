package com.yujia.backend.dto.logistics;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class LogisticsRecordCreateRequest {

    @NotNull(message = "批次ID不能为空")
    private Long batchId;

    private Long itemId;

    @NotBlank(message = "节点类型不能为空")
    private String nodeType;

    @NotBlank(message = "节点名称不能为空")
    private String nodeName;

    private LocalDateTime operationTime;

    private String operatorName;

    private String contactPhone;

    private String location;

    private String temperature;

    private String humidity;

    private String attachmentUrl;

    private String remark;
}

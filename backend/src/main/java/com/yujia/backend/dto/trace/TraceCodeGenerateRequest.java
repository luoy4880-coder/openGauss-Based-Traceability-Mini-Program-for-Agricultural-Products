package com.yujia.backend.dto.trace;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TraceCodeGenerateRequest {

    @NotNull(message = "批次ID不能为空")
    private Long batchId;
}

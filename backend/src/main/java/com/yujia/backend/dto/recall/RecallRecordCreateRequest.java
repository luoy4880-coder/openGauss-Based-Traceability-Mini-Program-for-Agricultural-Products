package com.yujia.backend.dto.recall;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class RecallRecordCreateRequest {

    @NotNull(message = "批次ID不能为空")
    private Long batchId;

    @NotNull(message = "召回级别不能为空")
    private Integer recallLevel;

    @NotBlank(message = "召回原因不能为空")
    private String reason;
}

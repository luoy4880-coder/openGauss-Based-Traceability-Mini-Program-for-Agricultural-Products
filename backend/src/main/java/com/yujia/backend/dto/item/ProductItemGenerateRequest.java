package com.yujia.backend.dto.item;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ProductItemGenerateRequest {

    @NotNull(message = "批次ID不能为空")
    private Long batchId;

    @NotNull(message = "生成数量不能为空")
    @Min(value = 1, message = "生成数量不能小于1")
    @Max(value = 1000, message = "单次最多生成1000个单品码")
    private Integer quantity;
}

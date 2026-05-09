package com.yujia.backend.dto.batch;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class ProductBatchCreateRequest {

    private String batchCode;

    @NotNull(message = "基地ID不能为空")
    private Long baseId;

    @NotBlank(message = "产品名称不能为空")
    private String productName;

    private String productCategory;
    private LocalDate plantingDate;
    private LocalDate expectedHarvestDate;
    private LocalDate actualHarvestDate;
    private BigDecimal quantity;
    private String unit;

    @NotNull(message = "批次状态不能为空")
    private Integer batchStatus;

    private String remark;
}

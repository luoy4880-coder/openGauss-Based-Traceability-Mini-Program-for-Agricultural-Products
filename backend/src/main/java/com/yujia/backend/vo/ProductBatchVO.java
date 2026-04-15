package com.yujia.backend.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class ProductBatchVO {

    private Long id;

    private String batchCode;

    private Long baseId;

    private String baseName;

    private String productName;

    private String productCategory;

    private LocalDate plantingDate;

    private LocalDate expectedHarvestDate;

    private LocalDate actualHarvestDate;

    private BigDecimal quantity;

    private String unit;

    private Integer batchStatus;

    private Integer recallStatus;

    private String remark;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}

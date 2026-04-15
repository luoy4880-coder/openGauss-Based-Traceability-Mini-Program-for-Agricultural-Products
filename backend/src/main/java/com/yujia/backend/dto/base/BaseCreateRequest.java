package com.yujia.backend.dto.base;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class BaseCreateRequest {

    @NotBlank(message = "基地编码不能为空")
    private String baseCode;

    @NotBlank(message = "基地名称不能为空")
    private String baseName;

    private String managerName;

    private String contactPhone;

    private String province;

    private String city;

    private String district;

    private String address;

    private BigDecimal acreage;

    @NotNull(message = "基地状态不能为空")
    private Integer status;
}

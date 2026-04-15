package com.yujia.backend.entity;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class BaseInfo {

    private Long id;

    private String baseCode;

    private String baseName;

    private String managerName;

    private String contactPhone;

    private String province;

    private String city;

    private String district;

    private String address;

    private BigDecimal acreage;

    private Integer status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}

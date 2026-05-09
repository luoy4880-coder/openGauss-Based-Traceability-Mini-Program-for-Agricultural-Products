package com.yujia.backend.vo;

import lombok.Data;

@Data
public class CompanyVO {
    private Long id;
    private String companyCode;
    private String companyName;
    private Integer status;
}

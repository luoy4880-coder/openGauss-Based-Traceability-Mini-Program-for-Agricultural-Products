package com.yujia.backend.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class Company {

    private Long id;
    private String companyCode;
    private String companyName;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

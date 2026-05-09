package com.yujia.backend.entity;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class SysUser {

    private Long id;
    private String username;
    private String password;
    private String realName;
    private String phone;
    private String openid;
    private Long companyId;
    private String companyName;
    private Integer status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

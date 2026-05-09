package com.yujia.backend.common.auth;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AuthUser {

    private Long userId;
    private String username;
    private String realName;
    private Long companyId;
    private String companyName;
    private List<String> roleCodes;
}

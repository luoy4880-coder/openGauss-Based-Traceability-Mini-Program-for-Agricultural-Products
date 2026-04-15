package com.yujia.backend.common.auth;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AuthUser {

    private Long userId;
    private String username;
    private List<String> roleCodes;
}

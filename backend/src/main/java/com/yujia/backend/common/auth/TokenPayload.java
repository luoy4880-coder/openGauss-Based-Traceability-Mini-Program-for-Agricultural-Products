package com.yujia.backend.common.auth;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class TokenPayload {

    private Long userId;
    private String username;
    private long expiresAt;
}

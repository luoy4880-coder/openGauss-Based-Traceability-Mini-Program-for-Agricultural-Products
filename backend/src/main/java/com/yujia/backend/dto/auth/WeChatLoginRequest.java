package com.yujia.backend.dto.auth;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class WeChatLoginRequest {
    @NotBlank(message = "code不能为空")
    private String code;
}
package com.yujia.backend.dto.user;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserPasswordUpdateRequest {

    @NotBlank(message = "新密码不能为空")
    private String newPassword;
}

package com.yujia.backend.dto.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

@Data
public class UserUpdateRequest {

    @NotBlank(message = "姓名不能为空")
    private String realName;

    private String phone;

    @NotNull(message = "状态不能为空")
    private Integer status;

    @NotEmpty(message = "角色不能为空")
    private List<Long> roleIds;
}

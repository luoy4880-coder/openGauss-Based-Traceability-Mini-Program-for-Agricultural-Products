package com.yujia.backend.dto.ai;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class UserAiChatRequest {

    @NotBlank(message = "问题不能为空")
    private String question;

    @NotBlank(message = "traceId不能为空")
    private String traceId;

    private String sign;
}

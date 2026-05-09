package com.yujia.backend.dto.feedback;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class FeedbackHandleRequest {

    @NotNull(message = "status不能为空")
    private Integer status;

    private Long assigneeUserId;

    private String handleNote;

    private FeedbackRecallRequest recall;
}

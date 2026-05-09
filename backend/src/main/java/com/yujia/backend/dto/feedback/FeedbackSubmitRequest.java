package com.yujia.backend.dto.feedback;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class FeedbackSubmitRequest {

    @NotBlank(message = "type不能为空")
    private String type;

    @NotBlank(message = "content不能为空")
    private String content;

    private String contact;

    private String traceId;

    private Long batchId;
}

package com.yujia.backend.dto.feedback;

import lombok.Data;

@Data
public class FeedbackRecallRequest {

    private Boolean enabled;

    private Long batchId;

    private Integer recallLevel;

    private String reason;
}

package com.yujia.backend.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AiChatReferenceVO {

    private String type;

    private String label;

    private Long batchId;
}

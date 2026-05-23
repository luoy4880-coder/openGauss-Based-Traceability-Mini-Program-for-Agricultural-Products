package com.yujia.backend.vo;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class AiChatAnswerVO {

    private String mode;

    private String contextTitle;

    private String answer;

    private List<AiChatReferenceVO> references;
}

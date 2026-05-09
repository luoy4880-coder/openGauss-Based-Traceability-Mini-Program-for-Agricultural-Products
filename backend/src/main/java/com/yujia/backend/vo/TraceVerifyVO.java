package com.yujia.backend.vo;

import lombok.Data;

@Data
public class TraceVerifyVO {

    private boolean valid;
    private boolean firstScan;
    private boolean abnormal;
    private Integer scanCount;
    private String verifyMessage;
    private String riskMessage;
}

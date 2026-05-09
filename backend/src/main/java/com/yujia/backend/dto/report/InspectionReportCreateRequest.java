package com.yujia.backend.dto.report;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class InspectionReportCreateRequest {

    @NotNull(message = "批次ID不能为空")
    private Long batchId;

    private String reportNo;

    @NotBlank(message = "检测机构不能为空")
    private String agencyName;

    private String inspectorName;

    @NotNull(message = "检测时间不能为空")
    private LocalDateTime inspectionTime;

    @NotNull(message = "检测结果不能为空")
    private Integer resultStatus;

    private String conclusion;
    private String reportUrl;
}
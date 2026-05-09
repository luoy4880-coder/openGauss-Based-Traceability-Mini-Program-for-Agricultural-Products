package com.yujia.backend.vo;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CropInfoImportVO {

    private Long baseId;
    private String baseCode;
    private Long batchId;
    private String batchCode;
    private int productionRecordCount;
    private int logisticsRecordCount;
    private int itemCount;
    private Long inspectionReportId;
    private String inspectionReportNo;
    private boolean createdRiskTask;
}

package com.yujia.backend.vo;

import com.yujia.backend.entity.InspectionReport;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class InspectionReportImportVO {

    private InspectionReport report;
    private int generatedProductionRecordCount;
    private int generatedLogisticsRecordCount;
    private boolean createdRiskTask;
}

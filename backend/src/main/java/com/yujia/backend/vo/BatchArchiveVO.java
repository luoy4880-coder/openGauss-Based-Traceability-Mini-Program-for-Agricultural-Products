package com.yujia.backend.vo;

import com.yujia.backend.entity.BaseInfo;
import com.yujia.backend.entity.InspectionReport;
import com.yujia.backend.entity.LogisticsRecord;
import com.yujia.backend.entity.ProductItem;
import com.yujia.backend.entity.ProductionRecord;
import com.yujia.backend.entity.RecallRecord;
import lombok.Data;

import java.util.List;

@Data
public class BatchArchiveVO {

    private ProductBatchVO batchInfo;
    private BaseInfo baseInfo;
    private List<ProductionRecord> productionRecords;
    private List<InspectionReport> inspectionReports;
    private List<ProductItem> productItems;
    private List<LogisticsRecord> logisticsRecords;
    private RecallRecord recallRecord;
    private int feedbackCount;
    private int highPriorityFeedbackCount;
    private int scanCount;
    private int abnormalScanCount;
}

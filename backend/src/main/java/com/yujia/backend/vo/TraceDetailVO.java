package com.yujia.backend.vo;

import com.yujia.backend.entity.BaseInfo;
import com.yujia.backend.entity.InspectionReport;
import com.yujia.backend.entity.LogisticsRecord;
import com.yujia.backend.entity.ProductItem;
import com.yujia.backend.entity.ProductionRecord;
import com.yujia.backend.entity.RecallRecord;
import com.yujia.backend.entity.TraceCode;
import lombok.Data;

import java.util.List;

@Data
public class TraceDetailVO {

    private TraceCode traceCode;

    private ProductItem productItem;

    private BaseInfo baseInfo;

    private ProductBatchVO batchInfo;

    private List<ProductionRecord> productionRecords;

    private List<InspectionReport> inspectionReports;

    private RecallRecord recallRecord;

    private boolean recallWarning;

    private TraceVerifyVO verifyInfo;

    private List<LogisticsRecord> logisticsRecords;
}

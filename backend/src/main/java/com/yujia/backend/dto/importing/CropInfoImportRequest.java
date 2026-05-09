package com.yujia.backend.dto.importing;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class CropInfoImportRequest {

    private BasePayload base;
    private BatchPayload batch;
    private ItemGenerationPayload itemGeneration;
    private List<ProductionRecordPayload> productionRecords;
    private List<LogisticsRecordPayload> logisticsRecords;
    private InspectionReportPayload inspectionReport;

    @Data
    public static class BasePayload {
        private String baseCode;
        private String baseName;
        private String managerName;
        private String contactPhone;
        private String province;
        private String city;
        private String district;
        private String address;
        private BigDecimal acreage;
        private Integer status;
    }

    @Data
    public static class BatchPayload {
        private String batchCode;
        private String productName;
        private String productCategory;
        private LocalDate plantingDate;
        private LocalDate expectedHarvestDate;
        private LocalDate actualHarvestDate;
        private BigDecimal quantity;
        private String unit;
        private Integer batchStatus;
        private String remark;
    }

    @Data
    public static class ItemGenerationPayload {
        private Integer quantity;
    }

    @Data
    public static class ProductionRecordPayload {
        private String recordType;
        private LocalDateTime operationTime;
        private String operatorName;
        private String materialName;
        private String dosage;
        private String content;
        private String attachmentUrl;
    }

    @Data
    public static class LogisticsRecordPayload {
        private String nodeType;
        private String nodeName;
        private LocalDateTime operationTime;
        private String operatorName;
        private String contactPhone;
        private String location;
        private String temperature;
        private String humidity;
        private String attachmentUrl;
        private String remark;
    }

    @Data
    public static class InspectionReportPayload {
        private String reportNo;
        private String agencyName;
        private String inspectorName;
        private LocalDateTime inspectionTime;
        private Integer resultStatus;
        private String conclusion;
        private String reportFileName;
    }
}

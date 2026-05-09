package com.yujia.backend.service;

import com.yujia.backend.common.exception.BusinessException;
import com.yujia.backend.dto.trace.TraceCodeGenerateRequest;
import com.yujia.backend.entity.ProductItem;
import com.yujia.backend.entity.TraceCode;
import com.yujia.backend.mapper.TraceCodeMapper;
import com.yujia.backend.vo.TraceDetailVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class TraceCodeService {

    private final TraceCodeMapper traceCodeMapper;
    private final ProductBatchService productBatchService;
    private final BaseInfoService baseInfoService;
    private final ProductionRecordService productionRecordService;
    private final InspectionReportService inspectionReportService;
    private final RecallRecordService recallRecordService;
    private final ProductItemService productItemService;
    private final ScanLogService scanLogService;
    private final LogisticsRecordService logisticsRecordService;
    private final TraceSecurityService traceSecurityService;

    public List<TraceCode> listByBatchId(Long batchId) {
        return traceCodeMapper.selectByBatchId(batchId);
    }

    public TraceCode generate(TraceCodeGenerateRequest request) {
        var batch = productBatchService.detail(request.getBatchId());

        TraceCode traceCode = new TraceCode();
        traceCode.setBatchId(request.getBatchId());
        traceCode.setTraceId(UUID.randomUUID().toString().replace("-", ""));
        traceCode.setSignValue(traceSecurityService.sign(batch.getBatchCode(), traceCode.getTraceId()));
        traceCode.setQrContent("/api/trace/" + traceCode.getTraceId() + "?sign=" + traceCode.getSignValue());
        traceCode.setCodeStatus(1);
        traceCodeMapper.insert(traceCode);
        return traceCodeMapper.selectByTraceId(traceCode.getTraceId());
    }

    public TraceDetailVO getTraceDetail(String traceId) {
        ProductItem productItem = productItemService.getByTraceId(traceId);
        if (productItem != null) {
            return buildItemTraceDetail(productItem);
        }
        return buildLegacyBatchTraceDetail(traceId);
    }

    private TraceDetailVO buildItemTraceDetail(ProductItem productItem) {
        var batchInfo = productBatchService.detail(productItem.getBatchId());
        boolean signValid = traceSecurityService.verify(
                batchInfo.getBatchCode(), productItem.getTraceId(), productItem.getItemCode(), productItem.getSignValue());
        boolean recallWarning = batchInfo.getRecallStatus() != null && batchInfo.getRecallStatus() == 1;
        var verifyInfo = scanLogService.recordItemScan(productItem, signValid, recallWarning, null, null);
        productItemService.recordScan(productItem);

        TraceDetailVO detail = new TraceDetailVO();
        TraceCode traceCode = new TraceCode();
        traceCode.setTraceId(productItem.getTraceId());
        traceCode.setBatchId(productItem.getBatchId());
        traceCode.setQrContent(productItem.getQrContent());
        traceCode.setSignValue(productItem.getSignValue());
        traceCode.setCodeStatus(productItem.getItemStatus());
        detail.setTraceCode(traceCode);
        detail.setProductItem(productItemService.getByTraceId(productItem.getTraceId()));
        detail.setBatchInfo(batchInfo);
        detail.setBaseInfo(baseInfoService.detail(batchInfo.getBaseId()));
        detail.setProductionRecords(productionRecordService.list(batchInfo.getId(), null));
        detail.setInspectionReports(inspectionReportService.list(batchInfo.getId(), null));
        detail.setRecallRecord(recallRecordService.latestByBatchId(batchInfo.getId()));
        detail.setRecallWarning(recallWarning);
        detail.setVerifyInfo(verifyInfo);
        detail.setLogisticsRecords(logisticsRecordService.list(batchInfo.getId(), productItem.getId()));
        return detail;
    }

    private TraceDetailVO buildLegacyBatchTraceDetail(String traceId) {
        TraceCode traceCode = traceCodeMapper.selectByTraceId(traceId);
        if (traceCode == null) {
            throw new BusinessException(404, "溯源码不存在");
        }

        var batchInfo = productBatchService.detail(traceCode.getBatchId());
        boolean recallWarning = batchInfo.getRecallStatus() != null && batchInfo.getRecallStatus() == 1;
        TraceDetailVO detail = new TraceDetailVO();
        detail.setTraceCode(traceCode);
        detail.setBatchInfo(batchInfo);
        detail.setBaseInfo(baseInfoService.detail(batchInfo.getBaseId()));
        detail.setProductionRecords(productionRecordService.list(batchInfo.getId(), null));
        detail.setInspectionReports(inspectionReportService.list(batchInfo.getId(), null));
        detail.setRecallRecord(recallRecordService.latestByBatchId(batchInfo.getId()));
        detail.setRecallWarning(recallWarning);
        detail.setVerifyInfo(scanLogService.legacyTraceVerify(traceId, recallWarning));
        detail.setLogisticsRecords(logisticsRecordService.list(batchInfo.getId(), null));
        return detail;
    }
}

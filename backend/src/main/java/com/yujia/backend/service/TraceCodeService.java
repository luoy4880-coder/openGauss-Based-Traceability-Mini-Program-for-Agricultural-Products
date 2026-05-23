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

    public TraceDetailVO getTraceDetail(String traceId, String signValue, String ipAddress, String userAgent) {
        return getTraceDetail(traceId, signValue, ipAddress, userAgent, true);
    }

    public TraceDetailVO getTraceSnapshot(String traceId, String signValue) {
        return getTraceDetail(traceId, signValue, null, null, false);
    }

    private TraceDetailVO getTraceDetail(String traceId, String signValue, String ipAddress, String userAgent, boolean recordScan) {
        ProductItem productItem = productItemService.getByTraceId(traceId);
        if (productItem != null) {
            return buildItemTraceDetail(productItem, signValue, ipAddress, userAgent, recordScan);
        }
        return buildLegacyBatchTraceDetail(traceId, signValue, ipAddress, userAgent, recordScan);
    }

    private TraceDetailVO buildItemTraceDetail(ProductItem productItem, String signValue, String ipAddress, String userAgent, boolean recordScan) {
        var batchInfo = productBatchService.detail(productItem.getBatchId());
        boolean signValid = traceSecurityService.verify(
                batchInfo.getBatchCode(),
                productItem.getTraceId(),
                productItem.getItemCode(),
                normalizeSignValue(signValue)
        );
        boolean recallWarning = batchInfo.getRecallStatus() != null && batchInfo.getRecallStatus() == 1;
        var verifyInfo = recordScan
                ? scanLogService.recordItemScan(productItem, signValid, recallWarning, ipAddress, userAgent)
                : scanLogService.buildItemVerifyPreview(productItem, signValid, recallWarning);
        if (recordScan) {
            productItemService.recordScan(productItem);
        }

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

    private TraceDetailVO buildLegacyBatchTraceDetail(String traceId, String signValue, String ipAddress, String userAgent, boolean recordScan) {
        TraceCode traceCode = traceCodeMapper.selectByTraceId(traceId);
        if (traceCode == null) {
            throw new BusinessException(404, "溯源码不存在");
        }

        var batchInfo = productBatchService.detail(traceCode.getBatchId());
        boolean signValid = traceSecurityService.verify(
                batchInfo.getBatchCode(),
                traceCode.getTraceId(),
                "",
                normalizeSignValue(signValue)
        );
        boolean recallWarning = batchInfo.getRecallStatus() != null && batchInfo.getRecallStatus() == 1;
        var verifyInfo = recordScan
                ? scanLogService.legacyTraceVerify(traceCode, signValid, recallWarning, ipAddress, userAgent)
                : scanLogService.buildLegacyVerifyPreview(traceCode, signValid, recallWarning);

        TraceDetailVO detail = new TraceDetailVO();
        detail.setTraceCode(traceCode);
        detail.setBatchInfo(batchInfo);
        detail.setBaseInfo(baseInfoService.detail(batchInfo.getBaseId()));
        detail.setProductionRecords(productionRecordService.list(batchInfo.getId(), null));
        detail.setInspectionReports(inspectionReportService.list(batchInfo.getId(), null));
        detail.setRecallRecord(recallRecordService.latestByBatchId(batchInfo.getId()));
        detail.setRecallWarning(recallWarning);
        detail.setVerifyInfo(verifyInfo);
        detail.setLogisticsRecords(logisticsRecordService.list(batchInfo.getId(), null));
        return detail;
    }

    private String normalizeSignValue(String signValue) {
        return signValue == null ? "" : signValue.trim();
    }
}

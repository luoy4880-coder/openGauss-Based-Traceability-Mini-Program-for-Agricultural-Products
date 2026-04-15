package com.yujia.backend.service;

import com.yujia.backend.common.exception.BusinessException;
import com.yujia.backend.dto.trace.TraceCodeGenerateRequest;
import com.yujia.backend.entity.TraceCode;
import com.yujia.backend.mapper.TraceCodeMapper;
import com.yujia.backend.vo.TraceDetailVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;
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

    public List<TraceCode> listByBatchId(Long batchId) {
        return traceCodeMapper.selectByBatchId(batchId);
    }

    public TraceCode generate(TraceCodeGenerateRequest request) {
        var batch = productBatchService.detail(request.getBatchId());

        TraceCode traceCode = new TraceCode();
        traceCode.setBatchId(request.getBatchId());
        traceCode.setTraceId(UUID.randomUUID().toString().replace("-", ""));
        traceCode.setQrContent("/api/trace/" + traceCode.getTraceId());
        traceCode.setSignValue(sign(batch.getBatchCode(), traceCode.getTraceId()));
        traceCode.setCodeStatus(1);
        traceCodeMapper.insert(traceCode);
        return traceCodeMapper.selectByTraceId(traceCode.getTraceId());
    }

    public TraceDetailVO getTraceDetail(String traceId) {
        TraceCode traceCode = traceCodeMapper.selectByTraceId(traceId);
        if (traceCode == null) {
            throw new BusinessException(404, "溯源码不存在");
        }

        var batchInfo = productBatchService.detail(traceCode.getBatchId());
        TraceDetailVO detail = new TraceDetailVO();
        detail.setTraceCode(traceCode);
        detail.setBatchInfo(batchInfo);
        detail.setBaseInfo(baseInfoService.detail(batchInfo.getBaseId()));
        detail.setProductionRecords(productionRecordService.list(batchInfo.getId(), null));
        detail.setInspectionReports(inspectionReportService.list(batchInfo.getId(), null));
        detail.setRecallRecord(recallRecordService.latestByBatchId(batchInfo.getId()));
        detail.setRecallWarning(batchInfo.getRecallStatus() != null && batchInfo.getRecallStatus() == 1);
        return detail;
    }

    private String sign(String batchCode, String traceId) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] digest = messageDigest.digest((batchCode + ":" + traceId).getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException exception) {
            throw new BusinessException(500, "生成溯源码签名失败");
        }
    }
}

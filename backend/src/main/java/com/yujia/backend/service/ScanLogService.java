package com.yujia.backend.service;

import com.yujia.backend.entity.ProductItem;
import com.yujia.backend.entity.ScanLog;
import com.yujia.backend.entity.TraceCode;
import com.yujia.backend.mapper.ScanLogMapper;
import com.yujia.backend.vo.TraceVerifyVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScanLogService {

    private static final int MAX_SCAN_SOURCE_LENGTH = 32;
    private static final int MAX_IP_ADDRESS_LENGTH = 64;
    private static final int MAX_USER_AGENT_LENGTH = 255;
    private static final int MAX_RISK_MESSAGE_LENGTH = 255;

    private final ScanLogMapper scanLogMapper;

    public TraceVerifyVO recordItemScan(ProductItem item, boolean signValid, boolean recalled, String ipAddress, String userAgent) {
        long previousCount = scanLogMapper.countByTraceId(item.getTraceId());
        TraceVerifyVO verify = buildItemVerify(item, signValid, recalled, previousCount);

        ScanLog scanLog = new ScanLog();
        scanLog.setTraceId(item.getTraceId());
        scanLog.setItemId(item.getId());
        scanLog.setBatchId(item.getBatchId());
        scanLog.setScanSource(limitLength("MINIAPP", MAX_SCAN_SOURCE_LENGTH));
        scanLog.setIpAddress(limitLength(ipAddress, MAX_IP_ADDRESS_LENGTH));
        scanLog.setUserAgent(limitLength(userAgent, MAX_USER_AGENT_LENGTH));
        scanLog.setVerifyResult(verify.isAbnormal() ? 0 : 1);
        scanLog.setRiskMessage(limitLength(verify.getRiskMessage(), MAX_RISK_MESSAGE_LENGTH));
        scanLogMapper.insert(scanLog);

        return verify;
    }

    public TraceVerifyVO buildItemVerifyPreview(ProductItem item, boolean signValid, boolean recalled) {
        long previousCount = scanLogMapper.countByTraceId(item.getTraceId());
        return buildItemVerify(item, signValid, recalled, previousCount);
    }

    public TraceVerifyVO legacyTraceVerify(TraceCode traceCode, boolean signValid, boolean recalled, String ipAddress, String userAgent) {
        long previousCount = scanLogMapper.countByTraceId(traceCode.getTraceId());
        TraceVerifyVO verify = buildLegacyVerify(traceCode, signValid, recalled, previousCount);

        ScanLog scanLog = new ScanLog();
        scanLog.setTraceId(traceCode.getTraceId());
        scanLog.setBatchId(traceCode.getBatchId());
        scanLog.setScanSource(limitLength("MINIAPP", MAX_SCAN_SOURCE_LENGTH));
        scanLog.setIpAddress(limitLength(ipAddress, MAX_IP_ADDRESS_LENGTH));
        scanLog.setUserAgent(limitLength(userAgent, MAX_USER_AGENT_LENGTH));
        scanLog.setVerifyResult(verify.isAbnormal() ? 0 : 1);
        scanLog.setRiskMessage(limitLength(verify.getRiskMessage(), MAX_RISK_MESSAGE_LENGTH));
        scanLogMapper.insert(scanLog);

        return verify;
    }

    public TraceVerifyVO buildLegacyVerifyPreview(TraceCode traceCode, boolean signValid, boolean recalled) {
        long previousCount = scanLogMapper.countByTraceId(traceCode.getTraceId());
        return buildLegacyVerify(traceCode, signValid, recalled, previousCount);
    }

    private TraceVerifyVO buildItemVerify(ProductItem item, boolean signValid, boolean recalled, long previousCount) {
        boolean abnormal = !signValid || recalled || item.getItemStatus() != null && item.getItemStatus() == 2 || previousCount >= 5;
        String riskMessage = buildRiskMessage(signValid, recalled, previousCount);
        TraceVerifyVO verify = new TraceVerifyVO();
        verify.setValid(signValid);
        verify.setFirstScan(previousCount == 0);
        verify.setAbnormal(abnormal);
        verify.setScanCount((int) previousCount + 1);
        verify.setVerifyMessage(signValid ? "官方有效溯源码" : "溯源码签名校验失败");
        verify.setRiskMessage(riskMessage);
        return verify;
    }

    private TraceVerifyVO buildLegacyVerify(TraceCode traceCode, boolean signValid, boolean recalled, long previousCount) {
        boolean abnormal = !signValid || recalled || previousCount >= 5;
        String riskMessage = buildRiskMessage(signValid, recalled, previousCount);
        TraceVerifyVO verify = new TraceVerifyVO();
        verify.setValid(signValid);
        verify.setFirstScan(previousCount == 0);
        verify.setAbnormal(abnormal);
        verify.setScanCount((int) previousCount + 1);
        verify.setVerifyMessage(signValid ? "官方有效溯源码" : "批次溯源码签名校验失败");
        verify.setRiskMessage(riskMessage);
        return verify;
    }

    private String buildRiskMessage(boolean signValid, boolean recalled, long previousCount) {
        if (!signValid) {
            return "溯源码签名异常，存在伪造风险";
        }
        if (recalled) {
            return "该批次已被召回，请停止食用并联系商家";
        }
        if (previousCount >= 5) {
            return "该码扫码次数较多，请核对包装信息";
        }
        return null;
    }

    private String limitLength(String value, int maxLength) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        if (trimmed.length() <= maxLength) {
            return trimmed;
        }
        return trimmed.substring(0, maxLength);
    }
}

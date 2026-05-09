package com.yujia.backend.service;

import com.yujia.backend.entity.ProductItem;
import com.yujia.backend.entity.ScanLog;
import com.yujia.backend.mapper.ScanLogMapper;
import com.yujia.backend.vo.TraceVerifyVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ScanLogService {

    private final ScanLogMapper scanLogMapper;

    public TraceVerifyVO recordItemScan(ProductItem item, boolean signValid, boolean recalled, String ipAddress, String userAgent) {
        long previousCount = scanLogMapper.countByTraceId(item.getTraceId());
        boolean abnormal = !signValid || recalled || item.getItemStatus() != null && item.getItemStatus() == 2 || previousCount >= 5;
        String riskMessage = buildRiskMessage(signValid, recalled, previousCount);

        ScanLog scanLog = new ScanLog();
        scanLog.setTraceId(item.getTraceId());
        scanLog.setItemId(item.getId());
        scanLog.setBatchId(item.getBatchId());
        scanLog.setScanSource("MINIAPP");
        scanLog.setIpAddress(ipAddress);
        scanLog.setUserAgent(userAgent);
        scanLog.setVerifyResult(abnormal ? 0 : 1);
        scanLog.setRiskMessage(riskMessage);
        scanLogMapper.insert(scanLog);

        TraceVerifyVO verify = new TraceVerifyVO();
        verify.setValid(signValid);
        verify.setFirstScan(previousCount == 0);
        verify.setAbnormal(abnormal);
        verify.setScanCount((int) previousCount + 1);
        verify.setVerifyMessage(signValid ? "官方有效溯源码" : "溯源码签名校验失败");
        verify.setRiskMessage(riskMessage);
        return verify;
    }

    public TraceVerifyVO legacyTraceVerify(String traceId, boolean recalled) {
        long previousCount = scanLogMapper.countByTraceId(traceId);
        ScanLog scanLog = new ScanLog();
        scanLog.setTraceId(traceId);
        scanLog.setScanSource("MINIAPP");
        scanLog.setVerifyResult(recalled ? 0 : 1);
        scanLog.setRiskMessage(recalled ? "该批次已被召回，请停止食用并联系商家" : null);
        scanLogMapper.insert(scanLog);

        TraceVerifyVO verify = new TraceVerifyVO();
        verify.setValid(true);
        verify.setFirstScan(previousCount == 0);
        verify.setAbnormal(recalled || previousCount >= 5);
        verify.setScanCount((int) previousCount + 1);
        verify.setVerifyMessage("批次溯源码有效");
        verify.setRiskMessage(recalled ? "该批次已被召回，请停止食用并联系商家" : null);
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
}

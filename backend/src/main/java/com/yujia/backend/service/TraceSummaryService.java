package com.yujia.backend.service;

import com.yujia.backend.vo.TraceDetailVO;
import com.yujia.backend.vo.TraceSummaryVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TraceSummaryService {

    private final TraceCodeService traceCodeService;

    public TraceSummaryVO summary(String traceId, String signValue) {
        var detail = traceCodeService.getTraceSnapshot(traceId, signValue);
        List<String> highlights = new ArrayList<>();
        List<String> riskTips = new ArrayList<>();
        List<String> actionTips = new ArrayList<>();

        int trustScore = 55;
        if (detail.getVerifyInfo() != null && detail.getVerifyInfo().isValid()) {
            trustScore += 15;
            highlights.add("追溯码签名校验通过");
        } else {
            riskTips.add("追溯码校验异常，请谨慎核验来源");
        }
        if (!detail.getProductionRecords().isEmpty()) {
            trustScore += 8;
            highlights.add("已记录生产过程");
        } else {
            riskTips.add("缺少生产过程记录");
        }
        if (!detail.getInspectionReports().isEmpty()) {
            trustScore += 10;
            highlights.add("存在质检报告");
        } else {
            riskTips.add("未看到质检报告");
        }
        if (!detail.getLogisticsRecords().isEmpty()) {
            trustScore += 7;
            highlights.add("可查看物流流转节点");
        }
        if (detail.isRecallWarning()) {
            trustScore -= 35;
            riskTips.add("该批次存在召回预警");
            actionTips.add("请暂停食用并联系商家或平台");
        }
        if (detail.getVerifyInfo() != null && detail.getVerifyInfo().isAbnormal()) {
            trustScore -= 15;
            if (detail.getVerifyInfo().getRiskMessage() != null) {
                riskTips.add(detail.getVerifyInfo().getRiskMessage());
            }
        }
        trustScore = Math.max(0, Math.min(100, trustScore));

        if (actionTips.isEmpty()) {
            actionTips.add("购买前可重点查看质检结论与物流节点是否完整");
        }
        if (riskTips.isEmpty()) {
            riskTips.add("当前未发现明显风险信号");
        }

        TraceSummaryVO vo = new TraceSummaryVO();
        vo.setSummaryTitle(detail.isRecallWarning() ? "存在风险提示" : "追溯信息较完整");
        vo.setSummaryText(buildSummaryText(detail, trustScore, riskTips));
        vo.setSafetyLevel(detail.isRecallWarning() ? "HIGH_RISK" : trustScore >= 80 ? "SAFE" : trustScore >= 60 ? "CHECK" : "CAUTION");
        vo.setTrustScore(trustScore);
        vo.setHighlights(highlights);
        vo.setRiskTips(riskTips);
        vo.setQualityInterpretation(buildQualityInterpretation(detail));
        vo.setActionTips(actionTips);
        return vo;
    }

    private String buildSummaryText(TraceDetailVO detail, int trustScore, List<String> riskTips) {
        String productName = detail.getBatchInfo().getProductName() == null ? "该产品" : detail.getBatchInfo().getProductName();
        return productName + " 当前追溯可信度约 " + trustScore + " 分。"
                + (detail.isRecallWarning() ? "系统检测到召回风险。" : "暂未发现召回风险。")
                + "重点提示：" + riskTips.get(0) + "。";
    }

    private String buildQualityInterpretation(TraceDetailVO detail) {
        if (detail.getInspectionReports().isEmpty()) {
            return "暂未查询到质检报告，建议谨慎参考该批次的质量证明。";
        }
        boolean allPassed = detail.getInspectionReports().stream()
                .allMatch(report -> report.getResultStatus() != null && report.getResultStatus() == 1);
        if (allPassed) {
            return "当前可见质检报告结论均为合格，结合生产和物流信息，质量证明相对完整。";
        }
        return "存在非合格或异常质检结果，建议不要仅凭页面信息判断安全性，应联系商家进一步核验。";
    }
}

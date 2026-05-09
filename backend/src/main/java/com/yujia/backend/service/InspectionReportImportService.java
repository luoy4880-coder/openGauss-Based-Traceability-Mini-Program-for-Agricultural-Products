package com.yujia.backend.service;

import com.yujia.backend.common.exception.BusinessException;
import com.yujia.backend.entity.InspectionReport;
import com.yujia.backend.entity.LogisticsRecord;
import com.yujia.backend.entity.ProductionRecord;
import com.yujia.backend.entity.SystemTask;
import com.yujia.backend.mapper.InspectionReportMapper;
import com.yujia.backend.mapper.LogisticsRecordMapper;
import com.yujia.backend.mapper.ProductionRecordMapper;
import com.yujia.backend.mapper.SystemTaskMapper;
import com.yujia.backend.vo.InspectionReportImportVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class InspectionReportImportService {

    private final ProductBatchService productBatchService;
    private final InspectionReportMapper inspectionReportMapper;
    private final ProductionRecordMapper productionRecordMapper;
    private final LogisticsRecordMapper logisticsRecordMapper;
    private final NumberGeneratorService numberGeneratorService;
    private final FileStorageService fileStorageService;
    private final SystemTaskMapper systemTaskMapper;

    @Transactional
    public InspectionReportImportVO importForTesting(Long batchId,
                                                     String agencyName,
                                                     String inspectorName,
                                                     LocalDateTime inspectionTime,
                                                     Integer resultStatus,
                                                     String conclusion,
                                                     MultipartFile file) {
        if (batchId == null) {
            throw new BusinessException(400, "batchId不能为空");
        }
        if (!StringUtils.hasText(agencyName)) {
            throw new BusinessException(400, "检测机构不能为空");
        }
        if (inspectionTime == null) {
            throw new BusinessException(400, "检测时间不能为空");
        }
        if (resultStatus == null) {
            throw new BusinessException(400, "检测结果不能为空");
        }
        if (file == null || file.isEmpty()) {
            throw new BusinessException(400, "请上传报告文件");
        }

        productBatchService.ensureBatchExists(batchId);

        String reportUrl = fileStorageService.store(file);
        InspectionReport report = new InspectionReport();
        report.setBatchId(batchId);
        report.setReportNo(numberGeneratorService.reportNo());
        report.setAgencyName(agencyName.trim());
        report.setInspectorName(StringUtils.hasText(inspectorName) ? inspectorName.trim() : "系统导入");
        report.setInspectionTime(inspectionTime);
        report.setResultStatus(resultStatus);
        report.setConclusion(buildConclusion(conclusion, resultStatus, file.getOriginalFilename()));
        report.setReportUrl(reportUrl);
        inspectionReportMapper.insert(report);

        int generatedProductionRecordCount = ensureSampleProductionRecords(batchId, reportUrl, inspectionTime);
        int generatedLogisticsRecordCount = ensureSampleLogisticsRecords(batchId, reportUrl, inspectionTime);
        boolean createdRiskTask = ensureRiskReviewTask(batchId, resultStatus, report.getReportNo(), report.getConclusion());

        return InspectionReportImportVO.builder()
                .report(inspectionReportMapper.selectById(report.getId()))
                .generatedProductionRecordCount(generatedProductionRecordCount)
                .generatedLogisticsRecordCount(generatedLogisticsRecordCount)
                .createdRiskTask(createdRiskTask)
                .build();
    }

    private String buildConclusion(String conclusion, Integer resultStatus, String originalFilename) {
        if (StringUtils.hasText(conclusion)) {
            return conclusion.trim();
        }
        String fileHint = StringUtils.hasText(originalFilename) ? "，来源文件：" + originalFilename.trim() : "";
        if (resultStatus != null && resultStatus == 1) {
            return "系统根据上传文件自动生成测试报告，当前判定为合格" + fileHint;
        }
        return "系统根据上传文件自动生成测试报告，当前判定为不合格，建议立即复核批次" + fileHint;
    }

    private int ensureSampleProductionRecords(Long batchId, String attachmentUrl, LocalDateTime inspectionTime) {
        if (!productionRecordMapper.selectList(null, batchId, null).isEmpty()) {
            return 0;
        }

        createProductionRecord(batchId, "播种", inspectionTime.minusDays(30), "种植班组", "优选种苗", "-", "完成播种与地块标记", attachmentUrl);
        createProductionRecord(batchId, "施肥", inspectionTime.minusDays(18), "田间管理员", "有机肥", "20kg", "完成生长期追肥与土壤检查", attachmentUrl);
        createProductionRecord(batchId, "采收", inspectionTime.minusDays(2), "采收班组", "周转筐", "-", "完成采收分拣并入库待检", attachmentUrl);
        return 3;
    }

    private void createProductionRecord(Long batchId,
                                        String recordType,
                                        LocalDateTime operationTime,
                                        String operatorName,
                                        String materialName,
                                        String dosage,
                                        String content,
                                        String attachmentUrl) {
        ProductionRecord record = new ProductionRecord();
        record.setBatchId(batchId);
        record.setRecordType(recordType);
        record.setOperationTime(operationTime);
        record.setOperatorName(operatorName);
        record.setMaterialName(materialName);
        record.setDosage(dosage);
        record.setContent(content);
        record.setAttachmentUrl(attachmentUrl);
        productionRecordMapper.insert(record);
    }

    private int ensureSampleLogisticsRecords(Long batchId, String attachmentUrl, LocalDateTime inspectionTime) {
        if (!logisticsRecordMapper.selectList(null, batchId, null).isEmpty()) {
            return 0;
        }

        createLogisticsRecord(batchId, "仓储", "产地冷库", inspectionTime.minusDays(1).minusHours(12), "冷库管理员", "基地冷库 A 区", "4C", "82%", attachmentUrl, "完成预冷入库");
        createLogisticsRecord(batchId, "运输", "冷链干线", inspectionTime.minusHours(18), "物流调度", "基地到区域仓", "5C", "78%", attachmentUrl, "冷链运输发车");
        createLogisticsRecord(batchId, "配送", "区域分拨", inspectionTime.minusHours(6), "分拨专员", "区域分拨中心", "6C", "75%", attachmentUrl, "等待末端配送");
        return 3;
    }

    private void createLogisticsRecord(Long batchId,
                                       String nodeType,
                                       String nodeName,
                                       LocalDateTime operationTime,
                                       String operatorName,
                                       String location,
                                       String temperature,
                                       String humidity,
                                       String attachmentUrl,
                                       String remark) {
        LogisticsRecord record = new LogisticsRecord();
        record.setBatchId(batchId);
        record.setItemId(null);
        record.setLogisticsCode(numberGeneratorService.logisticsCode());
        record.setNodeType(nodeType);
        record.setNodeName(nodeName);
        record.setOperationTime(operationTime);
        record.setOperatorName(operatorName);
        record.setContactPhone(null);
        record.setLocation(location);
        record.setTemperature(temperature);
        record.setHumidity(humidity);
        record.setAttachmentUrl(attachmentUrl);
        record.setRemark(remark);
        logisticsRecordMapper.insert(record);
    }

    private boolean ensureRiskReviewTask(Long batchId, Integer resultStatus, String reportNo, String conclusion) {
        if (resultStatus != null && resultStatus == 1) {
            return false;
        }

        SystemTask existing = systemTaskMapper.selectByUnique("REPORT_RISK_REVIEW", "BATCH", batchId);
        if (existing != null) {
            existing.setTitle("复核异常质检报告 - " + reportNo);
            existing.setDescription(conclusion);
            existing.setPriority(1);
            existing.setStatus(existing.getStatus() != null && existing.getStatus() == 2 ? 2 : 0);
            existing.setSourceType("REPORT_IMPORT");
            existing.setDueAt(LocalDateTime.now().plusHours(12));
            systemTaskMapper.update(existing);
            return false;
        }

        SystemTask task = new SystemTask();
        task.setTaskType("REPORT_RISK_REVIEW");
        task.setBizType("BATCH");
        task.setBizId(batchId);
        task.setTitle("复核异常质检报告 - " + reportNo);
        task.setDescription(conclusion);
        task.setPriority(1);
        task.setStatus(0);
        task.setAssigneeUserId(null);
        task.setClaimedAt(null);
        task.setCompletedByUserId(null);
        task.setSourceType("REPORT_IMPORT");
        task.setDueAt(LocalDateTime.now().plusHours(12));
        task.setCompletedAt(null);
        systemTaskMapper.insert(task);
        return true;
    }
}

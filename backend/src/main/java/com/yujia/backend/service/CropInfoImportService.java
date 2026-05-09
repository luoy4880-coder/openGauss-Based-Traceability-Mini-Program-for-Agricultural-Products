package com.yujia.backend.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yujia.backend.common.exception.BusinessException;
import com.yujia.backend.dto.importing.CropInfoImportRequest;
import com.yujia.backend.entity.BaseInfo;
import com.yujia.backend.entity.InspectionReport;
import com.yujia.backend.entity.LogisticsRecord;
import com.yujia.backend.entity.ProductBatch;
import com.yujia.backend.entity.ProductItem;
import com.yujia.backend.entity.ProductionRecord;
import com.yujia.backend.entity.SystemTask;
import com.yujia.backend.mapper.BaseInfoMapper;
import com.yujia.backend.mapper.InspectionReportMapper;
import com.yujia.backend.mapper.LogisticsRecordMapper;
import com.yujia.backend.mapper.ProductBatchMapper;
import com.yujia.backend.mapper.ProductItemMapper;
import com.yujia.backend.mapper.ProductionRecordMapper;
import com.yujia.backend.mapper.SystemTaskMapper;
import com.yujia.backend.vo.CropInfoImportVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CropInfoImportService {

    private final ObjectMapper objectMapper;
    private final BaseInfoMapper baseInfoMapper;
    private final ProductBatchMapper productBatchMapper;
    private final ProductionRecordMapper productionRecordMapper;
    private final LogisticsRecordMapper logisticsRecordMapper;
    private final InspectionReportMapper inspectionReportMapper;
    private final ProductItemMapper productItemMapper;
    private final NumberGeneratorService numberGeneratorService;
    private final TraceSecurityService traceSecurityService;
    private final SystemTaskMapper systemTaskMapper;
    private final CompanyScopeService companyScopeService;

    @Transactional
    public CropInfoImportVO importFromJsonFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException(400, "请先上传导入文件");
        }

        CropInfoImportRequest request = parseRequest(file);
        validateRequest(request);

        BaseInfo baseInfo = createBase(request.getBase());
        ProductBatch batch = createBatch(baseInfo.getId(), request.getBatch());
        int productionCount = createProductionRecords(batch.getId(), request.getProductionRecords());
        int logisticsCount = createLogisticsRecords(batch.getId(), request.getLogisticsRecords());
        InspectionReport report = createInspectionReport(batch.getId(), request.getInspectionReport());
        int itemCount = createItems(batch, request.getItemGeneration());
        boolean createdRiskTask = createRiskTaskIfNeeded(batch, report);

        return CropInfoImportVO.builder()
                .baseId(baseInfo.getId())
                .baseCode(baseInfo.getBaseCode())
                .batchId(batch.getId())
                .batchCode(batch.getBatchCode())
                .productionRecordCount(productionCount)
                .logisticsRecordCount(logisticsCount)
                .itemCount(itemCount)
                .inspectionReportId(report.getId())
                .inspectionReportNo(report.getReportNo())
                .createdRiskTask(createdRiskTask)
                .build();
    }

    private CropInfoImportRequest parseRequest(MultipartFile file) {
        try {
            return objectMapper.readValue(file.getInputStream(), CropInfoImportRequest.class);
        } catch (IOException exception) {
            throw new BusinessException(400, "导入文件不是有效的 JSON 结构");
        }
    }

    private void validateRequest(CropInfoImportRequest request) {
        if (request == null || request.getBase() == null || request.getBatch() == null || request.getInspectionReport() == null) {
            throw new BusinessException(400, "导入文件缺少基地、批次或质检报告信息");
        }
        if (!StringUtils.hasText(request.getBase().getBaseName())) {
            throw new BusinessException(400, "基地名称不能为空");
        }
        if (!StringUtils.hasText(request.getBatch().getProductName())) {
            throw new BusinessException(400, "作物名称不能为空");
        }
        if (!StringUtils.hasText(request.getInspectionReport().getAgencyName())) {
            throw new BusinessException(400, "检测机构不能为空");
        }
        if (request.getInspectionReport().getInspectionTime() == null) {
            throw new BusinessException(400, "检测时间不能为空");
        }
        if (request.getInspectionReport().getResultStatus() == null) {
            throw new BusinessException(400, "检测结果不能为空");
        }
    }

    private BaseInfo createBase(CropInfoImportRequest.BasePayload payload) {
        if (StringUtils.hasText(payload.getBaseCode()) && baseInfoMapper.selectByBaseCode(payload.getBaseCode()) != null) {
            throw new BusinessException(400, "基地编码已存在: " + payload.getBaseCode());
        }

        BaseInfo baseInfo = new BaseInfo();
        baseInfo.setCompanyId(companyScopeService.requireCurrentCompanyId());
        baseInfo.setBaseCode(StringUtils.hasText(payload.getBaseCode()) ? payload.getBaseCode().trim() : numberGeneratorService.baseCode());
        baseInfo.setBaseName(payload.getBaseName().trim());
        baseInfo.setManagerName(payload.getManagerName());
        baseInfo.setContactPhone(payload.getContactPhone());
        baseInfo.setProvince(payload.getProvince());
        baseInfo.setCity(payload.getCity());
        baseInfo.setDistrict(payload.getDistrict());
        baseInfo.setAddress(payload.getAddress());
        baseInfo.setAcreage(payload.getAcreage());
        baseInfo.setStatus(payload.getStatus() == null ? 1 : payload.getStatus());
        baseInfoMapper.insert(baseInfo);
        return baseInfo;
    }

    private ProductBatch createBatch(Long baseId, CropInfoImportRequest.BatchPayload payload) {
        if (StringUtils.hasText(payload.getBatchCode()) && productBatchMapper.selectByBatchCode(payload.getBatchCode()) != null) {
            throw new BusinessException(400, "批次编码已存在: " + payload.getBatchCode());
        }

        ProductBatch batch = new ProductBatch();
        batch.setBatchCode(StringUtils.hasText(payload.getBatchCode()) ? payload.getBatchCode().trim() : numberGeneratorService.batchCode());
        batch.setBaseId(baseId);
        batch.setCompanyId(companyScopeService.requireCurrentCompanyId());
        batch.setProductName(payload.getProductName().trim());
        batch.setProductCategory(payload.getProductCategory());
        batch.setPlantingDate(payload.getPlantingDate());
        batch.setExpectedHarvestDate(payload.getExpectedHarvestDate());
        batch.setActualHarvestDate(payload.getActualHarvestDate());
        batch.setQuantity(payload.getQuantity());
        batch.setUnit(payload.getUnit());
        batch.setBatchStatus(payload.getBatchStatus() == null ? 1 : payload.getBatchStatus());
        batch.setRecallStatus(0);
        batch.setRemark(payload.getRemark());
        productBatchMapper.insert(batch);
        return batch;
    }

    private int createProductionRecords(Long batchId, List<CropInfoImportRequest.ProductionRecordPayload> payloads) {
        if (payloads == null || payloads.isEmpty()) {
            return 0;
        }
        for (CropInfoImportRequest.ProductionRecordPayload payload : payloads) {
            ProductionRecord record = new ProductionRecord();
            record.setBatchId(batchId);
            record.setRecordType(payload.getRecordType());
            record.setOperationTime(payload.getOperationTime());
            record.setOperatorName(payload.getOperatorName());
            record.setMaterialName(payload.getMaterialName());
            record.setDosage(payload.getDosage());
            record.setContent(payload.getContent());
            record.setAttachmentUrl(payload.getAttachmentUrl());
            productionRecordMapper.insert(record);
        }
        return payloads.size();
    }

    private int createLogisticsRecords(Long batchId, List<CropInfoImportRequest.LogisticsRecordPayload> payloads) {
        if (payloads == null || payloads.isEmpty()) {
            return 0;
        }
        for (CropInfoImportRequest.LogisticsRecordPayload payload : payloads) {
            LogisticsRecord record = new LogisticsRecord();
            record.setBatchId(batchId);
            record.setItemId(null);
            record.setLogisticsCode(numberGeneratorService.logisticsCode());
            record.setNodeType(payload.getNodeType());
            record.setNodeName(payload.getNodeName());
            record.setOperationTime(payload.getOperationTime());
            record.setOperatorName(payload.getOperatorName());
            record.setContactPhone(payload.getContactPhone());
            record.setLocation(payload.getLocation());
            record.setTemperature(payload.getTemperature());
            record.setHumidity(payload.getHumidity());
            record.setAttachmentUrl(payload.getAttachmentUrl());
            record.setRemark(payload.getRemark());
            logisticsRecordMapper.insert(record);
        }
        return payloads.size();
    }

    private InspectionReport createInspectionReport(Long batchId, CropInfoImportRequest.InspectionReportPayload payload) {
        InspectionReport report = new InspectionReport();
        report.setBatchId(batchId);
        report.setReportNo(StringUtils.hasText(payload.getReportNo()) ? payload.getReportNo().trim() : numberGeneratorService.reportNo());
        report.setAgencyName(payload.getAgencyName().trim());
        report.setInspectorName(payload.getInspectorName());
        report.setInspectionTime(payload.getInspectionTime());
        report.setResultStatus(payload.getResultStatus());
        report.setConclusion(payload.getConclusion());
        report.setReportUrl(payload.getReportFileName());
        inspectionReportMapper.insert(report);
        return report;
    }

    private int createItems(ProductBatch batch, CropInfoImportRequest.ItemGenerationPayload payload) {
        int quantity = payload == null || payload.getQuantity() == null ? 0 : payload.getQuantity();
        if (quantity <= 0) {
            return 0;
        }
        List<ProductItem> created = new ArrayList<>();
        for (int index = 1; index <= quantity; index++) {
            ProductItem item = new ProductItem();
            item.setBatchId(batch.getId());
            item.setItemCode(numberGeneratorService.itemCode(batch.getBatchCode(), index));
            item.setTraceId(UUID.randomUUID().toString().replace("-", ""));
            item.setSignValue(traceSecurityService.sign(batch.getBatchCode(), item.getTraceId(), item.getItemCode()));
            item.setQrContent("/api/trace/" + item.getTraceId() + "?sign=" + item.getSignValue());
            item.setItemStatus(batch.getRecallStatus() != null && batch.getRecallStatus() == 1 ? 2 : 1);
            productItemMapper.insert(item);
            created.add(item);
        }
        return created.size();
    }

    private boolean createRiskTaskIfNeeded(ProductBatch batch, InspectionReport report) {
        if (report.getResultStatus() != null && report.getResultStatus() == 1) {
            return false;
        }

        SystemTask task = new SystemTask();
        task.setTaskType("IMPORT_RISK_REVIEW");
        task.setBizType("BATCH");
        task.setBizId(batch.getId());
        task.setTitle("处理异常导入批次 - " + batch.getBatchCode());
        task.setDescription("快速导入的批次质检结果异常，请优先复核。报告编号=" + report.getReportNo());
        task.setPriority(1);
        task.setStatus(0);
        task.setAssigneeUserId(null);
        task.setClaimedAt(null);
        task.setCompletedByUserId(null);
        task.setSourceType("QUICK_IMPORT");
        task.setDueAt(LocalDateTime.now().plusHours(12));
        task.setCompletedAt(null);
        systemTaskMapper.insert(task);
        return true;
    }
}

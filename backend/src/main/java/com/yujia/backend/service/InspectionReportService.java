package com.yujia.backend.service;

import com.yujia.backend.common.exception.BusinessException;
import com.yujia.backend.common.response.PageResponse;
import com.yujia.backend.dto.report.InspectionReportCreateRequest;
import com.yujia.backend.dto.report.InspectionReportUpdateRequest;
import com.yujia.backend.entity.InspectionReport;
import com.yujia.backend.mapper.InspectionReportMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InspectionReportService {

    private final InspectionReportMapper inspectionReportMapper;
    private final ProductBatchService productBatchService;
    private final NumberGeneratorService numberGeneratorService;
    private final CompanyScopeService companyScopeService;

    public List<InspectionReport> list(Long batchId, Integer resultStatus) {
        if (batchId != null) {
            productBatchService.ensureBatchExists(batchId);
        }
        return inspectionReportMapper.selectList(companyScopeService.currentCompanyScopeOrNull(), batchId, resultStatus);
    }

    public PageResponse<InspectionReport> page(Long batchId, Integer resultStatus,
                                               Integer pageNum, Integer pageSize) {
        int safePageNum = pageNum == null || pageNum < 1 ? 1 : pageNum;
        int safePageSize = pageSize == null || pageSize < 1 ? 10 : Math.min(pageSize, 100);
        if (batchId != null) {
            productBatchService.ensureBatchExists(batchId);
        }
        Long companyId = companyScopeService.currentCompanyScopeOrNull();
        long total = inspectionReportMapper.countList(companyId, batchId, resultStatus);
        List<InspectionReport> records = inspectionReportMapper.selectPage(
                companyId, batchId, resultStatus, (long) (safePageNum - 1) * safePageSize, safePageSize);
        return PageResponse.<InspectionReport>builder()
                .records(records)
                .total(total)
                .pageNum(safePageNum)
                .pageSize(safePageSize)
                .build();
    }

    public InspectionReport detail(Long id) {
        InspectionReport inspectionReport = inspectionReportMapper.selectById(id);
        if (inspectionReport == null) {
            throw new BusinessException(404, "质检报告不存在");
        }
        productBatchService.ensureBatchExists(inspectionReport.getBatchId());
        return inspectionReport;
    }

    public InspectionReport create(InspectionReportCreateRequest request) {
        productBatchService.ensureBatchExists(request.getBatchId());
        String reportNo = StringUtils.hasText(request.getReportNo()) ? request.getReportNo() : numberGeneratorService.reportNo();
        if (inspectionReportMapper.selectByReportNo(reportNo) != null) {
            throw new BusinessException("质检报告编号已存在");
        }

        InspectionReport inspectionReport = new InspectionReport();
        inspectionReport.setBatchId(request.getBatchId());
        inspectionReport.setReportNo(reportNo);
        inspectionReport.setAgencyName(request.getAgencyName());
        inspectionReport.setInspectorName(request.getInspectorName());
        inspectionReport.setInspectionTime(request.getInspectionTime());
        inspectionReport.setResultStatus(request.getResultStatus());
        inspectionReport.setConclusion(request.getConclusion());
        inspectionReport.setReportUrl(request.getReportUrl());
        inspectionReportMapper.insert(inspectionReport);
        return detail(inspectionReport.getId());
    }

    public InspectionReport update(Long id, InspectionReportUpdateRequest request) {
        InspectionReport inspectionReport = detail(id);
        productBatchService.ensureBatchExists(request.getBatchId());

        inspectionReport.setBatchId(request.getBatchId());
        inspectionReport.setAgencyName(request.getAgencyName());
        inspectionReport.setInspectorName(request.getInspectorName());
        inspectionReport.setInspectionTime(request.getInspectionTime());
        inspectionReport.setResultStatus(request.getResultStatus());
        inspectionReport.setConclusion(request.getConclusion());
        inspectionReport.setReportUrl(request.getReportUrl());
        inspectionReportMapper.updateById(inspectionReport);
        return detail(id);
    }

    public void delete(Long id) {
        detail(id);
        inspectionReportMapper.deleteById(id);
    }
}

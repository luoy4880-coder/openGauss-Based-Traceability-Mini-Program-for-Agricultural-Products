package com.yujia.backend.service;

import com.yujia.backend.common.exception.BusinessException;
import com.yujia.backend.common.response.PageResponse;
import com.yujia.backend.dto.report.InspectionReportCreateRequest;
import com.yujia.backend.dto.report.InspectionReportUpdateRequest;
import com.yujia.backend.entity.InspectionReport;
import com.yujia.backend.mapper.InspectionReportMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InspectionReportService {

    private final InspectionReportMapper inspectionReportMapper;
    private final ProductBatchService productBatchService;

    public List<InspectionReport> list(Long batchId, Integer resultStatus) {
        return inspectionReportMapper.selectList(batchId, resultStatus);
    }

    public PageResponse<InspectionReport> page(Long batchId, Integer resultStatus,
                                               Integer pageNum, Integer pageSize) {
        int safePageNum = normalizePageNum(pageNum);
        int safePageSize = normalizePageSize(pageSize);
        long total = inspectionReportMapper.countList(batchId, resultStatus);
        List<InspectionReport> records = inspectionReportMapper.selectPage(
                batchId, resultStatus, (long) (safePageNum - 1) * safePageSize, safePageSize);
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
        return inspectionReport;
    }

    public InspectionReport create(InspectionReportCreateRequest request) {
        productBatchService.ensureBatchExists(request.getBatchId());
        if (inspectionReportMapper.selectByReportNo(request.getReportNo()) != null) {
            throw new BusinessException("质检报告编号已存在");
        }

        InspectionReport inspectionReport = new InspectionReport();
        inspectionReport.setBatchId(request.getBatchId());
        inspectionReport.setReportNo(request.getReportNo());
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

    private int normalizePageNum(Integer pageNum) {
        return pageNum == null || pageNum < 1 ? 1 : pageNum;
    }

    private int normalizePageSize(Integer pageSize) {
        if (pageSize == null || pageSize < 1) {
            return 10;
        }
        return Math.min(pageSize, 100);
    }
}

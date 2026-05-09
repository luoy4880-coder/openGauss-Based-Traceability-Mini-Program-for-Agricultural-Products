package com.yujia.backend.service;

import com.yujia.backend.common.exception.BusinessException;
import com.yujia.backend.common.response.PageResponse;
import com.yujia.backend.dto.record.ProductionRecordCreateRequest;
import com.yujia.backend.dto.record.ProductionRecordUpdateRequest;
import com.yujia.backend.entity.ProductionRecord;
import com.yujia.backend.mapper.ProductionRecordMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductionRecordService {

    private final ProductionRecordMapper productionRecordMapper;
    private final ProductBatchService productBatchService;
    private final CompanyScopeService companyScopeService;

    public List<ProductionRecord> list(Long batchId, String recordType) {
        if (batchId != null) {
            productBatchService.ensureBatchExists(batchId);
        }
        return productionRecordMapper.selectList(companyScopeService.currentCompanyScopeOrNull(), batchId, recordType);
    }

    public PageResponse<ProductionRecord> page(Long batchId, String recordType,
                                               Integer pageNum, Integer pageSize) {
        int safePageNum = pageNum == null || pageNum < 1 ? 1 : pageNum;
        int safePageSize = pageSize == null || pageSize < 1 ? 10 : Math.min(pageSize, 100);
        if (batchId != null) {
            productBatchService.ensureBatchExists(batchId);
        }
        Long companyId = companyScopeService.currentCompanyScopeOrNull();
        long total = productionRecordMapper.countList(companyId, batchId, recordType);
        List<ProductionRecord> records = productionRecordMapper.selectPage(
                companyId, batchId, recordType, (long) (safePageNum - 1) * safePageSize, safePageSize);
        return PageResponse.<ProductionRecord>builder()
                .records(records)
                .total(total)
                .pageNum(safePageNum)
                .pageSize(safePageSize)
                .build();
    }

    public ProductionRecord detail(Long id) {
        ProductionRecord productionRecord = productionRecordMapper.selectById(id);
        if (productionRecord == null) {
            throw new BusinessException(404, "生产记录不存在");
        }
        productBatchService.ensureBatchExists(productionRecord.getBatchId());
        return productionRecord;
    }

    public ProductionRecord create(ProductionRecordCreateRequest request) {
        productBatchService.ensureBatchExists(request.getBatchId());

        ProductionRecord productionRecord = new ProductionRecord();
        productionRecord.setBatchId(request.getBatchId());
        productionRecord.setRecordType(request.getRecordType());
        productionRecord.setOperationTime(request.getOperationTime());
        productionRecord.setOperatorName(request.getOperatorName());
        productionRecord.setMaterialName(request.getMaterialName());
        productionRecord.setDosage(request.getDosage());
        productionRecord.setContent(request.getContent());
        productionRecord.setAttachmentUrl(request.getAttachmentUrl());
        productionRecordMapper.insert(productionRecord);
        return detail(productionRecord.getId());
    }

    public ProductionRecord update(Long id, ProductionRecordUpdateRequest request) {
        ProductionRecord productionRecord = detail(id);
        productBatchService.ensureBatchExists(request.getBatchId());

        productionRecord.setBatchId(request.getBatchId());
        productionRecord.setRecordType(request.getRecordType());
        productionRecord.setOperationTime(request.getOperationTime());
        productionRecord.setOperatorName(request.getOperatorName());
        productionRecord.setMaterialName(request.getMaterialName());
        productionRecord.setDosage(request.getDosage());
        productionRecord.setContent(request.getContent());
        productionRecord.setAttachmentUrl(request.getAttachmentUrl());
        productionRecordMapper.updateById(productionRecord);
        return detail(id);
    }

    public void delete(Long id) {
        detail(id);
        productionRecordMapper.deleteById(id);
    }
}

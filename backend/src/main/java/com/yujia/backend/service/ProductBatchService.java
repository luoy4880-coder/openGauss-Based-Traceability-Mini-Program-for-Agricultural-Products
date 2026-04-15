package com.yujia.backend.service;

import com.yujia.backend.common.exception.BusinessException;
import com.yujia.backend.common.response.PageResponse;
import com.yujia.backend.dto.batch.ProductBatchCreateRequest;
import com.yujia.backend.dto.batch.ProductBatchUpdateRequest;
import com.yujia.backend.entity.ProductBatch;
import com.yujia.backend.mapper.ProductBatchMapper;
import com.yujia.backend.vo.ProductBatchVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductBatchService {

    private final ProductBatchMapper productBatchMapper;
    private final BaseInfoService baseInfoService;

    public List<ProductBatchVO> list(String keyword, Long baseId, Integer batchStatus) {
        return productBatchMapper.selectList(keyword, baseId, batchStatus);
    }

    public PageResponse<ProductBatchVO> page(String keyword, Long baseId, Integer batchStatus,
                                             Integer pageNum, Integer pageSize) {
        int safePageNum = normalizePageNum(pageNum);
        int safePageSize = normalizePageSize(pageSize);
        long total = productBatchMapper.countList(keyword, baseId, batchStatus);
        List<ProductBatchVO> records = productBatchMapper.selectPage(
                keyword, baseId, batchStatus, (long) (safePageNum - 1) * safePageSize, safePageSize);
        return PageResponse.<ProductBatchVO>builder()
                .records(records)
                .total(total)
                .pageNum(safePageNum)
                .pageSize(safePageSize)
                .build();
    }

    public ProductBatchVO detail(Long id) {
        ProductBatchVO productBatchVO = productBatchMapper.selectDetailById(id);
        if (productBatchVO == null) {
            throw new BusinessException(404, "批次不存在");
        }
        return productBatchVO;
    }

    public ProductBatchVO create(ProductBatchCreateRequest request) {
        if (StringUtils.hasText(request.getBatchCode())
                && productBatchMapper.selectByBatchCode(request.getBatchCode()) != null) {
            throw new BusinessException("批次编码已存在");
        }

        baseInfoService.validateExists(request.getBaseId());

        ProductBatch productBatch = new ProductBatch();
        productBatch.setBatchCode(request.getBatchCode());
        productBatch.setBaseId(request.getBaseId());
        productBatch.setProductName(request.getProductName());
        productBatch.setProductCategory(request.getProductCategory());
        productBatch.setPlantingDate(request.getPlantingDate());
        productBatch.setExpectedHarvestDate(request.getExpectedHarvestDate());
        productBatch.setActualHarvestDate(request.getActualHarvestDate());
        productBatch.setQuantity(request.getQuantity());
        productBatch.setUnit(request.getUnit());
        productBatch.setBatchStatus(request.getBatchStatus());
        productBatch.setRecallStatus(request.getRecallStatus());
        productBatch.setRemark(request.getRemark());
        productBatchMapper.insert(productBatch);
        return detail(productBatch.getId());
    }

    public ProductBatchVO update(Long id, ProductBatchUpdateRequest request) {
        ProductBatchVO existing = detail(id);
        baseInfoService.validateExists(request.getBaseId());

        ProductBatch productBatch = new ProductBatch();
        productBatch.setId(existing.getId());
        productBatch.setBatchCode(existing.getBatchCode());
        productBatch.setBaseId(request.getBaseId());
        productBatch.setProductName(request.getProductName());
        productBatch.setProductCategory(request.getProductCategory());
        productBatch.setPlantingDate(request.getPlantingDate());
        productBatch.setExpectedHarvestDate(request.getExpectedHarvestDate());
        productBatch.setActualHarvestDate(request.getActualHarvestDate());
        productBatch.setQuantity(request.getQuantity());
        productBatch.setUnit(request.getUnit());
        productBatch.setBatchStatus(request.getBatchStatus());
        productBatch.setRecallStatus(request.getRecallStatus());
        productBatch.setRemark(request.getRemark());
        productBatchMapper.updateById(productBatch);
        return detail(id);
    }

    public void ensureBatchExists(Long id) {
        detail(id);
    }

    public void delete(Long id) {
        detail(id);
        if (productBatchMapper.countProductionRecordsByBatchId(id) > 0
                || productBatchMapper.countInspectionReportsByBatchId(id) > 0
                || productBatchMapper.countTraceCodesByBatchId(id) > 0
                || productBatchMapper.countRecallRecordsByBatchId(id) > 0) {
            throw new BusinessException("该批次下仍有关联业务数据，不能删除");
        }
        productBatchMapper.deleteById(id);
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

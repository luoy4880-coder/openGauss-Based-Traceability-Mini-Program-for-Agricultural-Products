package com.yujia.backend.service;

import com.yujia.backend.dto.item.ProductItemGenerateRequest;
import com.yujia.backend.entity.ProductItem;
import com.yujia.backend.mapper.ProductItemMapper;
import com.yujia.backend.vo.ProductBatchVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductItemService {

    private final ProductItemMapper productItemMapper;
    private final ProductBatchService productBatchService;
    private final NumberGeneratorService numberGeneratorService;
    private final TraceSecurityService traceSecurityService;

    public List<ProductItem> listByBatchId(Long batchId) {
        productBatchService.ensureBatchExists(batchId);
        return productItemMapper.selectByBatchId(batchId);
    }

    public ProductItem getByTraceId(String traceId) {
        return productItemMapper.selectByTraceId(traceId);
    }

    @Transactional
    public List<ProductItem> generate(ProductItemGenerateRequest request) {
        ProductBatchVO batch = productBatchService.detail(request.getBatchId());
        long existingCount = productItemMapper.countByBatchId(request.getBatchId());
        List<ProductItem> createdItems = new ArrayList<>();
        for (int index = 1; index <= request.getQuantity(); index++) {
            ProductItem item = new ProductItem();
            long serialNo = existingCount + index;
            item.setBatchId(request.getBatchId());
            item.setItemCode(numberGeneratorService.itemCode(batch.getBatchCode(), serialNo));
            item.setTraceId(UUID.randomUUID().toString().replace("-", ""));
            item.setSignValue(traceSecurityService.sign(batch.getBatchCode(), item.getTraceId(), item.getItemCode()));
            item.setQrContent("/api/trace/" + item.getTraceId() + "?sign=" + item.getSignValue());
            item.setItemStatus(batch.getRecallStatus() != null && batch.getRecallStatus() == 1 ? 2 : 1);
            productItemMapper.insert(item);
            createdItems.add(item);
        }
        return createdItems;
    }

    public void recordScan(ProductItem item) {
        if (item != null && item.getId() != null) {
            productItemMapper.incrementScanCount(item.getId());
        }
    }

    public void markBatchRecalled(Long batchId, boolean recalled) {
        productItemMapper.updateStatusByBatchId(batchId, recalled ? 2 : 1);
    }
}

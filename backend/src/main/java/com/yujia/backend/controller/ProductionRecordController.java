package com.yujia.backend.controller;

import com.yujia.backend.common.auth.AuthPermissionService;
import com.yujia.backend.common.response.ApiResponse;
import com.yujia.backend.common.response.PageResponse;
import com.yujia.backend.dto.record.ProductionRecordCreateRequest;
import com.yujia.backend.dto.record.ProductionRecordUpdateRequest;
import com.yujia.backend.entity.ProductionRecord;
import com.yujia.backend.service.ProductionRecordService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/production-records")
@RequiredArgsConstructor
public class ProductionRecordController {

    private final AuthPermissionService authPermissionService;
    private final ProductionRecordService productionRecordService;

    @GetMapping
    public ApiResponse<List<ProductionRecord>> list(@RequestParam(required = false) Long batchId,
                                                    @RequestParam(required = false) String recordType) {
        authPermissionService.requireStaff();
        return ApiResponse.success(productionRecordService.list(batchId, recordType));
    }

    @GetMapping("/page")
    public ApiResponse<PageResponse<ProductionRecord>> page(@RequestParam(required = false) Long batchId,
                                                            @RequestParam(required = false) String recordType,
                                                            @RequestParam(required = false) Integer pageNum,
                                                            @RequestParam(required = false) Integer pageSize) {
        authPermissionService.requireStaff();
        return ApiResponse.success(productionRecordService.page(batchId, recordType, pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public ApiResponse<ProductionRecord> detail(@PathVariable Long id) {
        authPermissionService.requireStaff();
        return ApiResponse.success(productionRecordService.detail(id));
    }

    @PostMapping
    public ApiResponse<ProductionRecord> create(@Valid @RequestBody ProductionRecordCreateRequest request) {
        authPermissionService.requireStaff();
        return ApiResponse.success(productionRecordService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<ProductionRecord> update(@PathVariable Long id,
                                                @Valid @RequestBody ProductionRecordUpdateRequest request) {
        authPermissionService.requireStaff();
        return ApiResponse.success(productionRecordService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        authPermissionService.requireAdmin();
        productionRecordService.delete(id);
        return ApiResponse.success();
    }
}

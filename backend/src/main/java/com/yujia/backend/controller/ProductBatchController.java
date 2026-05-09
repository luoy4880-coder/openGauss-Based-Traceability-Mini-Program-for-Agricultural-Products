package com.yujia.backend.controller;

import com.yujia.backend.common.auth.AuthPermissionService;
import com.yujia.backend.common.response.ApiResponse;
import com.yujia.backend.common.response.PageResponse;
import com.yujia.backend.dto.batch.ProductBatchCreateRequest;
import com.yujia.backend.dto.batch.ProductBatchUpdateRequest;
import com.yujia.backend.service.BatchArchiveService;
import com.yujia.backend.service.BatchInsightService;
import com.yujia.backend.service.ProductBatchService;
import com.yujia.backend.vo.BatchArchiveVO;
import com.yujia.backend.vo.BatchInsightVO;
import com.yujia.backend.vo.ProductBatchVO;
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
@RequestMapping("/api/batches")
@RequiredArgsConstructor
public class ProductBatchController {

    private final AuthPermissionService authPermissionService;
    private final ProductBatchService productBatchService;
    private final BatchArchiveService batchArchiveService;
    private final BatchInsightService batchInsightService;

    @GetMapping
    public ApiResponse<List<ProductBatchVO>> list(@RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long baseId,
            @RequestParam(required = false) Integer batchStatus) {
        authPermissionService.requireStaff();
        return ApiResponse.success(productBatchService.list(keyword, baseId, batchStatus));
    }

    @GetMapping("/page")
    public ApiResponse<PageResponse<ProductBatchVO>> page(@RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long baseId,
            @RequestParam(required = false) Integer batchStatus,
            @RequestParam(required = false) Integer pageNum,
            @RequestParam(required = false) Integer pageSize) {
        authPermissionService.requireStaff();
        return ApiResponse.success(productBatchService.page(keyword, baseId, batchStatus, pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public ApiResponse<ProductBatchVO> detail(@PathVariable Long id) {
        authPermissionService.requireStaff();
        return ApiResponse.success(productBatchService.detail(id));
    }

    @GetMapping("/{id}/archive")
    public ApiResponse<BatchArchiveVO> archive(@PathVariable Long id) {
        authPermissionService.requireStaff();
        return ApiResponse.success(batchArchiveService.archive(id));
    }

    @GetMapping("/{id}/insight")
    public ApiResponse<BatchInsightVO> insight(@PathVariable Long id) {
        authPermissionService.requireStaff();
        return ApiResponse.success(batchInsightService.insight(id));
    }

    @PostMapping
    public ApiResponse<ProductBatchVO> create(@Valid @RequestBody ProductBatchCreateRequest request) {
        authPermissionService.requireStaff();
        return ApiResponse.success(productBatchService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<ProductBatchVO> update(@PathVariable Long id,
            @Valid @RequestBody ProductBatchUpdateRequest request) {
        authPermissionService.requireStaff();
        return ApiResponse.success(productBatchService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        authPermissionService.requireAdmin();
        productBatchService.delete(id);
        return ApiResponse.success();
    }
}

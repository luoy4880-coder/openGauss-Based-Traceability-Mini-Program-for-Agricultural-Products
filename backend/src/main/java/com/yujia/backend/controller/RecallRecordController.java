package com.yujia.backend.controller;

import com.yujia.backend.common.auth.AuthPermissionService;
import com.yujia.backend.common.response.ApiResponse;
import com.yujia.backend.common.response.PageResponse;
import com.yujia.backend.dto.recall.RecallRecordCreateRequest;
import com.yujia.backend.entity.RecallRecord;
import com.yujia.backend.service.RecallRecordService;
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
@RequestMapping("/api/recalls")
@RequiredArgsConstructor
public class RecallRecordController {

    private final AuthPermissionService authPermissionService;
    private final RecallRecordService recallRecordService;

    @GetMapping
    public ApiResponse<List<RecallRecord>> list(@RequestParam(required = false) Long batchId,
                                                @RequestParam(required = false) Integer recallStatus) {
        authPermissionService.requireStaff();
        return ApiResponse.success(recallRecordService.list(batchId, recallStatus));
    }

    @GetMapping("/page")
    public ApiResponse<PageResponse<RecallRecord>> page(@RequestParam(required = false) Long batchId,
                                                        @RequestParam(required = false) Integer recallStatus,
                                                        @RequestParam(required = false) Integer pageNum,
                                                        @RequestParam(required = false) Integer pageSize) {
        authPermissionService.requireStaff();
        return ApiResponse.success(recallRecordService.page(batchId, recallStatus, pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public ApiResponse<RecallRecord> detail(@PathVariable Long id) {
        authPermissionService.requireStaff();
        return ApiResponse.success(recallRecordService.detail(id));
    }

    @PostMapping
    public ApiResponse<RecallRecord> create(@Valid @RequestBody RecallRecordCreateRequest request) {
        authPermissionService.requireStaff();
        return ApiResponse.success(recallRecordService.create(request));
    }

    @PutMapping("/{id}/close")
    public ApiResponse<RecallRecord> close(@PathVariable Long id) {
        authPermissionService.requireAdmin();
        return ApiResponse.success(recallRecordService.close(id));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        authPermissionService.requireAdmin();
        recallRecordService.delete(id);
        return ApiResponse.success();
    }
}

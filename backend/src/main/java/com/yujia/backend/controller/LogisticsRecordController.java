package com.yujia.backend.controller;

import com.yujia.backend.common.auth.AuthPermissionService;
import com.yujia.backend.common.response.ApiResponse;
import com.yujia.backend.dto.logistics.LogisticsRecordCreateRequest;
import com.yujia.backend.dto.logistics.LogisticsRecordUpdateRequest;
import com.yujia.backend.entity.LogisticsRecord;
import com.yujia.backend.service.LogisticsRecordService;
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
@RequestMapping("/api/logistics-records")
@RequiredArgsConstructor
public class LogisticsRecordController {

    private final AuthPermissionService authPermissionService;
    private final LogisticsRecordService logisticsRecordService;

    @GetMapping
    public ApiResponse<List<LogisticsRecord>> list(@RequestParam(required = false) Long batchId,
                                                   @RequestParam(required = false) Long itemId) {
        authPermissionService.requireStaff();
        return ApiResponse.success(logisticsRecordService.list(batchId, itemId));
    }

    @PostMapping
    public ApiResponse<LogisticsRecord> create(@Valid @RequestBody LogisticsRecordCreateRequest request) {
        authPermissionService.requireStaff();
        return ApiResponse.success(logisticsRecordService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<LogisticsRecord> update(@PathVariable Long id, @Valid @RequestBody LogisticsRecordUpdateRequest request) {
        authPermissionService.requireStaff();
        return ApiResponse.success(logisticsRecordService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        authPermissionService.requireAdmin();
        logisticsRecordService.delete(id);
        return ApiResponse.success();
    }
}

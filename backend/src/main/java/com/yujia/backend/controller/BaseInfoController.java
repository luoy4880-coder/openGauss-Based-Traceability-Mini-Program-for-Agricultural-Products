package com.yujia.backend.controller;

import com.yujia.backend.common.auth.AuthPermissionService;
import com.yujia.backend.common.response.ApiResponse;
import com.yujia.backend.common.response.PageResponse;
import com.yujia.backend.dto.base.BaseCreateRequest;
import com.yujia.backend.dto.base.BaseUpdateRequest;
import com.yujia.backend.entity.BaseInfo;
import com.yujia.backend.service.BaseInfoService;
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
@RequestMapping("/api/bases")
@RequiredArgsConstructor
public class BaseInfoController {

    private final AuthPermissionService authPermissionService;
    private final BaseInfoService baseInfoService;

    @GetMapping
    public ApiResponse<List<BaseInfo>> list(@RequestParam(required = false) String keyword,
                                            @RequestParam(required = false) Integer status) {
        authPermissionService.requireStaff();
        return ApiResponse.success(baseInfoService.list(keyword, status));
    }

    @GetMapping("/page")
    public ApiResponse<PageResponse<BaseInfo>> page(@RequestParam(required = false) String keyword,
                                                    @RequestParam(required = false) Integer status,
                                                    @RequestParam(required = false) Integer pageNum,
                                                    @RequestParam(required = false) Integer pageSize) {
        authPermissionService.requireStaff();
        return ApiResponse.success(baseInfoService.page(keyword, status, pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public ApiResponse<BaseInfo> detail(@PathVariable Long id) {
        authPermissionService.requireStaff();
        return ApiResponse.success(baseInfoService.detail(id));
    }

    @PostMapping
    public ApiResponse<BaseInfo> create(@Valid @RequestBody BaseCreateRequest request) {
        authPermissionService.requireStaff();
        return ApiResponse.success(baseInfoService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<BaseInfo> update(@PathVariable Long id,
                                        @Valid @RequestBody BaseUpdateRequest request) {
        authPermissionService.requireStaff();
        return ApiResponse.success(baseInfoService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        authPermissionService.requireAdmin();
        baseInfoService.delete(id);
        return ApiResponse.success();
    }
}

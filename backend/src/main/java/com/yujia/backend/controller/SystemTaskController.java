package com.yujia.backend.controller;

import com.yujia.backend.common.auth.AuthPermissionService;
import com.yujia.backend.common.response.ApiResponse;
import com.yujia.backend.common.response.PageResponse;
import com.yujia.backend.service.SystemTaskService;
import com.yujia.backend.vo.SystemTaskVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class SystemTaskController {

    private final AuthPermissionService authPermissionService;
    private final SystemTaskService systemTaskService;

    @GetMapping("/page")
    public ApiResponse<PageResponse<SystemTaskVO>> page(@RequestParam(required = false) Integer status,
                                                        @RequestParam(required = false) Long assigneeUserId,
                                                        @RequestParam(required = false) String keyword,
                                                        @RequestParam(required = false) Integer pageNum,
                                                        @RequestParam(required = false) Integer pageSize) {
        authPermissionService.requireStaff();
        return ApiResponse.success(systemTaskService.page(status, assigneeUserId, keyword, pageNum, pageSize));
    }

    @PostMapping("/{id}/claim")
    public ApiResponse<Void> claim(@PathVariable Long id) {
        authPermissionService.requireStaff();
        systemTaskService.claim(id);
        return ApiResponse.success();
    }

    @PostMapping("/{id}/complete")
    public ApiResponse<Void> complete(@PathVariable Long id) {
        authPermissionService.requireStaff();
        systemTaskService.complete(id);
        return ApiResponse.success();
    }

    @PostMapping("/{id}/reopen")
    public ApiResponse<Void> reopen(@PathVariable Long id) {
        authPermissionService.requireStaff();
        systemTaskService.reopen(id);
        return ApiResponse.success();
    }
}

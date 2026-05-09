package com.yujia.backend.controller;

import com.yujia.backend.common.auth.AuthPermissionService;
import com.yujia.backend.common.response.ApiResponse;
import com.yujia.backend.service.SysRoleService;
import com.yujia.backend.vo.RoleVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
public class SysRoleController {

    private final AuthPermissionService authPermissionService;
    private final SysRoleService sysRoleService;

    @GetMapping
    public ApiResponse<List<RoleVO>> list() {
        authPermissionService.requireStaff();
        return ApiResponse.success(sysRoleService.listAssignable());
    }
}

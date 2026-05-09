package com.yujia.backend.controller;

import com.yujia.backend.common.auth.AuthPermissionService;
import com.yujia.backend.common.response.ApiResponse;
import com.yujia.backend.service.CompanyService;
import com.yujia.backend.vo.CompanyVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/companies")
@RequiredArgsConstructor
public class CompanyController {

    private final AuthPermissionService authPermissionService;
    private final CompanyService companyService;

    @GetMapping
    public ApiResponse<List<CompanyVO>> list() {
        authPermissionService.requireStaff();
        return ApiResponse.success(companyService.list());
    }
}

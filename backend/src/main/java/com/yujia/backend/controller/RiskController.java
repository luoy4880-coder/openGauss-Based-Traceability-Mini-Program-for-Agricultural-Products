package com.yujia.backend.controller;

import com.yujia.backend.common.auth.AuthPermissionService;
import com.yujia.backend.common.response.ApiResponse;
import com.yujia.backend.service.RiskOverviewService;
import com.yujia.backend.vo.RiskOverviewVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/risk")
@RequiredArgsConstructor
public class RiskController {

    private final AuthPermissionService authPermissionService;
    private final RiskOverviewService riskOverviewService;

    @GetMapping("/overview")
    public ApiResponse<RiskOverviewVO> overview() {
        authPermissionService.requireStaff();
        return ApiResponse.success(riskOverviewService.overview());
    }
}

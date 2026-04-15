package com.yujia.backend.controller;

import com.yujia.backend.common.response.ApiResponse;
import com.yujia.backend.service.DashboardService;
import com.yujia.backend.vo.DashboardStatsVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/stats")
    public ApiResponse<DashboardStatsVO> stats() {
        return ApiResponse.success(dashboardService.stats());
    }
}

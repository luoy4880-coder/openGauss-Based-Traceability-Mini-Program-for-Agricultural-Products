package com.yujia.backend.service;

import com.yujia.backend.mapper.DashboardMapper;
import com.yujia.backend.vo.DashboardStatsVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final DashboardMapper dashboardMapper;
    private final CompanyScopeService companyScopeService;

    public DashboardStatsVO stats() {
        Long companyId = companyScopeService.currentCompanyScopeOrNull();
        DashboardStatsVO stats = new DashboardStatsVO();
        stats.setBaseCount(dashboardMapper.countBases(companyId));
        stats.setBatchCount(dashboardMapper.countBatches(companyId));
        stats.setTraceCodeCount(dashboardMapper.countTraceCodes(companyId));
        stats.setProductionRecordCount(dashboardMapper.countProductionRecords(companyId));
        stats.setInspectionReportCount(dashboardMapper.countInspectionReports(companyId));
        stats.setActiveRecallCount(dashboardMapper.countActiveRecalls(companyId));
        return stats;
    }
}

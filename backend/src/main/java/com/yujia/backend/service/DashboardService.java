package com.yujia.backend.service;

import com.yujia.backend.mapper.DashboardMapper;
import com.yujia.backend.vo.DashboardStatsVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DashboardService {

    private final DashboardMapper dashboardMapper;

    public DashboardStatsVO stats() {
        DashboardStatsVO stats = new DashboardStatsVO();
        stats.setBaseCount(dashboardMapper.countBases());
        stats.setBatchCount(dashboardMapper.countBatches());
        stats.setTraceCodeCount(dashboardMapper.countTraceCodes());
        stats.setProductionRecordCount(dashboardMapper.countProductionRecords());
        stats.setInspectionReportCount(dashboardMapper.countInspectionReports());
        stats.setActiveRecallCount(dashboardMapper.countActiveRecalls());
        return stats;
    }
}

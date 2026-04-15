package com.yujia.backend.vo;

import lombok.Data;

@Data
public class DashboardStatsVO {

    private long baseCount;

    private long batchCount;

    private long traceCodeCount;

    private long productionRecordCount;

    private long inspectionReportCount;

    private long activeRecallCount;
}

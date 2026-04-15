package com.yujia.backend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface DashboardMapper {

    @Select("SELECT COUNT(*) FROM base_info")
    long countBases();

    @Select("SELECT COUNT(*) FROM product_batch")
    long countBatches();

    @Select("SELECT COUNT(*) FROM trace_code")
    long countTraceCodes();

    @Select("SELECT COUNT(*) FROM production_record")
    long countProductionRecords();

    @Select("SELECT COUNT(*) FROM inspection_report")
    long countInspectionReports();

    @Select("SELECT COUNT(*) FROM recall_record WHERE recall_status = 1")
    long countActiveRecalls();
}

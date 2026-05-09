package com.yujia.backend.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface DashboardMapper {

    @Select("""
            <script>
            SELECT COUNT(*) FROM base_info
            <if test="companyId != null">WHERE company_id = #{companyId}</if>
            </script>
            """)
    long countBases(@Param("companyId") Long companyId);

    @Select("""
            <script>
            SELECT COUNT(*) FROM product_batch
            <if test="companyId != null">WHERE company_id = #{companyId}</if>
            </script>
            """)
    long countBatches(@Param("companyId") Long companyId);

    @Select("""
            <script>
            SELECT COUNT(*)
            FROM trace_code tc
            LEFT JOIN product_batch pb ON pb.id = tc.batch_id
            <if test="companyId != null">WHERE pb.company_id = #{companyId}</if>
            </script>
            """)
    long countTraceCodes(@Param("companyId") Long companyId);

    @Select("""
            <script>
            SELECT COUNT(*)
            FROM production_record pr
            LEFT JOIN product_batch pb ON pb.id = pr.batch_id
            <if test="companyId != null">WHERE pb.company_id = #{companyId}</if>
            </script>
            """)
    long countProductionRecords(@Param("companyId") Long companyId);

    @Select("""
            <script>
            SELECT COUNT(*)
            FROM inspection_report ir
            LEFT JOIN product_batch pb ON pb.id = ir.batch_id
            <if test="companyId != null">WHERE pb.company_id = #{companyId}</if>
            </script>
            """)
    long countInspectionReports(@Param("companyId") Long companyId);

    @Select("""
            <script>
            SELECT COUNT(*)
            FROM recall_record rr
            LEFT JOIN product_batch pb ON pb.id = rr.batch_id
            WHERE rr.recall_status = 1
            <if test="companyId != null">AND pb.company_id = #{companyId}</if>
            </script>
            """)
    long countActiveRecalls(@Param("companyId") Long companyId);
}

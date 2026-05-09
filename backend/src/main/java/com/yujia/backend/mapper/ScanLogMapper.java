package com.yujia.backend.mapper;

import com.yujia.backend.entity.ScanLog;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ScanLogMapper {

    @Insert("""
            INSERT INTO scan_log (
                trace_id, item_id, batch_id, scan_source, ip_address, user_agent,
                verify_result, risk_message, scanned_at
            ) VALUES (
                #{traceId}, #{itemId}, #{batchId}, #{scanSource}, #{ipAddress}, #{userAgent},
                #{verifyResult}, #{riskMessage}, CURRENT_TIMESTAMP
            )
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ScanLog scanLog);

    @Select("""
            SELECT id, trace_id, item_id, batch_id, scan_source, ip_address, user_agent,
                   verify_result, risk_message, scanned_at
            FROM scan_log
            WHERE trace_id = #{traceId}
            ORDER BY id DESC
            LIMIT #{limit}
            """)
    List<ScanLog> selectRecentByTraceId(@Param("traceId") String traceId, @Param("limit") int limit);

    @Select("SELECT COUNT(*) FROM scan_log WHERE trace_id = #{traceId}")
    long countByTraceId(String traceId);

    @Select("SELECT COUNT(*) FROM scan_log WHERE batch_id = #{batchId}")
    long countByBatchId(Long batchId);

    @Select("SELECT COUNT(*) FROM scan_log WHERE batch_id = #{batchId} AND verify_result = 0")
    long countAbnormalByBatchId(Long batchId);

    @Select("""
            <script>
            SELECT COUNT(*)
            FROM scan_log sl
            LEFT JOIN product_batch pb ON pb.id = sl.batch_id
            WHERE sl.verify_result = 0
            <if test="companyId != null">
                AND pb.company_id = #{companyId}
            </if>
            </script>
            """)
    long countAllAbnormal(@Param("companyId") Long companyId);
}

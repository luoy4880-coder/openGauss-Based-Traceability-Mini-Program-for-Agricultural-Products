package com.yujia.backend.mapper;

import com.yujia.backend.entity.TraceCode;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface TraceCodeMapper {

    @Select("""
            SELECT id, trace_id, batch_id, qr_content, sign_value, code_status, generated_at
            FROM trace_code
            WHERE batch_id = #{batchId}
            ORDER BY id DESC
            """)
    List<TraceCode> selectByBatchId(Long batchId);

    @Select("""
            SELECT id, trace_id, batch_id, qr_content, sign_value, code_status, generated_at
            FROM trace_code
            WHERE trace_id = #{traceId}
            """)
    TraceCode selectByTraceId(String traceId);

    @Insert("""
            INSERT INTO trace_code (
                trace_id, batch_id, qr_content, sign_value, code_status, generated_at
            ) VALUES (
                #{traceId}, #{batchId}, #{qrContent}, #{signValue}, #{codeStatus}, CURRENT_TIMESTAMP
            )
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(TraceCode traceCode);
}

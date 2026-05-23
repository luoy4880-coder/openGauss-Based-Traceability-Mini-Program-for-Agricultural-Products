package com.yujia.backend.mapper;

import com.yujia.backend.entity.LogisticsRecord;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface LogisticsRecordMapper {

    @Select("""
            <script>
            SELECT lr.id, lr.batch_id, lr.item_id, lr.logistics_code, lr.node_type, lr.node_name, lr.operation_time,
                   lr.operator_name, lr.contact_phone, lr.location, lr.temperature, lr.humidity, lr.attachment_url,
                   lr.remark, lr.created_at
            FROM logistics_record lr
            LEFT JOIN product_batch pb ON pb.id = lr.batch_id
            <where>
                <if test="companyId != null">
                    AND pb.company_id = #{companyId}
                </if>
                <if test="batchId != null">
                    AND lr.batch_id = #{batchId}
                </if>
                <if test="itemId != null">
                    AND (lr.item_id = #{itemId} OR lr.item_id IS NULL)
                </if>
            </where>
            ORDER BY lr.operation_time ASC, lr.id ASC
            </script>
            """)
    List<LogisticsRecord> selectList(@Param("companyId") Long companyId, @Param("batchId") Long batchId, @Param("itemId") Long itemId);

    @Insert("""
            INSERT INTO logistics_record (
                batch_id, item_id, logistics_code, node_type, node_name, operation_time,
                operator_name, contact_phone, location, temperature, humidity, attachment_url,
                remark, created_at
            ) VALUES (
                #{batchId}, #{itemId}, #{logisticsCode}, #{nodeType}, #{nodeName}, #{operationTime},
                #{operatorName}, #{contactPhone}, #{location}, #{temperature}, #{humidity}, #{attachmentUrl},
                #{remark}, CURRENT_TIMESTAMP
            )
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(LogisticsRecord logisticsRecord);

    @Select("""
            SELECT id, batch_id, item_id, logistics_code, node_type, node_name, operation_time,
                   operator_name, contact_phone, location, temperature, humidity, attachment_url,
                   remark, created_at
            FROM logistics_record
            WHERE id = #{id}
            """)
    LogisticsRecord selectById(Long id);

    @org.apache.ibatis.annotations.Update("""
            UPDATE logistics_record
            SET batch_id = #{batchId},
                item_id = #{itemId},
                node_type = #{nodeType},
                node_name = #{nodeName},
                operation_time = #{operationTime},
                operator_name = #{operatorName},
                contact_phone = #{contactPhone},
                location = #{location},
                temperature = #{temperature},
                humidity = #{humidity},
                attachment_url = #{attachmentUrl},
                remark = #{remark}
            WHERE id = #{id}
            """)
    int updateById(LogisticsRecord logisticsRecord);

    @org.apache.ibatis.annotations.Delete("""
            DELETE FROM logistics_record
            WHERE id = #{id}
            """)
    int deleteById(Long id);
}

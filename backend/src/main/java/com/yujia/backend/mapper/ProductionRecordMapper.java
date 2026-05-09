package com.yujia.backend.mapper;

import com.yujia.backend.entity.ProductionRecord;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ProductionRecordMapper {

    @Select("""
            <script>
            SELECT pr.id, pr.batch_id, pr.record_type, pr.operation_time, pr.operator_name, pr.material_name, pr.dosage,
                   pr.content, pr.attachment_url, pr.created_at
            FROM production_record pr
            LEFT JOIN product_batch pb ON pb.id = pr.batch_id
            <where>
                <if test="companyId != null">
                    AND pb.company_id = #{companyId}
                </if>
                <if test="batchId != null">
                    AND pr.batch_id = #{batchId}
                </if>
                <if test="recordType != null and recordType != ''">
                    AND pr.record_type = #{recordType}
                </if>
            </where>
            ORDER BY pr.operation_time DESC, pr.id DESC
            </script>
            """)
    List<ProductionRecord> selectList(@Param("companyId") Long companyId, @Param("batchId") Long batchId, @Param("recordType") String recordType);

    @Select("""
            <script>
            SELECT COUNT(*)
            FROM production_record pr
            LEFT JOIN product_batch pb ON pb.id = pr.batch_id
            <where>
                <if test="companyId != null">
                    AND pb.company_id = #{companyId}
                </if>
                <if test="batchId != null">
                    AND pr.batch_id = #{batchId}
                </if>
                <if test="recordType != null and recordType != ''">
                    AND pr.record_type = #{recordType}
                </if>
            </where>
            </script>
            """)
    long countList(@Param("companyId") Long companyId, @Param("batchId") Long batchId, @Param("recordType") String recordType);

    @Select("""
            <script>
            SELECT pr.id, pr.batch_id, pr.record_type, pr.operation_time, pr.operator_name, pr.material_name, pr.dosage,
                   pr.content, pr.attachment_url, pr.created_at
            FROM production_record pr
            LEFT JOIN product_batch pb ON pb.id = pr.batch_id
            <where>
                <if test="companyId != null">
                    AND pb.company_id = #{companyId}
                </if>
                <if test="batchId != null">
                    AND pr.batch_id = #{batchId}
                </if>
                <if test="recordType != null and recordType != ''">
                    AND pr.record_type = #{recordType}
                </if>
            </where>
            ORDER BY pr.operation_time DESC, pr.id DESC
            LIMIT #{limit} OFFSET #{offset}
            </script>
            """)
    List<ProductionRecord> selectPage(@Param("companyId") Long companyId,
                                      @Param("batchId") Long batchId,
                                      @Param("recordType") String recordType,
                                      @Param("offset") long offset,
                                      @Param("limit") int limit);

    @Select("""
            SELECT id, batch_id, record_type, operation_time, operator_name, material_name, dosage,
                   content, attachment_url, created_at
            FROM production_record
            WHERE id = #{id}
            """)
    ProductionRecord selectById(Long id);

    @Insert("""
            INSERT INTO production_record (
                batch_id, record_type, operation_time, operator_name, material_name, dosage,
                content, attachment_url, created_at
            ) VALUES (
                #{batchId}, #{recordType}, #{operationTime}, #{operatorName}, #{materialName}, #{dosage},
                #{content}, #{attachmentUrl}, CURRENT_TIMESTAMP
            )
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ProductionRecord productionRecord);

    @Update("""
            UPDATE production_record
            SET batch_id = #{batchId},
                record_type = #{recordType},
                operation_time = #{operationTime},
                operator_name = #{operatorName},
                material_name = #{materialName},
                dosage = #{dosage},
                content = #{content},
                attachment_url = #{attachmentUrl}
            WHERE id = #{id}
            """)
    int updateById(ProductionRecord productionRecord);

    @org.apache.ibatis.annotations.Delete("""
            DELETE FROM production_record
            WHERE id = #{id}
            """)
    int deleteById(Long id);
}

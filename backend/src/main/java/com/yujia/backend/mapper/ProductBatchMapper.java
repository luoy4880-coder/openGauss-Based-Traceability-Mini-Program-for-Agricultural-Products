package com.yujia.backend.mapper;

import com.yujia.backend.entity.ProductBatch;
import com.yujia.backend.vo.ProductBatchVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ProductBatchMapper {

    @Select("""
            <script>
            SELECT pb.id, pb.batch_code, pb.base_id, bi.base_name, pb.product_name, pb.product_category,
                   pb.planting_date, pb.expected_harvest_date, pb.actual_harvest_date, pb.quantity, pb.unit,
                   pb.batch_status, pb.recall_status, pb.remark, pb.created_at, pb.updated_at
            FROM product_batch pb
            LEFT JOIN base_info bi ON pb.base_id = bi.id
            <where>
                <if test="keyword != null and keyword != ''">
                    AND (pb.batch_code LIKE CONCAT('%', #{keyword}, '%')
                    OR pb.product_name LIKE CONCAT('%', #{keyword}, '%'))
                </if>
                <if test="baseId != null">
                    AND pb.base_id = #{baseId}
                </if>
                <if test="batchStatus != null">
                    AND pb.batch_status = #{batchStatus}
                </if>
            </where>
            ORDER BY pb.id DESC
            </script>
            """)
    List<ProductBatchVO> selectList(@Param("keyword") String keyword,
                                    @Param("baseId") Long baseId,
                                    @Param("batchStatus") Integer batchStatus);

    @Select("""
            <script>
            SELECT COUNT(*)
            FROM product_batch pb
            <where>
                <if test="keyword != null and keyword != ''">
                    AND (pb.batch_code LIKE CONCAT('%', #{keyword}, '%')
                    OR pb.product_name LIKE CONCAT('%', #{keyword}, '%'))
                </if>
                <if test="baseId != null">
                    AND pb.base_id = #{baseId}
                </if>
                <if test="batchStatus != null">
                    AND pb.batch_status = #{batchStatus}
                </if>
            </where>
            </script>
            """)
    long countList(@Param("keyword") String keyword,
                   @Param("baseId") Long baseId,
                   @Param("batchStatus") Integer batchStatus);

    @Select("""
            <script>
            SELECT pb.id, pb.batch_code, pb.base_id, bi.base_name, pb.product_name, pb.product_category,
                   pb.planting_date, pb.expected_harvest_date, pb.actual_harvest_date, pb.quantity, pb.unit,
                   pb.batch_status, pb.recall_status, pb.remark, pb.created_at, pb.updated_at
            FROM product_batch pb
            LEFT JOIN base_info bi ON pb.base_id = bi.id
            <where>
                <if test="keyword != null and keyword != ''">
                    AND (pb.batch_code LIKE CONCAT('%', #{keyword}, '%')
                    OR pb.product_name LIKE CONCAT('%', #{keyword}, '%'))
                </if>
                <if test="baseId != null">
                    AND pb.base_id = #{baseId}
                </if>
                <if test="batchStatus != null">
                    AND pb.batch_status = #{batchStatus}
                </if>
            </where>
            ORDER BY pb.id DESC
            LIMIT #{limit} OFFSET #{offset}
            </script>
            """)
    List<ProductBatchVO> selectPage(@Param("keyword") String keyword,
                                    @Param("baseId") Long baseId,
                                    @Param("batchStatus") Integer batchStatus,
                                    @Param("offset") long offset,
                                    @Param("limit") int limit);

    @Select("""
            SELECT pb.id, pb.batch_code, pb.base_id, bi.base_name, pb.product_name, pb.product_category,
                   pb.planting_date, pb.expected_harvest_date, pb.actual_harvest_date, pb.quantity, pb.unit,
                   pb.batch_status, pb.recall_status, pb.remark, pb.created_at, pb.updated_at
            FROM product_batch pb
            LEFT JOIN base_info bi ON pb.base_id = bi.id
            WHERE pb.id = #{id}
            """)
    ProductBatchVO selectDetailById(Long id);

    @Select("""
            SELECT id, batch_code, base_id, product_name, product_category, planting_date,
                   expected_harvest_date, actual_harvest_date, quantity, unit,
                   batch_status, recall_status, remark, created_at, updated_at
            FROM product_batch
            WHERE batch_code = #{batchCode}
            """)
    ProductBatch selectByBatchCode(String batchCode);

    @Insert("""
            INSERT INTO product_batch (
                batch_code, base_id, product_name, product_category, planting_date,
                expected_harvest_date, actual_harvest_date, quantity, unit,
                batch_status, recall_status, remark, created_at, updated_at
            ) VALUES (
                #{batchCode}, #{baseId}, #{productName}, #{productCategory}, #{plantingDate},
                #{expectedHarvestDate}, #{actualHarvestDate}, #{quantity}, #{unit},
                #{batchStatus}, #{recallStatus}, #{remark}, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
            )
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ProductBatch productBatch);

    @Update("""
            UPDATE product_batch
            SET base_id = #{baseId},
                product_name = #{productName},
                product_category = #{productCategory},
                planting_date = #{plantingDate},
                expected_harvest_date = #{expectedHarvestDate},
                actual_harvest_date = #{actualHarvestDate},
                quantity = #{quantity},
                unit = #{unit},
                batch_status = #{batchStatus},
                recall_status = #{recallStatus},
                remark = #{remark},
                updated_at = CURRENT_TIMESTAMP
            WHERE id = #{id}
            """)
    int updateById(ProductBatch productBatch);

    @Update("""
            UPDATE product_batch
            SET recall_status = #{recallStatus},
                updated_at = CURRENT_TIMESTAMP
            WHERE id = #{id}
            """)
    int updateRecallStatus(@Param("id") Long id, @Param("recallStatus") Integer recallStatus);

    @Select("SELECT COUNT(*) FROM production_record WHERE batch_id = #{batchId}")
    long countProductionRecordsByBatchId(Long batchId);

    @Select("SELECT COUNT(*) FROM inspection_report WHERE batch_id = #{batchId}")
    long countInspectionReportsByBatchId(Long batchId);

    @Select("SELECT COUNT(*) FROM trace_code WHERE batch_id = #{batchId}")
    long countTraceCodesByBatchId(Long batchId);

    @Select("SELECT COUNT(*) FROM recall_record WHERE batch_id = #{batchId}")
    long countRecallRecordsByBatchId(Long batchId);

    @org.apache.ibatis.annotations.Delete("""
            DELETE FROM product_batch
            WHERE id = #{id}
            """)
    int deleteById(Long id);
}

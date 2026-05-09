package com.yujia.backend.mapper;

import com.yujia.backend.entity.ProductItem;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface ProductItemMapper {

    @Select("""
            SELECT id, batch_id, item_code, trace_id, qr_content, sign_value, item_status,
                   scan_count, first_scanned_at, last_scanned_at, generated_at, updated_at
            FROM product_item
            WHERE batch_id = #{batchId}
            ORDER BY id DESC
            """)
    List<ProductItem> selectByBatchId(Long batchId);

    @Select("""
            SELECT id, batch_id, item_code, trace_id, qr_content, sign_value, item_status,
                   scan_count, first_scanned_at, last_scanned_at, generated_at, updated_at
            FROM product_item
            WHERE trace_id = #{traceId}
            """)
    ProductItem selectByTraceId(String traceId);

    @Select("SELECT COUNT(*) FROM product_item WHERE batch_id = #{batchId}")
    long countByBatchId(Long batchId);

    @Insert("""
            INSERT INTO product_item (
                batch_id, item_code, trace_id, qr_content, sign_value, item_status,
                scan_count, generated_at, updated_at
            ) VALUES (
                #{batchId}, #{itemCode}, #{traceId}, #{qrContent}, #{signValue}, #{itemStatus},
                0, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
            )
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ProductItem productItem);

    @Update("""
            UPDATE product_item
            SET scan_count = scan_count + 1,
                first_scanned_at = CASE WHEN first_scanned_at IS NULL THEN CURRENT_TIMESTAMP ELSE first_scanned_at END,
                last_scanned_at = CURRENT_TIMESTAMP,
                updated_at = CURRENT_TIMESTAMP
            WHERE id = #{id}
            """)
    int incrementScanCount(Long id);

    @Update("""
            UPDATE product_item
            SET item_status = #{itemStatus}, updated_at = CURRENT_TIMESTAMP
            WHERE batch_id = #{batchId}
            """)
    int updateStatusByBatchId(@Param("batchId") Long batchId, @Param("itemStatus") Integer itemStatus);
}

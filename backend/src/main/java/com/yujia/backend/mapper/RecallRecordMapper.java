package com.yujia.backend.mapper;

import com.yujia.backend.entity.RecallRecord;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface RecallRecordMapper {

    @Select("""
            <script>
            SELECT rr.id, rr.batch_id, rr.recall_level, rr.reason, rr.recall_status, rr.notice_time, rr.closed_at, rr.created_at
            FROM recall_record rr
            LEFT JOIN product_batch pb ON pb.id = rr.batch_id
            <where>
                <if test="companyId != null">
                    AND pb.company_id = #{companyId}
                </if>
                <if test="batchId != null">
                    AND rr.batch_id = #{batchId}
                </if>
                <if test="recallStatus != null">
                    AND rr.recall_status = #{recallStatus}
                </if>
            </where>
            ORDER BY rr.id DESC
            </script>
            """)
    List<RecallRecord> selectList(@Param("companyId") Long companyId, @Param("batchId") Long batchId, @Param("recallStatus") Integer recallStatus);

    @Select("""
            <script>
            SELECT COUNT(*)
            FROM recall_record rr
            LEFT JOIN product_batch pb ON pb.id = rr.batch_id
            <where>
                <if test="companyId != null">
                    AND pb.company_id = #{companyId}
                </if>
                <if test="batchId != null">
                    AND rr.batch_id = #{batchId}
                </if>
                <if test="recallStatus != null">
                    AND rr.recall_status = #{recallStatus}
                </if>
            </where>
            </script>
            """)
    long countList(@Param("companyId") Long companyId, @Param("batchId") Long batchId, @Param("recallStatus") Integer recallStatus);

    @Select("""
            <script>
            SELECT rr.id, rr.batch_id, rr.recall_level, rr.reason, rr.recall_status, rr.notice_time, rr.closed_at, rr.created_at
            FROM recall_record rr
            LEFT JOIN product_batch pb ON pb.id = rr.batch_id
            <where>
                <if test="companyId != null">
                    AND pb.company_id = #{companyId}
                </if>
                <if test="batchId != null">
                    AND rr.batch_id = #{batchId}
                </if>
                <if test="recallStatus != null">
                    AND rr.recall_status = #{recallStatus}
                </if>
            </where>
            ORDER BY rr.id DESC
            LIMIT #{limit} OFFSET #{offset}
            </script>
            """)
    List<RecallRecord> selectPage(@Param("companyId") Long companyId,
                                  @Param("batchId") Long batchId,
                                  @Param("recallStatus") Integer recallStatus,
                                  @Param("offset") long offset,
                                  @Param("limit") int limit);

    @Select("""
            SELECT id, batch_id, recall_level, reason, recall_status, notice_time, closed_at, created_at
            FROM recall_record
            WHERE id = #{id}
            """)
    RecallRecord selectById(Long id);

    @Select("""
            SELECT id, batch_id, recall_level, reason, recall_status, notice_time, closed_at, created_at
            FROM recall_record
            WHERE batch_id = #{batchId}
            ORDER BY id DESC
            LIMIT 1
            """)
    RecallRecord selectLatestByBatchId(Long batchId);

    @Insert("""
            INSERT INTO recall_record (
                batch_id, recall_level, reason, recall_status, notice_time, created_at
            ) VALUES (
                #{batchId}, #{recallLevel}, #{reason}, #{recallStatus}, #{noticeTime}, CURRENT_TIMESTAMP
            )
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(RecallRecord recallRecord);

    @Update("""
            UPDATE recall_record
            SET recall_status = #{recallStatus},
                closed_at = #{closedAt}
            WHERE id = #{id}
            """)
    int updateStatus(RecallRecord recallRecord);

    @org.apache.ibatis.annotations.Delete("""
            DELETE FROM recall_record
            WHERE id = #{id}
            """)
    int deleteById(Long id);
}

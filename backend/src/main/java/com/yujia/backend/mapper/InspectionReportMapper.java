package com.yujia.backend.mapper;

import com.yujia.backend.entity.InspectionReport;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface InspectionReportMapper {

    @Select("""
            <script>
            SELECT id, batch_id, report_no, agency_name, inspector_name, inspection_time,
                   result_status, conclusion, report_url, created_at
            FROM inspection_report
            <where>
                <if test="batchId != null">
                    AND batch_id = #{batchId}
                </if>
                <if test="resultStatus != null">
                    AND result_status = #{resultStatus}
                </if>
            </where>
            ORDER BY inspection_time DESC, id DESC
            </script>
            """)
    List<InspectionReport> selectList(@Param("batchId") Long batchId, @Param("resultStatus") Integer resultStatus);

    @Select("""
            <script>
            SELECT COUNT(*)
            FROM inspection_report
            <where>
                <if test="batchId != null">
                    AND batch_id = #{batchId}
                </if>
                <if test="resultStatus != null">
                    AND result_status = #{resultStatus}
                </if>
            </where>
            </script>
            """)
    long countList(@Param("batchId") Long batchId, @Param("resultStatus") Integer resultStatus);

    @Select("""
            <script>
            SELECT id, batch_id, report_no, agency_name, inspector_name, inspection_time,
                   result_status, conclusion, report_url, created_at
            FROM inspection_report
            <where>
                <if test="batchId != null">
                    AND batch_id = #{batchId}
                </if>
                <if test="resultStatus != null">
                    AND result_status = #{resultStatus}
                </if>
            </where>
            ORDER BY inspection_time DESC, id DESC
            LIMIT #{limit} OFFSET #{offset}
            </script>
            """)
    List<InspectionReport> selectPage(@Param("batchId") Long batchId,
                                      @Param("resultStatus") Integer resultStatus,
                                      @Param("offset") long offset,
                                      @Param("limit") int limit);

    @Select("""
            SELECT id, batch_id, report_no, agency_name, inspector_name, inspection_time,
                   result_status, conclusion, report_url, created_at
            FROM inspection_report
            WHERE id = #{id}
            """)
    InspectionReport selectById(Long id);

    @Select("""
            SELECT id, batch_id, report_no, agency_name, inspector_name, inspection_time,
                   result_status, conclusion, report_url, created_at
            FROM inspection_report
            WHERE report_no = #{reportNo}
            """)
    InspectionReport selectByReportNo(String reportNo);

    @Insert("""
            INSERT INTO inspection_report (
                batch_id, report_no, agency_name, inspector_name, inspection_time,
                result_status, conclusion, report_url, created_at
            ) VALUES (
                #{batchId}, #{reportNo}, #{agencyName}, #{inspectorName}, #{inspectionTime},
                #{resultStatus}, #{conclusion}, #{reportUrl}, CURRENT_TIMESTAMP
            )
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(InspectionReport inspectionReport);

    @Update("""
            UPDATE inspection_report
            SET batch_id = #{batchId},
                agency_name = #{agencyName},
                inspector_name = #{inspectorName},
                inspection_time = #{inspectionTime},
                result_status = #{resultStatus},
                conclusion = #{conclusion},
                report_url = #{reportUrl}
            WHERE id = #{id}
            """)
    int updateById(InspectionReport inspectionReport);

    @org.apache.ibatis.annotations.Delete("""
            DELETE FROM inspection_report
            WHERE id = #{id}
            """)
    int deleteById(Long id);
}

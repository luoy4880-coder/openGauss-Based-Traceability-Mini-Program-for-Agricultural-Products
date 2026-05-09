package com.yujia.backend.mapper;

import com.yujia.backend.entity.UserFeedback;
import com.yujia.backend.vo.FeedbackTaskVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface UserFeedbackMapper {

    @Insert("""
            INSERT INTO user_feedback (
                user_id, type, content, contact, trace_id, batch_id, company_id,
                ai_category, ai_priority, risk_level, urgent_flag, ai_summary,
                assignee_user_id, linked_task_id, linked_recall_id, handle_note, status, created_at, updated_at
            ) VALUES (
                #{userId}, #{type}, #{content}, #{contact}, #{traceId}, #{batchId}, #{companyId},
                #{aiCategory}, #{aiPriority}, #{riskLevel}, #{urgentFlag}, #{aiSummary},
                #{assigneeUserId}, #{linkedTaskId}, #{linkedRecallId}, #{handleNote}, #{status}, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
            )
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(UserFeedback feedback);

    @Select("""
            SELECT id, user_id, type, content, contact, trace_id, batch_id, company_id,
                   ai_category, ai_priority, risk_level, urgent_flag, ai_summary,
                   assignee_user_id, linked_task_id, linked_recall_id, handle_note, status, created_at, handled_at, updated_at
            FROM user_feedback
            WHERE user_id = #{userId}
            ORDER BY id DESC
            LIMIT #{limit}
            """)
    List<UserFeedback> selectLatestByUserId(@Param("userId") Long userId, @Param("limit") int limit);

    @Select("""
            SELECT id, user_id, type, content, contact, trace_id, batch_id, company_id,
                   ai_category, ai_priority, risk_level, urgent_flag, ai_summary,
                   assignee_user_id, linked_task_id, linked_recall_id, handle_note, status, created_at, handled_at, updated_at
            FROM user_feedback
            WHERE id = #{id}
            """)
    UserFeedback selectById(Long id);

    @Select("""
            <script>
            SELECT COUNT(*)
            FROM user_feedback uf
            <where>
                <if test="companyId != null">
                    AND uf.company_id = #{companyId}
                </if>
                <if test="keyword != null and keyword != ''">
                    AND (uf.content LIKE CONCAT('%', #{keyword}, '%')
                    OR uf.contact LIKE CONCAT('%', #{keyword}, '%')
                    OR uf.trace_id LIKE CONCAT('%', #{keyword}, '%'))
                </if>
                <if test="category != null and category != ''">
                    AND uf.ai_category = #{category}
                </if>
                <if test="priority != null">
                    AND uf.ai_priority = #{priority}
                </if>
                <if test="riskLevel != null and riskLevel != ''">
                    AND uf.risk_level = #{riskLevel}
                </if>
                <if test="status != null">
                    AND uf.status = #{status}
                </if>
                <if test="assigneeUserId != null">
                    AND uf.assignee_user_id = #{assigneeUserId}
                </if>
            </where>
            </script>
            """)
    long countPage(@Param("companyId") Long companyId,
                   @Param("keyword") String keyword,
                   @Param("category") String category,
                   @Param("priority") Integer priority,
                   @Param("riskLevel") String riskLevel,
                   @Param("status") Integer status,
                   @Param("assigneeUserId") Long assigneeUserId);

    @Select("""
            <script>
            SELECT uf.id, uf.user_id, uf.type, uf.content, uf.contact, uf.trace_id,
                   COALESCE(uf.batch_id, pi.batch_id, tc.batch_id) AS batch_id,
                   uf.ai_category, uf.ai_priority, uf.risk_level, uf.urgent_flag, uf.ai_summary,
                   uf.assignee_user_id, su.real_name AS assignee_name,
                   uf.linked_task_id, uf.linked_recall_id,
                   uf.handle_note, uf.status, uf.created_at, uf.handled_at, uf.updated_at
            FROM user_feedback uf
            LEFT JOIN sys_user su ON su.id = uf.assignee_user_id
            LEFT JOIN product_item pi ON pi.trace_id = uf.trace_id
            LEFT JOIN trace_code tc ON tc.trace_id = uf.trace_id
            <where>
                <if test="companyId != null">
                    AND uf.company_id = #{companyId}
                </if>
                <if test="keyword != null and keyword != ''">
                    AND (uf.content LIKE CONCAT('%', #{keyword}, '%')
                    OR uf.contact LIKE CONCAT('%', #{keyword}, '%')
                    OR uf.trace_id LIKE CONCAT('%', #{keyword}, '%'))
                </if>
                <if test="category != null and category != ''">
                    AND uf.ai_category = #{category}
                </if>
                <if test="priority != null">
                    AND uf.ai_priority = #{priority}
                </if>
                <if test="riskLevel != null and riskLevel != ''">
                    AND uf.risk_level = #{riskLevel}
                </if>
                <if test="status != null">
                    AND uf.status = #{status}
                </if>
                <if test="assigneeUserId != null">
                    AND uf.assignee_user_id = #{assigneeUserId}
                </if>
            </where>
            ORDER BY
              CASE uf.status
                WHEN 0 THEN 0
                WHEN 1 THEN 1
                ELSE 2
              END,
              CASE uf.ai_priority
                WHEN 1 THEN 0
                WHEN 2 THEN 1
                ELSE 2
              END,
              uf.id DESC
            LIMIT #{limit} OFFSET #{offset}
            </script>
            """)
    List<FeedbackTaskVO> selectPage(@Param("companyId") Long companyId,
                                    @Param("keyword") String keyword,
                                    @Param("category") String category,
                                    @Param("priority") Integer priority,
                                    @Param("riskLevel") String riskLevel,
                                    @Param("status") Integer status,
                                    @Param("assigneeUserId") Long assigneeUserId,
                                    @Param("offset") long offset,
                                    @Param("limit") int limit);

    @Update("""
            UPDATE user_feedback
            SET status = #{status},
                assignee_user_id = #{assigneeUserId},
                linked_task_id = #{linkedTaskId},
                linked_recall_id = #{linkedRecallId},
                handle_note = #{handleNote},
                handled_at = CASE WHEN #{status} = 2 THEN CURRENT_TIMESTAMP ELSE handled_at END,
                updated_at = CURRENT_TIMESTAMP
            WHERE id = #{id}
            """)
    int updateHandle(UserFeedback feedback);

    @Select("""
            SELECT COUNT(*)
            FROM user_feedback uf
            LEFT JOIN product_item pi ON pi.trace_id = uf.trace_id
            LEFT JOIN trace_code tc ON tc.trace_id = uf.trace_id
            WHERE COALESCE(pi.batch_id, tc.batch_id) = #{batchId}
            """)
    long countByBatchId(Long batchId);

    @Select("""
            SELECT COUNT(*)
            FROM user_feedback uf
            LEFT JOIN product_item pi ON pi.trace_id = uf.trace_id
            LEFT JOIN trace_code tc ON tc.trace_id = uf.trace_id
            WHERE COALESCE(pi.batch_id, tc.batch_id) = #{batchId}
              AND uf.ai_priority = 1
            """)
    long countHighPriorityByBatchId(Long batchId);

    @Select("""
            <script>
            SELECT COUNT(*) FROM user_feedback
            WHERE status = 0
            <if test="companyId != null">AND company_id = #{companyId}</if>
            </script>
            """)
    long countPending(@Param("companyId") Long companyId);

    @Select("""
            <script>
            SELECT COUNT(*)
            FROM user_feedback
            WHERE status IN (0, 1)
              AND risk_level = 'HIGH'
            <if test="companyId != null">AND company_id = #{companyId}</if>
            </script>
            """)
    long countHighRiskPending(@Param("companyId") Long companyId);

    @Select("""
            <script>
            SELECT COUNT(*)
            FROM user_feedback
            WHERE status IN (0, 1)
              AND urgent_flag = 1
            <if test="companyId != null">AND company_id = #{companyId}</if>
            </script>
            """)
    long countUrgentPending(@Param("companyId") Long companyId);

    @Select("""
            <script>
            SELECT uf.id, uf.user_id, uf.type, uf.content, uf.trace_id, uf.contact,
                   COALESCE(uf.batch_id, pi.batch_id, tc.batch_id) AS batch_id,
                   uf.ai_category, uf.ai_priority, uf.risk_level, uf.urgent_flag, uf.ai_summary,
                   uf.assignee_user_id, su.real_name AS assignee_name,
                   uf.linked_task_id, uf.linked_recall_id,
                   uf.handle_note, uf.status, uf.created_at, uf.handled_at, uf.updated_at
            FROM user_feedback uf
            LEFT JOIN sys_user su ON su.id = uf.assignee_user_id
            LEFT JOIN product_item pi ON pi.trace_id = uf.trace_id
            LEFT JOIN trace_code tc ON tc.trace_id = uf.trace_id
            WHERE uf.status IN (0, 1)
              AND uf.risk_level = 'HIGH'
            <if test="companyId != null">AND uf.company_id = #{companyId}</if>
            ORDER BY uf.id DESC
            LIMIT #{limit}
            </script>
            """)
    List<FeedbackTaskVO> selectLatestHighRisk(@Param("companyId") Long companyId, @Param("limit") int limit);

    @Select("""
            SELECT COALESCE(uf.batch_id, pi.batch_id, tc.batch_id)
            FROM user_feedback uf
            LEFT JOIN product_item pi ON pi.trace_id = uf.trace_id
            LEFT JOIN trace_code tc ON tc.trace_id = uf.trace_id
            WHERE uf.id = #{feedbackId}
            """)
    Long selectBatchIdByFeedbackId(Long feedbackId);
}

package com.yujia.backend.mapper;

import com.yujia.backend.entity.SystemTask;
import com.yujia.backend.vo.SystemTaskVO;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface SystemTaskMapper {

    @Select("""
            SELECT id, task_type, biz_type, biz_id, title, description, priority, status,
                   assignee_user_id, claimed_at, completed_by_user_id, source_type, due_at,
                   completed_at, created_at, updated_at
            FROM system_task
            WHERE id = #{id}
            """)
    SystemTask selectById(Long id);

    @Select("""
            SELECT id, task_type, biz_type, biz_id, title, description, priority, status,
                   assignee_user_id, claimed_at, completed_by_user_id, source_type, due_at,
                   completed_at, created_at, updated_at
            FROM system_task
            WHERE task_type = #{taskType} AND biz_type = #{bizType} AND biz_id = #{bizId}
            """)
    SystemTask selectByUnique(@Param("taskType") String taskType,
                              @Param("bizType") String bizType,
                              @Param("bizId") Long bizId);

    @Insert("""
            INSERT INTO system_task (
                task_type, biz_type, biz_id, title, description, priority, status,
                assignee_user_id, claimed_at, completed_by_user_id, source_type, due_at,
                completed_at, created_at, updated_at
            ) VALUES (
                #{taskType}, #{bizType}, #{bizId}, #{title}, #{description}, #{priority}, #{status},
                #{assigneeUserId}, #{claimedAt}, #{completedByUserId}, #{sourceType}, #{dueAt},
                #{completedAt}, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
            )
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(SystemTask task);

    @Update("""
            UPDATE system_task
            SET title = #{title},
                description = #{description},
                priority = #{priority},
                status = #{status},
                assignee_user_id = #{assigneeUserId},
                claimed_at = #{claimedAt},
                completed_by_user_id = #{completedByUserId},
                source_type = #{sourceType},
                due_at = #{dueAt},
                completed_at = #{completedAt},
                updated_at = CURRENT_TIMESTAMP
            WHERE id = #{id}
            """)
    int update(SystemTask task);

    @Select("""
            <script>
            SELECT st.id, st.task_type, st.biz_type, st.biz_id, st.title, st.description,
                   st.priority, st.status, st.assignee_user_id,
                   COALESCE(NULLIF(su.real_name, ''), su.username) AS assignee_name,
                   st.claimed_at,
                   st.completed_by_user_id,
                   COALESCE(NULLIF(cu.real_name, ''), cu.username) AS completed_by_name,
                   st.source_type, st.due_at, st.completed_at, st.created_at, st.updated_at
            FROM system_task st
            LEFT JOIN sys_user su ON su.id = st.assignee_user_id
            LEFT JOIN sys_user cu ON cu.id = st.completed_by_user_id
            <where>
                <if test="companyId != null">
                    AND (
                        (st.biz_type = 'BATCH' AND EXISTS (
                            SELECT 1 FROM product_batch pb
                            WHERE pb.id = st.biz_id AND pb.company_id = #{companyId}
                        ))
                        OR
                        (st.biz_type = 'FEEDBACK' AND EXISTS (
                            SELECT 1 FROM user_feedback uf
                            WHERE uf.id = st.biz_id AND uf.company_id = #{companyId}
                        ))
                    )
                </if>
                <if test="status != null">
                    AND st.status = #{status}
                </if>
                <if test="assigneeUserId != null">
                    AND st.assignee_user_id = #{assigneeUserId}
                </if>
                <if test="keyword != null and keyword != ''">
                    AND (st.title LIKE CONCAT('%', #{keyword}, '%')
                    OR st.description LIKE CONCAT('%', #{keyword}, '%'))
                </if>
            </where>
            ORDER BY
              CASE st.priority
                WHEN 1 THEN 0
                WHEN 2 THEN 1
                ELSE 2
              END,
              st.id DESC
            LIMIT #{limit} OFFSET #{offset}
            </script>
            """)
    List<SystemTaskVO> selectPage(@Param("companyId") Long companyId,
                                  @Param("status") Integer status,
                                  @Param("assigneeUserId") Long assigneeUserId,
                                  @Param("keyword") String keyword,
                                  @Param("offset") long offset,
                                  @Param("limit") int limit);

    @Select("""
            <script>
            SELECT COUNT(*)
            FROM system_task st
            <where>
                <if test="companyId != null">
                    AND (
                        (st.biz_type = 'BATCH' AND EXISTS (
                            SELECT 1 FROM product_batch pb
                            WHERE pb.id = st.biz_id AND pb.company_id = #{companyId}
                        ))
                        OR
                        (st.biz_type = 'FEEDBACK' AND EXISTS (
                            SELECT 1 FROM user_feedback uf
                            WHERE uf.id = st.biz_id AND uf.company_id = #{companyId}
                        ))
                    )
                </if>
                <if test="status != null">
                    AND st.status = #{status}
                </if>
                <if test="assigneeUserId != null">
                    AND st.assignee_user_id = #{assigneeUserId}
                </if>
                <if test="keyword != null and keyword != ''">
                    AND (st.title LIKE CONCAT('%', #{keyword}, '%')
                    OR st.description LIKE CONCAT('%', #{keyword}, '%'))
                </if>
            </where>
            </script>
            """)
    long countPage(@Param("companyId") Long companyId,
                   @Param("status") Integer status,
                   @Param("assigneeUserId") Long assigneeUserId,
                   @Param("keyword") String keyword);

    @Select("""
            <script>
            SELECT COUNT(*)
            FROM system_task st
            WHERE st.status = 0
            <if test="companyId != null">
              AND (
                    (st.biz_type = 'BATCH' AND EXISTS (
                        SELECT 1 FROM product_batch pb
                        WHERE pb.id = st.biz_id AND pb.company_id = #{companyId}
                    ))
                    OR
                    (st.biz_type = 'FEEDBACK' AND EXISTS (
                        SELECT 1 FROM user_feedback uf
                        WHERE uf.id = st.biz_id AND uf.company_id = #{companyId}
                    ))
                )
            </if>
            </script>
            """)
    long countOpenTasks(@Param("companyId") Long companyId);

    @Select("""
            <script>
            SELECT COUNT(*)
            FROM system_task st
            WHERE st.status = 0 AND st.task_type = #{taskType}
            <if test="companyId != null">
              AND (
                    (st.biz_type = 'BATCH' AND EXISTS (
                        SELECT 1 FROM product_batch pb
                        WHERE pb.id = st.biz_id AND pb.company_id = #{companyId}
                    ))
                    OR
                    (st.biz_type = 'FEEDBACK' AND EXISTS (
                        SELECT 1 FROM user_feedback uf
                        WHERE uf.id = st.biz_id AND uf.company_id = #{companyId}
                    ))
                )
            </if>
            </script>
            """)
    long countOpenByTaskType(@Param("companyId") Long companyId, @Param("taskType") String taskType);
}

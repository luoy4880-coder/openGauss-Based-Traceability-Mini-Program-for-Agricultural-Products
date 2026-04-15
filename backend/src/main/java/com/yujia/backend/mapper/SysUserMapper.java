package com.yujia.backend.mapper;

import com.yujia.backend.entity.SysUser;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface SysUserMapper {

    @Select("""
            SELECT id, username, password, real_name, phone, status, created_at, updated_at
            FROM sys_user
            WHERE username = #{username}
            """)
    SysUser selectByUsername(String username);

    @Select("""
            SELECT id, username, password, real_name, phone, status, created_at, updated_at
            FROM sys_user
            WHERE id = #{id}
            """)
    SysUser selectById(Long id);

    @Select("""
            <script>
            SELECT id, username, password, real_name, phone, status, created_at, updated_at
            FROM sys_user
            <where>
                <if test="keyword != null and keyword != ''">
                    AND (username LIKE CONCAT('%', #{keyword}, '%')
                    OR real_name LIKE CONCAT('%', #{keyword}, '%'))
                </if>
                <if test="status != null">
                    AND status = #{status}
                </if>
            </where>
            ORDER BY id DESC
            LIMIT #{limit} OFFSET #{offset}
            </script>
            """)
    List<SysUser> selectPage(@Param("keyword") String keyword,
                             @Param("status") Integer status,
                             @Param("offset") long offset,
                             @Param("limit") int limit);

    @Select("""
            <script>
            SELECT COUNT(*)
            FROM sys_user
            <where>
                <if test="keyword != null and keyword != ''">
                    AND (username LIKE CONCAT('%', #{keyword}, '%')
                    OR real_name LIKE CONCAT('%', #{keyword}, '%'))
                </if>
                <if test="status != null">
                    AND status = #{status}
                </if>
            </where>
            </script>
            """)
    long countPage(@Param("keyword") String keyword, @Param("status") Integer status);

    @Select("SELECT COUNT(*) FROM sys_user")
    long countAll();

    @Insert("""
            INSERT INTO sys_user (username, password, real_name, phone, status, created_at, updated_at)
            VALUES (#{username}, #{password}, #{realName}, #{phone}, #{status}, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(SysUser sysUser);

    @Update("""
            UPDATE sys_user
            SET real_name = #{realName},
                phone = #{phone},
                status = #{status},
                updated_at = CURRENT_TIMESTAMP
            WHERE id = #{id}
            """)
    int updateById(SysUser sysUser);

    @Update("""
            UPDATE sys_user
            SET password = #{password},
                updated_at = CURRENT_TIMESTAMP
            WHERE id = #{id}
            """)
    int updatePassword(@Param("id") Long id, @Param("password") String password);

    @org.apache.ibatis.annotations.Delete("DELETE FROM sys_user WHERE id = #{id}")
    int deleteById(Long id);
}

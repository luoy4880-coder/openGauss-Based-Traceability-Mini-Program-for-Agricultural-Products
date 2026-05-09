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
            SELECT su.id, su.username, su.password, su.real_name, su.phone, su.openid, su.company_id,
                   c.company_name, su.status, su.created_at, su.updated_at
            FROM sys_user su
            LEFT JOIN company c ON c.id = su.company_id
            WHERE su.username = #{username}
            """)
    SysUser selectByUsername(String username);

    @Select("""
            SELECT su.id, su.username, su.password, su.real_name, su.phone, su.openid, su.company_id,
                   c.company_name, su.status, su.created_at, su.updated_at
            FROM sys_user su
            LEFT JOIN company c ON c.id = su.company_id
            WHERE su.id = #{id}
            """)
    SysUser selectById(Long id);

    @Select("""
            SELECT su.id, su.username, su.password, su.real_name, su.phone, su.openid, su.company_id,
                   c.company_name, su.status, su.created_at, su.updated_at
            FROM sys_user su
            LEFT JOIN company c ON c.id = su.company_id
            WHERE su.openid = #{openid}
            """)
    SysUser selectByOpenId(String openid);

    @Select("""
            SELECT DISTINCT su.id, su.username, su.password, su.real_name, su.phone, su.openid, su.company_id,
                   c.company_name, su.status, su.created_at, su.updated_at
            FROM sys_user su
            LEFT JOIN company c ON c.id = su.company_id
            INNER JOIN sys_user_role sur ON sur.user_id = su.id
            INNER JOIN sys_role sr ON sr.id = sur.role_id
            WHERE su.status = 1
              AND sr.role_code IN ('ADMIN', 'OPERATOR')
            ORDER BY su.id ASC
            """)
    List<SysUser> selectActiveStaff();

    @Select("""
            SELECT DISTINCT su.id, su.username, su.password, su.real_name, su.phone, su.openid, su.company_id,
                   c.company_name, su.status, su.created_at, su.updated_at
            FROM sys_user su
            LEFT JOIN company c ON c.id = su.company_id
            INNER JOIN sys_user_role sur ON sur.user_id = su.id
            INNER JOIN sys_role sr ON sr.id = sur.role_id
            WHERE su.status = 1
              AND su.company_id = #{companyId}
              AND sr.role_code IN ('ADMIN', 'OPERATOR')
            ORDER BY su.id ASC
            """)
    List<SysUser> selectActiveStaffByCompanyId(@Param("companyId") Long companyId);

    @Select("""
            SELECT COUNT(*)
            FROM sys_user su
            INNER JOIN sys_user_role sur ON sur.user_id = su.id
            INNER JOIN sys_role sr ON sr.id = sur.role_id
            WHERE su.id = #{userId}
              AND su.status = 1
              AND sr.role_code IN ('ADMIN', 'OPERATOR')
            """)
    long countActiveStaffById(@Param("userId") Long userId);

    @Select("""
            <script>
            SELECT su.id, su.username, su.password, su.real_name, su.phone, su.company_id, c.company_name,
                   su.status, su.created_at, su.updated_at
            FROM sys_user su
            LEFT JOIN company c ON c.id = su.company_id
            <where>
                <if test="companyId != null">
                    AND su.company_id = #{companyId}
                </if>
                <if test="keyword != null and keyword != ''">
                    AND (su.username LIKE CONCAT('%', #{keyword}, '%')
                    OR su.real_name LIKE CONCAT('%', #{keyword}, '%')
                    OR c.company_name LIKE CONCAT('%', #{keyword}, '%'))
                </if>
                <if test="status != null">
                    AND su.status = #{status}
                </if>
            </where>
            ORDER BY su.id DESC
            LIMIT #{limit} OFFSET #{offset}
            </script>
            """)
    List<SysUser> selectPage(@Param("companyId") Long companyId,
                             @Param("keyword") String keyword,
                             @Param("status") Integer status,
                             @Param("offset") long offset,
                             @Param("limit") int limit);

    @Select("""
            <script>
            SELECT COUNT(*)
            FROM sys_user su
            LEFT JOIN company c ON c.id = su.company_id
            <where>
                <if test="companyId != null">
                    AND su.company_id = #{companyId}
                </if>
                <if test="keyword != null and keyword != ''">
                    AND (su.username LIKE CONCAT('%', #{keyword}, '%')
                    OR su.real_name LIKE CONCAT('%', #{keyword}, '%')
                    OR c.company_name LIKE CONCAT('%', #{keyword}, '%'))
                </if>
                <if test="status != null">
                    AND su.status = #{status}
                </if>
            </where>
            </script>
            """)
    long countPage(@Param("companyId") Long companyId, @Param("keyword") String keyword, @Param("status") Integer status);

    @Select("SELECT COUNT(*) FROM sys_user")
    long countAll();

    @Insert("""
            INSERT INTO sys_user (username, password, real_name, phone, openid, company_id, status, created_at, updated_at)
            VALUES (#{username}, #{password}, #{realName}, #{phone}, #{openid}, #{companyId}, #{status}, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(SysUser sysUser);

    @Update("""
            UPDATE sys_user
            SET real_name = #{realName},
                phone = #{phone},
                company_id = #{companyId},
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

    @Update("""
            UPDATE sys_user
            SET username = #{username},
                password = #{password},
                real_name = #{realName},
                phone = #{phone},
                company_id = #{companyId},
                updated_at = CURRENT_TIMESTAMP
            WHERE id = #{id}
            """)
    int updateAuthById(SysUser sysUser);

    @Update("""
            UPDATE sys_user
            SET real_name = #{realName},
                phone = #{phone},
                updated_at = CURRENT_TIMESTAMP
            WHERE id = #{id}
            """)
    int updateProfileById(SysUser sysUser);

    @org.apache.ibatis.annotations.Delete("DELETE FROM sys_user WHERE id = #{id}")
    int deleteById(Long id);
}

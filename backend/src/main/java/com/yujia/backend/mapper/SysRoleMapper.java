package com.yujia.backend.mapper;

import com.yujia.backend.entity.SysRole;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SysRoleMapper {

    @Select("""
            SELECT id, role_code, role_name, remark, created_at
            FROM sys_role
            ORDER BY id ASC
            """)
    List<SysRole> selectAll();

    @Select("""
            <script>
            SELECT id, role_code, role_name, remark, created_at
            FROM sys_role
            WHERE id IN
            <foreach collection="roleIds" item="roleId" open="(" separator="," close=")">
                #{roleId}
            </foreach>
            ORDER BY id ASC
            </script>
            """)
    List<SysRole> selectByIds(@Param("roleIds") List<Long> roleIds);

    @Select("""
            SELECT r.id, r.role_code, r.role_name, r.remark, r.created_at
            FROM sys_role r
            INNER JOIN sys_user_role ur ON ur.role_id = r.id
            WHERE ur.user_id = #{userId}
            ORDER BY r.id ASC
            """)
    List<SysRole> selectByUserId(Long userId);

    @Select("""
            SELECT id, role_code, role_name, remark, created_at
            FROM sys_role
            WHERE role_code = #{roleCode}
            LIMIT 1
            """)
    SysRole selectByRoleCode(@Param("roleCode") String roleCode);
}

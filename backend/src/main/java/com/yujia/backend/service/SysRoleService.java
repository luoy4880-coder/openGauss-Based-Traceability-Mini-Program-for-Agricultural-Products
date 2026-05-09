package com.yujia.backend.service;

import com.yujia.backend.common.auth.AuthPermissionService;
import com.yujia.backend.common.exception.BusinessException;
import com.yujia.backend.entity.SysRole;
import com.yujia.backend.mapper.SysRoleMapper;
import com.yujia.backend.vo.RoleVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SysRoleService {

    private final SysRoleMapper sysRoleMapper;
    private final AuthPermissionService authPermissionService;

    public List<RoleVO> list() {
        return sysRoleMapper.selectAll().stream().map(this::toVO).toList();
    }

    public List<RoleVO> listAssignable() {
        if (authPermissionService.isAdmin()) {
            return list();
        }
        return sysRoleMapper.selectAll().stream()
                .filter(role -> !"ADMIN".equalsIgnoreCase(role.getRoleCode()))
                .map(this::toVO)
                .toList();
    }

    public List<RoleVO> listByUserId(Long userId) {
        return sysRoleMapper.selectByUserId(userId).stream().map(this::toVO).toList();
    }

    public void validateRoleIds(List<Long> roleIds) {
        if (roleIds == null || roleIds.isEmpty()) {
            throw new BusinessException("角色不能为空");
        }
        List<SysRole> roles = sysRoleMapper.selectByIds(roleIds);
        if (roles.size() != roleIds.size()) {
            throw new BusinessException("角色不存在或参数无效");
        }
        if (!authPermissionService.isAdmin()
                && roles.stream().anyMatch(role -> "ADMIN".equalsIgnoreCase(role.getRoleCode()))) {
            throw new BusinessException(403, "无权分配管理员角色");
        }
    }

    public Long findRoleIdByCode(String roleCode) {
        if (roleCode == null || roleCode.isBlank()) {
            return null;
        }
        SysRole role = sysRoleMapper.selectByRoleCode(roleCode);
        return role == null ? null : role.getId();
    }

    private RoleVO toVO(SysRole role) {
        RoleVO roleVO = new RoleVO();
        roleVO.setId(role.getId());
        roleVO.setRoleCode(role.getRoleCode());
        roleVO.setRoleName(role.getRoleName());
        roleVO.setRemark(role.getRemark());
        return roleVO;
    }
}

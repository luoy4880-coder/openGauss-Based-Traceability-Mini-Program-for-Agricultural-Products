package com.yujia.backend.service;

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

    public List<RoleVO> list() {
        return sysRoleMapper.selectAll().stream().map(this::toVO).toList();
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

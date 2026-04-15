package com.yujia.backend.service;

import com.yujia.backend.common.auth.AuthUser;
import com.yujia.backend.common.auth.PasswordUtil;
import com.yujia.backend.common.exception.BusinessException;
import com.yujia.backend.common.response.PageResponse;
import com.yujia.backend.dto.auth.BootstrapAdminRequest;
import com.yujia.backend.dto.user.UserCreateRequest;
import com.yujia.backend.dto.user.UserPasswordUpdateRequest;
import com.yujia.backend.dto.user.UserUpdateRequest;
import com.yujia.backend.entity.SysUser;
import com.yujia.backend.mapper.SysUserMapper;
import com.yujia.backend.mapper.SysUserRoleMapper;
import com.yujia.backend.vo.RoleVO;
import com.yujia.backend.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SysUserService {

    private final SysUserMapper sysUserMapper;
    private final SysUserRoleMapper sysUserRoleMapper;
    private final SysRoleService sysRoleService;

    public SysUser findByUsername(String username) {
        return sysUserMapper.selectByUsername(username);
    }

    public UserVO detail(Long id) {
        return toVO(detailEntity(id));
    }

    public PageResponse<UserVO> page(String keyword, Integer status, Integer pageNum, Integer pageSize) {
        int safePageNum = pageNum == null || pageNum < 1 ? 1 : pageNum;
        int safePageSize = pageSize == null || pageSize < 1 ? 10 : Math.min(pageSize, 100);
        long total = sysUserMapper.countPage(keyword, status);
        List<UserVO> records = sysUserMapper.selectPage(keyword, status,
                (long) (safePageNum - 1) * safePageSize, safePageSize).stream().map(this::toVO).toList();
        return PageResponse.<UserVO>builder()
                .records(records)
                .total(total)
                .pageNum(safePageNum)
                .pageSize(safePageSize)
                .build();
    }

    @Transactional
    public UserVO create(UserCreateRequest request) {
        if (sysUserMapper.selectByUsername(request.getUsername()) != null) {
            throw new BusinessException("用户名已存在");
        }
        sysRoleService.validateRoleIds(request.getRoleIds());

        SysUser sysUser = new SysUser();
        sysUser.setUsername(request.getUsername());
        sysUser.setPassword(PasswordUtil.encode(request.getPassword()));
        sysUser.setRealName(request.getRealName());
        sysUser.setPhone(request.getPhone());
        sysUser.setStatus(request.getStatus());
        sysUserMapper.insert(sysUser);
        sysUserRoleMapper.insertBatch(sysUser.getId(), request.getRoleIds());
        return detail(sysUser.getId());
    }

    @Transactional
    public UserVO update(Long id, UserUpdateRequest request) {
        SysUser sysUser = detailEntity(id);
        sysRoleService.validateRoleIds(request.getRoleIds());

        sysUser.setRealName(request.getRealName());
        sysUser.setPhone(request.getPhone());
        sysUser.setStatus(request.getStatus());
        sysUserMapper.updateById(sysUser);
        sysUserRoleMapper.deleteByUserId(id);
        sysUserRoleMapper.insertBatch(id, request.getRoleIds());
        return detail(id);
    }

    public void updatePassword(Long id, UserPasswordUpdateRequest request) {
        detailEntity(id);
        sysUserMapper.updatePassword(id, PasswordUtil.encode(request.getNewPassword()));
    }

    @Transactional
    public void delete(Long id) {
        detailEntity(id);
        sysUserRoleMapper.deleteByUserId(id);
        sysUserMapper.deleteById(id);
    }

    @Transactional
    public UserVO bootstrapAdmin(BootstrapAdminRequest request) {
        if (sysUserMapper.countAll() > 0) {
            throw new BusinessException("系统已初始化管理员，不能重复初始化");
        }

        List<RoleVO> roles = sysRoleService.list();
        Long adminRoleId = roles.stream()
                .filter(role -> "ADMIN".equalsIgnoreCase(role.getRoleCode()))
                .map(RoleVO::getId)
                .findFirst()
                .orElseThrow(() -> new BusinessException("缺少ADMIN角色，请先在sys_role表插入管理员角色"));

        UserCreateRequest createRequest = new UserCreateRequest();
        createRequest.setUsername(request.getUsername());
        createRequest.setPassword(request.getPassword());
        createRequest.setRealName(request.getRealName());
        createRequest.setPhone(request.getPhone());
        createRequest.setStatus(1);
        createRequest.setRoleIds(List.of(adminRoleId));
        return create(createRequest);
    }

    public AuthUser loadAuthUser(Long userId) {
        SysUser sysUser = sysUserMapper.selectById(userId);
        if (sysUser == null || sysUser.getStatus() == null || sysUser.getStatus() != 1) {
            return null;
        }
        List<String> roleCodes = sysRoleService.listByUserId(userId).stream().map(RoleVO::getRoleCode).toList();
        return AuthUser.builder()
                .userId(sysUser.getId())
                .username(sysUser.getUsername())
                .roleCodes(roleCodes)
                .build();
    }

    private SysUser detailEntity(Long id) {
        SysUser sysUser = sysUserMapper.selectById(id);
        if (sysUser == null) {
            throw new BusinessException(404, "用户不存在");
        }
        return sysUser;
    }

    private UserVO toVO(SysUser sysUser) {
        UserVO userVO = new UserVO();
        userVO.setId(sysUser.getId());
        userVO.setUsername(sysUser.getUsername());
        userVO.setRealName(sysUser.getRealName());
        userVO.setPhone(sysUser.getPhone());
        userVO.setStatus(sysUser.getStatus());
        userVO.setCreatedAt(sysUser.getCreatedAt());
        userVO.setUpdatedAt(sysUser.getUpdatedAt());
        userVO.setRoles(sysRoleService.listByUserId(sysUser.getId()));
        return userVO;
    }
}

package com.yujia.backend.service;

import com.yujia.backend.common.auth.AuthContext;
import com.yujia.backend.common.auth.PasswordUtil;
import com.yujia.backend.common.auth.TokenUtil;
import com.yujia.backend.common.exception.BusinessException;
import com.yujia.backend.dto.auth.BootstrapAdminRequest;
import com.yujia.backend.dto.auth.LoginRequest;
import com.yujia.backend.entity.SysUser;
import com.yujia.backend.vo.LoginVO;
import com.yujia.backend.vo.RoleVO;
import com.yujia.backend.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final SysUserService sysUserService;
    private final SysRoleService sysRoleService;
    private final TokenUtil tokenUtil = new TokenUtil();

    public LoginVO login(LoginRequest request) {
        SysUser sysUser = sysUserService.findByUsername(request.getUsername());
        if (sysUser == null || !PasswordUtil.matches(request.getPassword(), sysUser.getPassword())) {
            throw new BusinessException(401, "用户名或密码错误");
        }
        if (sysUser.getStatus() == null || sysUser.getStatus() != 1) {
            throw new BusinessException(403, "用户已被禁用");
        }

        List<String> roleCodes = sysRoleService.listByUserId(sysUser.getId()).stream()
                .map(RoleVO::getRoleCode)
                .toList();
        return LoginVO.builder()
                .token(tokenUtil.generateToken(sysUser.getId(), sysUser.getUsername()))
                .userId(sysUser.getId())
                .username(sysUser.getUsername())
                .realName(sysUser.getRealName())
                .roleCodes(roleCodes)
                .build();
    }

    public UserVO currentUser() {
        var authUser = AuthContext.get();
        if (authUser == null) {
            throw new BusinessException(401, "未登录");
        }
        return sysUserService.detail(authUser.getUserId());
    }

    public UserVO bootstrapAdmin(BootstrapAdminRequest request) {
        return sysUserService.bootstrapAdmin(request);
    }
}

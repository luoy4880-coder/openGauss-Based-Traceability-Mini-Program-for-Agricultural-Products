package com.yujia.backend.service;

import com.yujia.backend.common.auth.AuthContext;
import com.yujia.backend.common.auth.PasswordUtil;
import com.yujia.backend.common.auth.TokenUtil;
import com.yujia.backend.common.exception.BusinessException;
import com.yujia.backend.dto.auth.BindAccountRequest;
import com.yujia.backend.dto.auth.LoginRequest;
import com.yujia.backend.dto.auth.ProfileUpdateRequest;
import com.yujia.backend.dto.auth.RegisterRequest;
import com.yujia.backend.dto.auth.WeChatLoginRequest;
import com.yujia.backend.entity.SysUser;
import com.yujia.backend.vo.LoginVO;
import com.yujia.backend.vo.RoleVO;
import com.yujia.backend.vo.UserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

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
            throw new BusinessException(403, "账号已被禁用");
        }

        List<String> roleCodes = sysRoleService.listByUserId(sysUser.getId()).stream()
                .map(RoleVO::getRoleCode)
                .toList();
        boolean hasStaffRole = roleCodes.stream().anyMatch(roleCode ->
                "ADMIN".equalsIgnoreCase(roleCode) || "OPERATOR".equalsIgnoreCase(roleCode));
        if (!hasStaffRole) {
            throw new BusinessException(403, "该账号没有管理后台权限");
        }
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

    public LoginVO wechatLogin(WeChatLoginRequest request) {
        String openid = buildWechatOpenId(request.getCode());
        SysUser user = sysUserService.findByOpenId(openid);
        if (user == null) {
            user = sysUserService.createByOpenId(openid);
        }

        List<String> roleCodes = sysRoleService.listByUserId(user.getId()).stream()
                .map(RoleVO::getRoleCode)
                .toList();
        return LoginVO.builder()
                .token(tokenUtil.generateToken(user.getId(), user.getUsername()))
                .userId(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .roleCodes(roleCodes)
                .build();
    }

    public LoginVO register(RegisterRequest request) {
        SysUser user = sysUserService.register(
                request.getUsername(),
                request.getPassword(),
                request.getRealName(),
                request.getPhone(),
                request.getCompanyName()
        );
        return LoginVO.builder()
                .token(tokenUtil.generateToken(user.getId(), user.getUsername()))
                .userId(user.getId())
                .username(user.getUsername())
                .realName(user.getRealName())
                .roleCodes(sysRoleService.listByUserId(user.getId()).stream().map(RoleVO::getRoleCode).toList())
                .build();
    }

    public UserVO bindAccount(BindAccountRequest request) {
        var authUser = AuthContext.get();
        if (authUser == null) {
            throw new BusinessException(401, "未登录");
        }
        sysUserService.bindAccount(
                authUser.getUserId(),
                request.getUsername(),
                request.getPassword(),
                request.getRealName(),
                request.getPhone()
        );
        return sysUserService.detail(authUser.getUserId());
    }

    public UserVO updateProfile(ProfileUpdateRequest request) {
        var authUser = AuthContext.get();
        if (authUser == null) {
            throw new BusinessException(401, "未登录");
        }
        return sysUserService.updateProfile(authUser.getUserId(), request.getRealName(), request.getPhone());
    }

    private String buildWechatOpenId(String code) {
        String normalizedCode = code == null ? "" : code.trim();
        if (normalizedCode.isEmpty()) {
            throw new BusinessException(400, "微信登录凭证不能为空");
        }
        String stableId = UUID.nameUUIDFromBytes(normalizedCode.getBytes(StandardCharsets.UTF_8))
                .toString()
                .replace("-", "");
        return "WX_OPENID_" + stableId;
    }
}

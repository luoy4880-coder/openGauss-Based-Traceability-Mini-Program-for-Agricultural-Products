package com.yujia.backend.common.auth;

import com.yujia.backend.common.exception.BusinessException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthPermissionService {

    public AuthUser requireLogin() {
        AuthUser currentUser = AuthContext.get();
        if (currentUser == null) {
            throw new BusinessException(401, "未登录");
        }
        return currentUser;
    }

    public boolean isAdmin() {
        AuthUser currentUser = AuthContext.get();
        return currentUser != null
                && currentUser.getRoleCodes() != null
                && currentUser.getRoleCodes().stream().anyMatch(role -> "ADMIN".equalsIgnoreCase(role));
    }

    public void requireAdmin() {
        requireAnyRole("ADMIN");
    }

    public void requireStaff() {
        requireAnyRole("ADMIN", "OPERATOR");
    }

    public void requireAnyRole(String... roleCodes) {
        AuthUser currentUser = requireLogin();
        if (currentUser.getRoleCodes() == null || currentUser.getRoleCodes().isEmpty()) {
            throw new BusinessException(403, "无权执行该操作");
        }

        Set<String> expectedRoles = Arrays.stream(roleCodes)
                .map(role -> role == null ? "" : role.trim().toUpperCase())
                .filter(role -> !role.isEmpty())
                .collect(Collectors.toSet());

        boolean matched = currentUser.getRoleCodes().stream()
                .map(role -> role == null ? "" : role.trim().toUpperCase())
                .anyMatch(expectedRoles::contains);

        if (!matched) {
            throw new BusinessException(403, "无权执行该操作");
        }
    }
}

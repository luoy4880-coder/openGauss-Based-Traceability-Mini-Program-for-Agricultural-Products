package com.yujia.backend.service;

import com.yujia.backend.common.auth.AuthContext;
import com.yujia.backend.common.auth.AuthUser;
import com.yujia.backend.common.exception.BusinessException;
import org.springframework.stereotype.Service;

@Service
public class CompanyScopeService {

    public boolean isAdmin() {
        var authUser = AuthContext.get();
        return authUser != null
                && authUser.getRoleCodes() != null
                && authUser.getRoleCodes().stream().anyMatch("ADMIN"::equalsIgnoreCase);
    }

    public Long currentCompanyIdOrNull() {
        var authUser = AuthContext.get();
        return authUser == null ? null : authUser.getCompanyId();
    }

    public Long currentCompanyScopeOrNull() {
        var authUser = AuthContext.get();
        if (authUser == null) {
            return null;
        }
        if (isAdmin()) {
            return null;
        }
        if (!isStaff(authUser)) {
            return authUser.getCompanyId();
        }
        return requireCurrentCompanyId();
    }

    public Long requireCurrentCompanyId() {
        Long companyId = currentCompanyIdOrNull();
        if (companyId == null) {
            throw new BusinessException(403, "当前账号未绑定公司，无法访问公司业务数据");
        }
        return companyId;
    }

    public void assertAccessibleCompany(Long targetCompanyId) {
        var authUser = AuthContext.get();
        if (authUser == null || isAdmin()) {
            return;
        }
        if (!isStaff(authUser)) {
            return;
        }
        Long companyId = requireCurrentCompanyId();
        if (targetCompanyId == null || !companyId.equals(targetCompanyId)) {
            throw new BusinessException(403, "无权访问其他公司的数据");
        }
    }

    private boolean isStaff(AuthUser authUser) {
        return authUser != null
                && authUser.getRoleCodes() != null
                && authUser.getRoleCodes().stream().anyMatch(role ->
                "ADMIN".equalsIgnoreCase(role) || "OPERATOR".equalsIgnoreCase(role));
    }
}

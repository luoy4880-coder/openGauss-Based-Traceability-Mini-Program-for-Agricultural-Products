package com.yujia.backend.service;

import com.yujia.backend.common.auth.AuthContext;
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
        if (isAdmin()) {
            return null;
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
        if (isAdmin()) {
            return;
        }
        Long companyId = requireCurrentCompanyId();
        if (targetCompanyId == null || !companyId.equals(targetCompanyId)) {
            throw new BusinessException(403, "无权访问其他公司的数据");
        }
    }
}

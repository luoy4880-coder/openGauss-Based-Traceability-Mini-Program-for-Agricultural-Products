package com.yujia.backend.service;

import com.yujia.backend.common.exception.BusinessException;
import com.yujia.backend.entity.Company;
import com.yujia.backend.mapper.CompanyMapper;
import com.yujia.backend.vo.CompanyVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CompanyService {

    private final CompanyMapper companyMapper;
    private final NumberGeneratorService numberGeneratorService;
    private final CompanyScopeService companyScopeService;

    public List<CompanyVO> list() {
        Long companyId = companyScopeService.currentCompanyScopeOrNull();
        if (companyId != null) {
            return List.of(toVO(detailEntity(companyId)));
        }
        return companyMapper.selectAll().stream().map(this::toVO).toList();
    }

    public Company detailEntity(Long id) {
        Company company = companyMapper.selectById(id);
        if (company == null) {
            throw new BusinessException(404, "公司不存在");
        }
        return company;
    }

    public void assertManageableCompany(Long companyId) {
        if (companyId == null) {
            throw new BusinessException(400, "公司不能为空");
        }
        companyScopeService.assertAccessibleCompany(companyId);
    }

    public Long resolveOrCreateCompanyId(String companyName) {
        if (!StringUtils.hasText(companyName)) {
            throw new BusinessException(400, "公司名称不能为空");
        }
        String normalizedName = companyName.trim();
        Company existing = companyMapper.selectByName(normalizedName);
        if (existing != null) {
            return existing.getId();
        }
        Company company = new Company();
        company.setCompanyCode(numberGeneratorService.companyCode());
        company.setCompanyName(normalizedName);
        company.setStatus(1);
        companyMapper.insert(company);
        return company.getId();
    }

    private CompanyVO toVO(Company company) {
        CompanyVO vo = new CompanyVO();
        vo.setId(company.getId());
        vo.setCompanyCode(company.getCompanyCode());
        vo.setCompanyName(company.getCompanyName());
        vo.setStatus(company.getStatus());
        return vo;
    }
}

package com.yujia.backend.mapper;

import com.yujia.backend.entity.Company;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CompanyMapper {

    @Select("""
            SELECT id, company_code, company_name, status, created_at, updated_at
            FROM company
            ORDER BY id DESC
            """)
    List<Company> selectAll();

    @Select("""
            SELECT id, company_code, company_name, status, created_at, updated_at
            FROM company
            WHERE id = #{id}
            """)
    Company selectById(Long id);

    @Select("""
            SELECT id, company_code, company_name, status, created_at, updated_at
            FROM company
            WHERE company_name = #{companyName}
            """)
    Company selectByName(String companyName);

    @Insert("""
            INSERT INTO company (company_code, company_name, status, created_at, updated_at)
            VALUES (#{companyCode}, #{companyName}, #{status}, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Company company);
}

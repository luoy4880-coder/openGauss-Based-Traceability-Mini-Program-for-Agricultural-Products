package com.yujia.backend.mapper;

import com.yujia.backend.entity.BaseInfo;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Options;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

@Mapper
public interface BaseInfoMapper {

    @Select("""
            <script>
            SELECT id, base_code, company_id, base_name, manager_name, contact_phone, province, city, district,
                   address, acreage, status, created_at, updated_at
            FROM base_info
            <where>
                <if test="companyId != null">
                    AND company_id = #{companyId}
                </if>
                <if test="keyword != null and keyword != ''">
                    AND (base_code LIKE CONCAT('%', #{keyword}, '%')
                    OR base_name LIKE CONCAT('%', #{keyword}, '%'))
                </if>
                <if test="status != null">
                    AND status = #{status}
                </if>
            </where>
            ORDER BY id DESC
            </script>
            """)
    List<BaseInfo> selectList(@Param("companyId") Long companyId, @Param("keyword") String keyword, @Param("status") Integer status);

    @Select("""
            <script>
            SELECT COUNT(*)
            FROM base_info
            <where>
                <if test="companyId != null">
                    AND company_id = #{companyId}
                </if>
                <if test="keyword != null and keyword != ''">
                    AND (base_code LIKE CONCAT('%', #{keyword}, '%')
                    OR base_name LIKE CONCAT('%', #{keyword}, '%'))
                </if>
                <if test="status != null">
                    AND status = #{status}
                </if>
            </where>
            </script>
            """)
    long countList(@Param("companyId") Long companyId, @Param("keyword") String keyword, @Param("status") Integer status);

    @Select("""
            <script>
            SELECT id, base_code, company_id, base_name, manager_name, contact_phone, province, city, district,
                   address, acreage, status, created_at, updated_at
            FROM base_info
            <where>
                <if test="companyId != null">
                    AND company_id = #{companyId}
                </if>
                <if test="keyword != null and keyword != ''">
                    AND (base_code LIKE CONCAT('%', #{keyword}, '%')
                    OR base_name LIKE CONCAT('%', #{keyword}, '%'))
                </if>
                <if test="status != null">
                    AND status = #{status}
                </if>
            </where>
            ORDER BY id DESC
            LIMIT #{limit} OFFSET #{offset}
            </script>
            """)
    List<BaseInfo> selectPage(@Param("companyId") Long companyId,
                              @Param("keyword") String keyword,
                              @Param("status") Integer status,
                              @Param("offset") long offset,
                              @Param("limit") int limit);

    @Select("""
            SELECT id, base_code, company_id, base_name, manager_name, contact_phone, province, city, district,
                   address, acreage, status, created_at, updated_at
            FROM base_info
            WHERE id = #{id}
            """)
    BaseInfo selectById(Long id);

    @Select("""
            SELECT id, base_code, company_id, base_name, manager_name, contact_phone, province, city, district,
                   address, acreage, status, created_at, updated_at
            FROM base_info
            WHERE base_code = #{baseCode}
            """)
    BaseInfo selectByBaseCode(String baseCode);

    @Insert("""
            INSERT INTO base_info (
                base_code, company_id, base_name, manager_name, contact_phone, province, city, district,
                address, acreage, status, created_at, updated_at
            ) VALUES (
                #{baseCode}, #{companyId}, #{baseName}, #{managerName}, #{contactPhone}, #{province}, #{city}, #{district},
                #{address}, #{acreage}, #{status}, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP
            )
            """)
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(BaseInfo baseInfo);

    @Update("""
            UPDATE base_info
            SET base_name = #{baseName},
                manager_name = #{managerName},
                contact_phone = #{contactPhone},
                province = #{province},
                city = #{city},
                district = #{district},
                address = #{address},
                acreage = #{acreage},
                status = #{status},
                updated_at = CURRENT_TIMESTAMP
            WHERE id = #{id}
            """)
    int updateById(BaseInfo baseInfo);

    @Select("""
            SELECT COUNT(*)
            FROM product_batch
            WHERE base_id = #{baseId}
            """)
    long countBatchesByBaseId(Long baseId);

    @org.apache.ibatis.annotations.Delete("""
            DELETE FROM base_info
            WHERE id = #{id}
            """)
    int deleteById(Long id);
}

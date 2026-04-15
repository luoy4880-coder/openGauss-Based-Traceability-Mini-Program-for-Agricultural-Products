package com.yujia.backend.service;

import com.yujia.backend.common.exception.BusinessException;
import com.yujia.backend.common.response.PageResponse;
import com.yujia.backend.dto.base.BaseCreateRequest;
import com.yujia.backend.dto.base.BaseUpdateRequest;
import com.yujia.backend.entity.BaseInfo;
import com.yujia.backend.mapper.BaseInfoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
@RequiredArgsConstructor
public class BaseInfoService {

    private final BaseInfoMapper baseInfoMapper;

    public List<BaseInfo> list(String keyword, Integer status) {
        return baseInfoMapper.selectList(keyword, status);
    }

    public PageResponse<BaseInfo> page(String keyword, Integer status, Integer pageNum, Integer pageSize) {
        int safePageNum = normalizePageNum(pageNum);
        int safePageSize = normalizePageSize(pageSize);
        long total = baseInfoMapper.countList(keyword, status);
        List<BaseInfo> records = baseInfoMapper.selectPage(
                keyword, status, (long) (safePageNum - 1) * safePageSize, safePageSize);
        return PageResponse.<BaseInfo>builder()
                .records(records)
                .total(total)
                .pageNum(safePageNum)
                .pageSize(safePageSize)
                .build();
    }

    public BaseInfo detail(Long id) {
        BaseInfo baseInfo = baseInfoMapper.selectById(id);
        if (baseInfo == null) {
            throw new BusinessException(404, "基地不存在");
        }
        return baseInfo;
    }

    public BaseInfo create(BaseCreateRequest request) {
        if (StringUtils.hasText(request.getBaseCode())
                && baseInfoMapper.selectByBaseCode(request.getBaseCode()) != null) {
            throw new BusinessException("基地编码已存在");
        }

        BaseInfo baseInfo = new BaseInfo();
        baseInfo.setBaseCode(request.getBaseCode());
        baseInfo.setBaseName(request.getBaseName());
        baseInfo.setManagerName(request.getManagerName());
        baseInfo.setContactPhone(request.getContactPhone());
        baseInfo.setProvince(request.getProvince());
        baseInfo.setCity(request.getCity());
        baseInfo.setDistrict(request.getDistrict());
        baseInfo.setAddress(request.getAddress());
        baseInfo.setAcreage(request.getAcreage());
        baseInfo.setStatus(request.getStatus());
        baseInfoMapper.insert(baseInfo);
        return detail(baseInfo.getId());
    }

    public BaseInfo update(Long id, BaseUpdateRequest request) {
        BaseInfo baseInfo = detail(id);
        baseInfo.setBaseName(request.getBaseName());
        baseInfo.setManagerName(request.getManagerName());
        baseInfo.setContactPhone(request.getContactPhone());
        baseInfo.setProvince(request.getProvince());
        baseInfo.setCity(request.getCity());
        baseInfo.setDistrict(request.getDistrict());
        baseInfo.setAddress(request.getAddress());
        baseInfo.setAcreage(request.getAcreage());
        baseInfo.setStatus(request.getStatus());
        baseInfoMapper.updateById(baseInfo);
        return detail(id);
    }

    public void validateExists(Long id) {
        if (baseInfoMapper.selectById(id) == null) {
            throw new BusinessException(404, "基地不存在");
        }
    }

    public void delete(Long id) {
        detail(id);
        if (baseInfoMapper.countBatchesByBaseId(id) > 0) {
            throw new BusinessException("该基地下仍有关联批次，不能删除");
        }
        baseInfoMapper.deleteById(id);
    }

    private int normalizePageNum(Integer pageNum) {
        return pageNum == null || pageNum < 1 ? 1 : pageNum;
    }

    private int normalizePageSize(Integer pageSize) {
        if (pageSize == null || pageSize < 1) {
            return 10;
        }
        return Math.min(pageSize, 100);
    }
}

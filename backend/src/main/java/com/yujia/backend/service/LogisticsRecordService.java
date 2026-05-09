package com.yujia.backend.service;

import com.yujia.backend.common.auth.AuthContext;
import com.yujia.backend.common.exception.BusinessException;
import com.yujia.backend.dto.logistics.LogisticsRecordCreateRequest;
import com.yujia.backend.dto.logistics.LogisticsRecordUpdateRequest;
import com.yujia.backend.entity.LogisticsRecord;
import com.yujia.backend.mapper.LogisticsRecordMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LogisticsRecordService {

    private final LogisticsRecordMapper logisticsRecordMapper;
    private final ProductBatchService productBatchService;
    private final NumberGeneratorService numberGeneratorService;
    private final CompanyScopeService companyScopeService;

    public List<LogisticsRecord> list(Long batchId, Long itemId) {
        if (batchId != null) {
            productBatchService.ensureBatchExists(batchId);
        }
        return logisticsRecordMapper.selectList(companyScopeService.currentCompanyScopeOrNull(), batchId, itemId);
    }

    public LogisticsRecord create(LogisticsRecordCreateRequest request) {
        productBatchService.ensureBatchExists(request.getBatchId());
        LogisticsRecord record = new LogisticsRecord();
        record.setBatchId(request.getBatchId());
        record.setItemId(request.getItemId());
        record.setLogisticsCode(numberGeneratorService.logisticsCode());
        record.setNodeType(request.getNodeType());
        record.setNodeName(request.getNodeName());
        record.setOperationTime(request.getOperationTime() == null ? LocalDateTime.now() : request.getOperationTime());
        record.setOperatorName(resolveOperatorName(request.getOperatorName()));
        record.setContactPhone(request.getContactPhone());
        record.setLocation(request.getLocation());
        record.setTemperature(request.getTemperature());
        record.setHumidity(request.getHumidity());
        record.setAttachmentUrl(request.getAttachmentUrl());
        record.setRemark(request.getRemark());
        logisticsRecordMapper.insert(record);
        return record;
    }

    public LogisticsRecord detail(Long id) {
        LogisticsRecord record = logisticsRecordMapper.selectById(id);
        if (record == null) {
            throw new BusinessException(404, "流通链路记录不存在");
        }
        productBatchService.ensureBatchExists(record.getBatchId());
        return record;
    }

    public LogisticsRecord update(Long id, LogisticsRecordUpdateRequest request) {
        LogisticsRecord record = detail(id);
        productBatchService.ensureBatchExists(request.getBatchId());
        record.setBatchId(request.getBatchId());
        record.setItemId(request.getItemId());
        record.setNodeType(request.getNodeType());
        record.setNodeName(request.getNodeName());
        record.setOperationTime(request.getOperationTime() == null ? LocalDateTime.now() : request.getOperationTime());
        record.setOperatorName(resolveOperatorName(request.getOperatorName()));
        record.setContactPhone(request.getContactPhone());
        record.setLocation(request.getLocation());
        record.setTemperature(request.getTemperature());
        record.setHumidity(request.getHumidity());
        record.setAttachmentUrl(request.getAttachmentUrl());
        record.setRemark(request.getRemark());
        logisticsRecordMapper.updateById(record);
        return detail(id);
    }

    public void delete(Long id) {
        detail(id);
        logisticsRecordMapper.deleteById(id);
    }

    private String resolveOperatorName(String operatorName) {
        if (operatorName != null && !operatorName.isBlank()) {
            return operatorName.trim();
        }
        var authUser = AuthContext.get();
        if (authUser == null) {
            return null;
        }
        if (authUser.getRealName() != null && !authUser.getRealName().isBlank()) {
            return authUser.getRealName().trim();
        }
        if (authUser.getUsername() != null && !authUser.getUsername().isBlank()) {
            return authUser.getUsername().trim();
        }
        return null;
    }
}

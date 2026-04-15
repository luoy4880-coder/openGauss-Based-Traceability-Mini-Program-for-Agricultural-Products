package com.yujia.backend.service;

import com.yujia.backend.common.exception.BusinessException;
import com.yujia.backend.common.response.PageResponse;
import com.yujia.backend.dto.recall.RecallRecordCreateRequest;
import com.yujia.backend.entity.RecallRecord;
import com.yujia.backend.mapper.ProductBatchMapper;
import com.yujia.backend.mapper.RecallRecordMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecallRecordService {

    private final RecallRecordMapper recallRecordMapper;
    private final ProductBatchService productBatchService;
    private final ProductBatchMapper productBatchMapper;

    public List<RecallRecord> list(Long batchId, Integer recallStatus) {
        return recallRecordMapper.selectList(batchId, recallStatus);
    }

    public PageResponse<RecallRecord> page(Long batchId, Integer recallStatus,
                                           Integer pageNum, Integer pageSize) {
        int safePageNum = normalizePageNum(pageNum);
        int safePageSize = normalizePageSize(pageSize);
        long total = recallRecordMapper.countList(batchId, recallStatus);
        List<RecallRecord> records = recallRecordMapper.selectPage(
                batchId, recallStatus, (long) (safePageNum - 1) * safePageSize, safePageSize);
        return PageResponse.<RecallRecord>builder()
                .records(records)
                .total(total)
                .pageNum(safePageNum)
                .pageSize(safePageSize)
                .build();
    }

    public RecallRecord detail(Long id) {
        RecallRecord recallRecord = recallRecordMapper.selectById(id);
        if (recallRecord == null) {
            throw new BusinessException(404, "召回记录不存在");
        }
        return recallRecord;
    }

    public RecallRecord latestByBatchId(Long batchId) {
        return recallRecordMapper.selectLatestByBatchId(batchId);
    }

    @Transactional
    public RecallRecord create(RecallRecordCreateRequest request) {
        productBatchService.ensureBatchExists(request.getBatchId());

        RecallRecord recallRecord = new RecallRecord();
        recallRecord.setBatchId(request.getBatchId());
        recallRecord.setRecallLevel(request.getRecallLevel());
        recallRecord.setReason(request.getReason());
        recallRecord.setRecallStatus(1);
        recallRecord.setNoticeTime(LocalDateTime.now());
        recallRecordMapper.insert(recallRecord);
        productBatchMapper.updateRecallStatus(request.getBatchId(), 1);
        return detail(recallRecord.getId());
    }

    @Transactional
    public RecallRecord close(Long id) {
        RecallRecord recallRecord = detail(id);
        if (recallRecord.getRecallStatus() != null && recallRecord.getRecallStatus() == 0) {
            return recallRecord;
        }

        recallRecord.setRecallStatus(0);
        recallRecord.setClosedAt(LocalDateTime.now());
        recallRecordMapper.updateStatus(recallRecord);
        productBatchMapper.updateRecallStatus(recallRecord.getBatchId(), 0);
        return detail(id);
    }

    @Transactional
    public void delete(Long id) {
        RecallRecord recallRecord = detail(id);
        recallRecordMapper.deleteById(id);
        RecallRecord latest = recallRecordMapper.selectLatestByBatchId(recallRecord.getBatchId());
        int recallStatus = latest != null && latest.getRecallStatus() != null ? latest.getRecallStatus() : 0;
        productBatchMapper.updateRecallStatus(recallRecord.getBatchId(), recallStatus);
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

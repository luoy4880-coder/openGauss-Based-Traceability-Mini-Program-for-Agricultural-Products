package com.yujia.backend.service;

import com.yujia.backend.mapper.ScanLogMapper;
import com.yujia.backend.mapper.UserFeedbackMapper;
import com.yujia.backend.vo.BatchArchiveVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BatchArchiveService {

    private final ProductBatchService productBatchService;
    private final BaseInfoService baseInfoService;
    private final ProductionRecordService productionRecordService;
    private final InspectionReportService inspectionReportService;
    private final ProductItemService productItemService;
    private final LogisticsRecordService logisticsRecordService;
    private final RecallRecordService recallRecordService;
    private final UserFeedbackMapper userFeedbackMapper;
    private final ScanLogMapper scanLogMapper;

    public BatchArchiveVO archive(Long batchId) {
        var batchInfo = productBatchService.detail(batchId);
        BatchArchiveVO archive = new BatchArchiveVO();
        archive.setBatchInfo(batchInfo);
        archive.setBaseInfo(baseInfoService.detail(batchInfo.getBaseId()));
        archive.setProductionRecords(productionRecordService.list(batchId, null));
        archive.setInspectionReports(inspectionReportService.list(batchId, null));
        archive.setProductItems(productItemService.listByBatchId(batchId));
        archive.setLogisticsRecords(logisticsRecordService.list(batchId, null));
        archive.setRecallRecord(recallRecordService.latestByBatchId(batchId));
        archive.setFeedbackCount((int) userFeedbackMapper.countByBatchId(batchId));
        archive.setHighPriorityFeedbackCount((int) userFeedbackMapper.countHighPriorityByBatchId(batchId));
        archive.setScanCount((int) scanLogMapper.countByBatchId(batchId));
        archive.setAbnormalScanCount((int) scanLogMapper.countAbnormalByBatchId(batchId));
        return archive;
    }
}

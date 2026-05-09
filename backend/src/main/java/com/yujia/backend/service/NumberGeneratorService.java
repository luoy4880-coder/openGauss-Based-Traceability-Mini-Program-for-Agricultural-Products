package com.yujia.backend.service;

import com.yujia.backend.mapper.NumberSequenceMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class NumberGeneratorService {

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final NumberSequenceMapper numberSequenceMapper;

    public String baseCode() {
        return next("BASE", "seq_base_code", 3);
    }

    public String batchCode() {
        return next("BATCH", "seq_batch_code", 4);
    }

    public String reportNo() {
        return next("IR", "seq_report_no", 4);
    }

    public String recallCode() {
        return next("RC", "seq_recall_code", 4);
    }

    public String logisticsCode() {
        return next("LG", "seq_logistics_code", 4);
    }

    public String companyCode() {
        return next("CP", "seq_company_code", 4);
    }

    public String itemCode(String batchCode, long serialNo) {
        String safeBatchCode = batchCode == null ? "BATCH" : batchCode.replaceAll("[^A-Za-z0-9]", "");
        return "ITEM-" + safeBatchCode + "-" + String.format("%06d", serialNo);
    }

    private String next(String prefix, String sequenceName, int width) {
        ensureSequenceExists(sequenceName);
        long value = numberSequenceMapper.nextValue(sequenceName);
        return prefix + "-" + LocalDate.now().format(DATE_FORMATTER) + "-" + String.format("%0" + width + "d", value);
    }

    private void ensureSequenceExists(String sequenceName) {
        if (numberSequenceMapper.countSequence(sequenceName) > 0) {
            return;
        }
        try {
            numberSequenceMapper.createSequence(sequenceName);
        } catch (DataAccessException ex) {
            if (numberSequenceMapper.countSequence(sequenceName) > 0) {
                return;
            }
            throw ex;
        }
    }
}

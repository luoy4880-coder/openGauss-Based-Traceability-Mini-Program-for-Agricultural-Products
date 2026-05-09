package com.yujia.backend.controller;

import com.yujia.backend.common.auth.AuthPermissionService;
import com.yujia.backend.common.response.ApiResponse;
import com.yujia.backend.common.response.PageResponse;
import com.yujia.backend.dto.report.InspectionReportCreateRequest;
import com.yujia.backend.dto.report.InspectionReportUpdateRequest;
import com.yujia.backend.entity.InspectionReport;
import com.yujia.backend.service.InspectionReportImportService;
import com.yujia.backend.service.InspectionReportService;
import com.yujia.backend.vo.InspectionReportImportVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/inspection-reports")
@RequiredArgsConstructor
public class InspectionReportController {

    private final AuthPermissionService authPermissionService;
    private final InspectionReportService inspectionReportService;
    private final InspectionReportImportService inspectionReportImportService;

    @GetMapping
    public ApiResponse<List<InspectionReport>> list(@RequestParam(required = false) Long batchId,
                                                    @RequestParam(required = false) Integer resultStatus) {
        authPermissionService.requireStaff();
        return ApiResponse.success(inspectionReportService.list(batchId, resultStatus));
    }

    @GetMapping("/page")
    public ApiResponse<PageResponse<InspectionReport>> page(@RequestParam(required = false) Long batchId,
                                                            @RequestParam(required = false) Integer resultStatus,
                                                            @RequestParam(required = false) Integer pageNum,
                                                            @RequestParam(required = false) Integer pageSize) {
        authPermissionService.requireStaff();
        return ApiResponse.success(inspectionReportService.page(batchId, resultStatus, pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public ApiResponse<InspectionReport> detail(@PathVariable Long id) {
        authPermissionService.requireStaff();
        return ApiResponse.success(inspectionReportService.detail(id));
    }

    @PostMapping
    public ApiResponse<InspectionReport> create(@Valid @RequestBody InspectionReportCreateRequest request) {
        authPermissionService.requireStaff();
        return ApiResponse.success(inspectionReportService.create(request));
    }

    @PostMapping("/import")
    public ApiResponse<InspectionReportImportVO> importReport(@RequestParam Long batchId,
                                                              @RequestParam String agencyName,
                                                              @RequestParam(required = false) String inspectorName,
                                                              @RequestParam String inspectionTime,
                                                              @RequestParam Integer resultStatus,
                                                              @RequestParam(required = false) String conclusion,
                                                              @RequestParam("file") MultipartFile file) {
        authPermissionService.requireStaff();
        return ApiResponse.success(inspectionReportImportService.importForTesting(
                batchId,
                agencyName,
                inspectorName,
                LocalDateTime.parse(inspectionTime),
                resultStatus,
                conclusion,
                file
        ));
    }

    @PutMapping("/{id}")
    public ApiResponse<InspectionReport> update(@PathVariable Long id,
                                                @Valid @RequestBody InspectionReportUpdateRequest request) {
        authPermissionService.requireStaff();
        return ApiResponse.success(inspectionReportService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        authPermissionService.requireAdmin();
        inspectionReportService.delete(id);
        return ApiResponse.success();
    }
}

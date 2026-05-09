package com.yujia.backend.controller;

import com.yujia.backend.common.auth.AuthPermissionService;
import com.yujia.backend.common.response.ApiResponse;
import com.yujia.backend.service.CropInfoImportService;
import com.yujia.backend.vo.CropInfoImportVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/crop-import")
@RequiredArgsConstructor
public class CropInfoImportController {

    private final AuthPermissionService authPermissionService;
    private final CropInfoImportService cropInfoImportService;

    @PostMapping("/quick")
    public ApiResponse<CropInfoImportVO> quickImport(@RequestParam("file") MultipartFile file) {
        authPermissionService.requireStaff();
        return ApiResponse.success(cropInfoImportService.importFromJsonFile(file));
    }
}

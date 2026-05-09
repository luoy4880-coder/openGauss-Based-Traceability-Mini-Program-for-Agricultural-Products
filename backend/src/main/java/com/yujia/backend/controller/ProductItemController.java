package com.yujia.backend.controller;

import com.yujia.backend.common.auth.AuthPermissionService;
import com.yujia.backend.common.response.ApiResponse;
import com.yujia.backend.dto.item.ProductItemGenerateRequest;
import com.yujia.backend.entity.ProductItem;
import com.yujia.backend.service.ProductItemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/product-items")
@RequiredArgsConstructor
public class ProductItemController {

    private final AuthPermissionService authPermissionService;
    private final ProductItemService productItemService;

    @GetMapping
    public ApiResponse<List<ProductItem>> list(@RequestParam Long batchId) {
        authPermissionService.requireStaff();
        return ApiResponse.success(productItemService.listByBatchId(batchId));
    }

    @PostMapping("/generate")
    public ApiResponse<List<ProductItem>> generate(@Valid @RequestBody ProductItemGenerateRequest request) {
        authPermissionService.requireStaff();
        return ApiResponse.success(productItemService.generate(request));
    }
}

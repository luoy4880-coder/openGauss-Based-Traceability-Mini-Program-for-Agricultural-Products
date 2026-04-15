package com.yujia.backend.controller;

import com.yujia.backend.common.response.ApiResponse;
import com.yujia.backend.common.response.PageResponse;
import com.yujia.backend.dto.user.UserCreateRequest;
import com.yujia.backend.dto.user.UserPasswordUpdateRequest;
import com.yujia.backend.dto.user.UserUpdateRequest;
import com.yujia.backend.service.SysUserService;
import com.yujia.backend.vo.UserVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class SysUserController {

    private final SysUserService sysUserService;

    @GetMapping("/page")
    public ApiResponse<PageResponse<UserVO>> page(@RequestParam(required = false) String keyword,
                                                  @RequestParam(required = false) Integer status,
                                                  @RequestParam(required = false) Integer pageNum,
                                                  @RequestParam(required = false) Integer pageSize) {
        return ApiResponse.success(sysUserService.page(keyword, status, pageNum, pageSize));
    }

    @GetMapping("/{id}")
    public ApiResponse<UserVO> detail(@PathVariable Long id) {
        return ApiResponse.success(sysUserService.detail(id));
    }

    @PostMapping
    public ApiResponse<UserVO> create(@Valid @RequestBody UserCreateRequest request) {
        return ApiResponse.success(sysUserService.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<UserVO> update(@PathVariable Long id, @Valid @RequestBody UserUpdateRequest request) {
        return ApiResponse.success(sysUserService.update(id, request));
    }

    @PutMapping("/{id}/password")
    public ApiResponse<Void> updatePassword(@PathVariable Long id,
                                            @Valid @RequestBody UserPasswordUpdateRequest request) {
        sysUserService.updatePassword(id, request);
        return ApiResponse.success();
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        sysUserService.delete(id);
        return ApiResponse.success();
    }
}

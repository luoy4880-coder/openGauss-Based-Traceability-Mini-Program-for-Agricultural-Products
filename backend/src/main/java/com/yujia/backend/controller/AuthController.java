package com.yujia.backend.controller;

import com.yujia.backend.common.response.ApiResponse;
import com.yujia.backend.dto.auth.BootstrapAdminRequest;
import com.yujia.backend.dto.auth.BindAccountRequest;
import com.yujia.backend.dto.auth.LoginRequest;
import com.yujia.backend.dto.auth.ProfileUpdateRequest;
import com.yujia.backend.dto.auth.RegisterRequest;
import com.yujia.backend.dto.auth.WeChatLoginRequest;
import com.yujia.backend.service.AuthService;
import com.yujia.backend.vo.LoginVO;
import com.yujia.backend.vo.UserVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ApiResponse<LoginVO> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(authService.login(request));
    }

    @PostMapping("/wechat-login")
    public ApiResponse<LoginVO> wechatLogin(@Valid @RequestBody WeChatLoginRequest request) {
        return ApiResponse.success(authService.wechatLogin(request));
    }

    @PostMapping("/register")
    public ApiResponse<LoginVO> register(@Valid @RequestBody RegisterRequest request) {
        return ApiResponse.success(authService.register(request));
    }

    @PostMapping("/bind")
    public ApiResponse<UserVO> bind(@Valid @RequestBody BindAccountRequest request) {
        return ApiResponse.success(authService.bindAccount(request));
    }

    @PostMapping("/bootstrap")
    public ApiResponse<UserVO> bootstrap(@Valid @RequestBody BootstrapAdminRequest request) {
        return ApiResponse.success(authService.bootstrapAdmin(request));
    }

    @GetMapping("/me")
    public ApiResponse<UserVO> me() {
        return ApiResponse.success(authService.currentUser());
    }

    @PutMapping("/profile")
    public ApiResponse<UserVO> updateProfile(@Valid @RequestBody ProfileUpdateRequest request) {
        return ApiResponse.success(authService.updateProfile(request));
    }

}

package com.yujia.backend.controller;

import com.yujia.backend.common.auth.AuthContext;
import com.yujia.backend.common.auth.AuthPermissionService;
import com.yujia.backend.common.response.ApiResponse;
import com.yujia.backend.dto.ai.StaffAiChatRequest;
import com.yujia.backend.dto.ai.UserAiChatRequest;
import com.yujia.backend.service.AiChatService;
import com.yujia.backend.service.AiRateLimitService;
import com.yujia.backend.vo.AiChatAnswerVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/ai")
public class AiChatController {

    private final AuthPermissionService authPermissionService;
    private final AiChatService aiChatService;
    private final AiRateLimitService aiRateLimitService;

    @PostMapping("/staff-chat")
    public ApiResponse<AiChatAnswerVO> staffChat(@Valid @RequestBody StaffAiChatRequest request) {
        authPermissionService.requireStaff();
        var currentUser = AuthContext.get();
        aiRateLimitService.checkStaffLimit(currentUser == null ? null : currentUser.getUserId());
        return ApiResponse.success(aiChatService.answerForStaff(request));
    }

    @PostMapping("/user-chat")
    public ApiResponse<AiChatAnswerVO> userChat(@Valid @RequestBody UserAiChatRequest request,
                                                HttpServletRequest httpServletRequest) {
        aiRateLimitService.checkUserLimit(resolveClientKey(httpServletRequest), request.getTraceId());
        return ApiResponse.success(aiChatService.answerForUser(request));
    }

    private String resolveClientKey(HttpServletRequest request) {
        String forwardedFor = request.getHeader("X-Forwarded-For");
        if (forwardedFor != null && !forwardedFor.isBlank()) {
            return forwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}

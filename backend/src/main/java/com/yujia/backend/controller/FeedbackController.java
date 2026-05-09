package com.yujia.backend.controller;

import com.yujia.backend.common.auth.AuthPermissionService;
import com.yujia.backend.common.response.ApiResponse;
import com.yujia.backend.common.response.PageResponse;
import com.yujia.backend.dto.feedback.FeedbackHandleRequest;
import com.yujia.backend.dto.feedback.FeedbackSubmitRequest;
import com.yujia.backend.entity.UserFeedback;
import com.yujia.backend.service.FeedbackService;
import com.yujia.backend.vo.FeedbackOverviewVO;
import com.yujia.backend.vo.FeedbackTaskVO;
import com.yujia.backend.vo.StaffOptionVO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/feedback")
@RequiredArgsConstructor
public class FeedbackController {

    private final AuthPermissionService authPermissionService;
    private final FeedbackService feedbackService;

    @GetMapping("/my")
    public ApiResponse<List<UserFeedback>> my(@RequestParam(required = false) Integer limit) {
        return ApiResponse.success(feedbackService.myLatest(limit));
    }

    @PostMapping
    public ApiResponse<Map<String, Long>> submit(@Valid @RequestBody FeedbackSubmitRequest request) {
        return ApiResponse.success(Map.of("id", feedbackService.submit(request)));
    }

    @GetMapping("/page")
    public ApiResponse<PageResponse<FeedbackTaskVO>> page(@RequestParam(required = false) String keyword,
                                                          @RequestParam(required = false) String category,
                                                          @RequestParam(required = false) Integer priority,
                                                          @RequestParam(required = false) String riskLevel,
                                                          @RequestParam(required = false) Integer status,
                                                          @RequestParam(required = false) Long assigneeUserId,
                                                          @RequestParam(required = false) Integer pageNum,
                                                          @RequestParam(required = false) Integer pageSize) {
        authPermissionService.requireStaff();
        return ApiResponse.success(feedbackService.page(
                keyword, category, priority, riskLevel, status, assigneeUserId, pageNum, pageSize
        ));
    }

    @GetMapping("/overview")
    public ApiResponse<FeedbackOverviewVO> overview() {
        authPermissionService.requireStaff();
        return ApiResponse.success(feedbackService.overview());
    }

    @GetMapping("/assignees")
    public ApiResponse<List<StaffOptionVO>> assignees() {
        authPermissionService.requireStaff();
        return ApiResponse.success(feedbackService.assignees());
    }

    @PutMapping("/{id}/handle")
    public ApiResponse<Void> handle(@PathVariable Long id, @Valid @RequestBody FeedbackHandleRequest request) {
        authPermissionService.requireStaff();
        feedbackService.handle(id, request);
        return ApiResponse.success();
    }
}

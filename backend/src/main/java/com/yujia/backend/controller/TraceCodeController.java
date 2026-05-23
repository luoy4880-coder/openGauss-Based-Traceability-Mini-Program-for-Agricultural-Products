package com.yujia.backend.controller;

import com.yujia.backend.common.auth.AuthPermissionService;
import com.yujia.backend.common.response.ApiResponse;
import com.yujia.backend.dto.trace.TraceCodeGenerateRequest;
import com.yujia.backend.entity.TraceCode;
import com.yujia.backend.service.TraceCodeService;
import com.yujia.backend.service.TraceSummaryService;
import com.yujia.backend.vo.TraceDetailVO;
import com.yujia.backend.vo.TraceSummaryVO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class TraceCodeController {

    private final AuthPermissionService authPermissionService;
    private final TraceCodeService traceCodeService;
    private final TraceSummaryService traceSummaryService;

    @GetMapping("/api/trace-codes")
    public ApiResponse<List<TraceCode>> list(@RequestParam Long batchId) {
        authPermissionService.requireStaff();
        return ApiResponse.success(traceCodeService.listByBatchId(batchId));
    }

    @PostMapping("/api/trace-codes/generate")
    public ApiResponse<TraceCode> generate(@Valid @RequestBody TraceCodeGenerateRequest request) {
        authPermissionService.requireStaff();
        return ApiResponse.success(traceCodeService.generate(request));
    }

    @GetMapping("/api/trace/{traceId}")
    public ApiResponse<TraceDetailVO> traceDetail(@PathVariable String traceId,
                                                  @RequestParam(required = false) String sign,
                                                  HttpServletRequest request) {
        return ApiResponse.success(traceCodeService.getTraceDetail(
                traceId,
                sign,
                request.getRemoteAddr(),
                request.getHeader("User-Agent")
        ));
    }

    @GetMapping("/api/trace/{traceId}/summary")
    public ApiResponse<TraceSummaryVO> traceSummary(@PathVariable String traceId,
                                                    @RequestParam(required = false) String sign) {
        return ApiResponse.success(traceSummaryService.summary(traceId, sign));
    }
}

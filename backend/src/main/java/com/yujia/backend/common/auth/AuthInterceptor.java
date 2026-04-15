package com.yujia.backend.common.auth;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yujia.backend.common.response.ApiResponse;
import com.yujia.backend.service.SysUserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class AuthInterceptor implements HandlerInterceptor {

    private final SysUserService sysUserService;
    private final ObjectMapper objectMapper;
    private final TokenUtil tokenUtil = new TokenUtil();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }

        String authorization = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (authorization == null || !authorization.startsWith("Bearer ")) {
            writeUnauthorized(response, "未登录或登录已失效");
            return false;
        }

        String token = authorization.substring(7);
        TokenPayload payload = tokenUtil.parseToken(token);
        if (payload == null) {
            writeUnauthorized(response, "登录令牌无效");
            return false;
        }

        var currentUser = sysUserService.loadAuthUser(payload.getUserId());
        if (currentUser == null) {
            writeUnauthorized(response, "用户不存在");
            return false;
        }

        AuthContext.set(currentUser);
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        AuthContext.clear();
    }

    private void writeUnauthorized(HttpServletResponse response, String message) throws Exception {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.getWriter().write(objectMapper.writeValueAsString(ApiResponse.fail(401, message)));
    }
}

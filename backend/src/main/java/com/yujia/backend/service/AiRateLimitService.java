package com.yujia.backend.service;

import com.yujia.backend.common.exception.BusinessException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class AiRateLimitService {

    private final ConcurrentHashMap<String, CounterWindow> counters = new ConcurrentHashMap<>();

    @Value("${app.ai.chat.staff-limit-per-minute:30}")
    private int staffLimitPerMinute;

    @Value("${app.ai.chat.user-limit-per-minute:12}")
    private int userLimitPerMinute;

    public void checkStaffLimit(Long userId) {
        String key = "staff:" + (userId == null ? "anonymous" : userId);
        if (!allow(key, Math.max(staffLimitPerMinute, 1))) {
            throw new BusinessException(429, "AI 问答过于频繁，请稍后再试。");
        }
    }

    public void checkUserLimit(String clientKey, String traceId) {
        String key = "user:" + safe(clientKey) + ":" + safe(traceId);
        if (!allow(key, Math.max(userLimitPerMinute, 1))) {
            throw new BusinessException(429, "提问过于频繁，请稍后再试。");
        }
    }

    private boolean allow(String key, int limit) {
        long minuteWindow = Instant.now().getEpochSecond() / 60;
        CounterWindow window = counters.compute(key, (ignored, existing) -> {
            if (existing == null || existing.windowMinute != minuteWindow) {
                return new CounterWindow(minuteWindow);
            }
            return existing;
        });
        return window.counter.incrementAndGet() <= limit;
    }

    private String safe(String value) {
        if (value == null || value.isBlank()) {
            return "unknown";
        }
        return value.trim();
    }

    private static final class CounterWindow {
        private final long windowMinute;
        private final AtomicInteger counter = new AtomicInteger();

        private CounterWindow(long windowMinute) {
            this.windowMinute = windowMinute;
        }
    }
}

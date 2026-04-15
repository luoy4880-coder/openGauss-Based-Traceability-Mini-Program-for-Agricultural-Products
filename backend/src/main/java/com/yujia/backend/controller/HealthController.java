package com.yujia.backend.controller;

import com.yujia.backend.common.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class HealthController {

    private final Environment environment;

    private final JdbcTemplate jdbcTemplate;

    private final DataSource dataSource;

    @GetMapping("/health")
    public ApiResponse<Map<String, Object>> health() {
        Map<String, Object> data = new LinkedHashMap<>();
        data.put("status", "ok");
        data.put("application", environment.getProperty("spring.application.name", "backend"));
        data.put("profiles", Arrays.asList(environment.getActiveProfiles()));
        data.put("timestamp", LocalDateTime.now());
        return ApiResponse.success(data);
    }

    @GetMapping("/db/ping")
    public ApiResponse<Map<String, Object>> pingDatabase() throws SQLException {
        Map<String, Object> data = new LinkedHashMap<>();
        Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);

        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData metaData = connection.getMetaData();
            data.put("databaseProductName", metaData.getDatabaseProductName());
            data.put("databaseProductVersion", metaData.getDatabaseProductVersion());
            data.put("databaseUrl", metaData.getURL());
        }

        data.put("validationQuery", "SELECT 1");
        data.put("validationResult", result);
        return ApiResponse.success(data);
    }
}

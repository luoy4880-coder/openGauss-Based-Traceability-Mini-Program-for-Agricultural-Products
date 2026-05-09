package com.yujia.backend.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "app.file-storage")
public class FileStorageProperties {

    private String uploadDir = "uploads";

    private String accessPath = "/uploads/**";

    private String urlPrefix = "/uploads";
}

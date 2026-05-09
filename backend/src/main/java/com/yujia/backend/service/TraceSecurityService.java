package com.yujia.backend.service;

import com.yujia.backend.common.exception.BusinessException;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

@Service
public class TraceSecurityService {

    public String sign(String batchCode, String traceId) {
        return sign(batchCode, traceId, "");
    }

    public String sign(String batchCode, String traceId, String itemCode) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
            byte[] digest = messageDigest.digest((batchCode + ":" + traceId + ":" + itemCode).getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException exception) {
            throw new BusinessException(500, "生成溯源码签名失败");
        }
    }

    public boolean verify(String batchCode, String traceId, String itemCode, String signValue) {
        return sign(batchCode, traceId, itemCode).equals(signValue);
    }
}

package com.yujia.backend.common.auth;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class TokenUtil {

    private static final String SECRET = "yujia-backend-admin-token-secret";
    private static final long EXPIRE_MILLIS = 7L * 24 * 60 * 60 * 1000;

    public String generateToken(Long userId, String username) {
        long expiresAt = System.currentTimeMillis() + EXPIRE_MILLIS;
        String payload = userId + ":" + username + ":" + expiresAt;
        String signature = sign(payload);
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString((payload + ":" + signature).getBytes(StandardCharsets.UTF_8));
    }

    public TokenPayload parseToken(String token) {
        try {
            String raw = new String(Base64.getUrlDecoder().decode(token), StandardCharsets.UTF_8);
            String[] parts = raw.split(":");
            if (parts.length != 4) {
                return null;
            }

            String payload = parts[0] + ":" + parts[1] + ":" + parts[2];
            if (!sign(payload).equals(parts[3])) {
                return null;
            }

            long expiresAt = Long.parseLong(parts[2]);
            if (expiresAt < System.currentTimeMillis()) {
                return null;
            }

            return TokenPayload.builder()
                    .userId(Long.parseLong(parts[0]))
                    .username(parts[1])
                    .expiresAt(expiresAt)
                    .build();
        } catch (Exception exception) {
            return null;
        }
    }

    private String sign(String payload) {
        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(SECRET.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            byte[] digest = mac.doFinal(payload.getBytes(StandardCharsets.UTF_8));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
        } catch (Exception exception) {
            throw new IllegalStateException("token sign failed", exception);
        }
    }
}

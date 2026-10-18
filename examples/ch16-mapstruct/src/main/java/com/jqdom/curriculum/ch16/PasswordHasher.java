package com.jqdom.curriculum.ch16;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HexFormat;

import org.springframework.stereotype.Component;

/**
 * 학습용 해시. 솔트가 없는 SHA-256이라 실제 서비스에는 쓰면 안 된다.
 * ch30(Spring Security)에서 BCrypt로 바꾼다.
 */
@Component
public class PasswordHasher {

    public String hash(String raw) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256").digest(raw.getBytes(StandardCharsets.UTF_8));
            return HexFormat.of().formatHex(digest);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}

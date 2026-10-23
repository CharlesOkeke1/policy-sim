package org.cokeke.policysim.common;

import java.security.SecureRandom;
import java.util.Base64;
import org.springframework.stereotype.Service;

@Service
public class TokenGenerator {
    private static final SecureRandom random = new SecureRandom();

    public static String generateTokenString() {
        byte[] bytes = new byte[32];
        random.nextBytes(bytes);

        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }

    public static String generateRefreshToken() {
        byte[] bytes = new byte[24]; 
        random.nextBytes(bytes);

        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}


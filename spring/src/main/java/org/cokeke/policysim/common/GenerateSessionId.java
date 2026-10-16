package org.cokeke.policysim.common;

import java.security.SecureRandom;
import java.util.Base64;
import org.springframework.stereotype.Service;

@Service
public class GenerateSessionId {
    private static final SecureRandom random = new SecureRandom();

    public static String generateId() {
        byte[] bytes = new byte[16];
        random.nextBytes(bytes);

        return Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(bytes);
    }
    
}

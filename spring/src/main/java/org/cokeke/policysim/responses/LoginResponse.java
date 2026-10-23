package org.cokeke.policysim.responses;

import java.time.LocalDateTime;

public record LoginResponse(Long id, String email, String name, String authToken, LocalDateTime tokenExpirationTime,
    String refreshToken, LocalDateTime sessionStartTime, Long sessionId
) {}
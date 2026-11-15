package org.cokeke.policysim.responses;

import java.time.LocalDateTime;
import org.cokeke.policysim.models.Role;

public record MeResponse(Long id, String email, String name, String authToken, LocalDateTime sessionExpirationTime,
    String refreshToken, LocalDateTime sessionStartTime, Long sessionId, Role role
) {}
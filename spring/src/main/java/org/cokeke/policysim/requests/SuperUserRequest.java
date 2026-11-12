package org.cokeke.policysim.requests;
import java.time.LocalDateTime;

// Should actually be ServerStatusRequest
public record SuperUserRequest(Long userId, String secret, boolean status, LocalDateTime expectedUpTime) {}

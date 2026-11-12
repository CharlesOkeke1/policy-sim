package org.cokeke.policysim.responses;
import java.time.LocalDateTime;

public record ServerStatusResponse(boolean serverStatus, LocalDateTime expectedUpTime) {}

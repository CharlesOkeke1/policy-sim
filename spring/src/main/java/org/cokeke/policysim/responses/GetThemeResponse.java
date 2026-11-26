package org.cokeke.policysim.responses;
import java.time.LocalDateTime;

public record GetThemeResponse(String theme, LocalDateTime lastChanged) {}
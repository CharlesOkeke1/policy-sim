package org.cokeke.policysim.responses;

import java.time.LocalDateTime;

public record BatchRoundPushResponse(Long gameId, String stateName, LocalDateTime pushTime) {}
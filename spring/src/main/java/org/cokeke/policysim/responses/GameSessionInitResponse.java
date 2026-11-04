package org.cokeke.policysim.responses;
import java.time.Instant;

public record GameSessionInitResponse(
        Long id,
        Long userId,
        Long gameId,
        Instant startTime,
        Instant endTime,
        String engineToken
) {}
package org.cokeke.policysim.requests;
import java.time.Instant;

public record CreateGameRequest(String stateName, Instant startDate, Long userId) {}
package org.cokeke.policysim.responses;
import java.time.Instant;
import java.util.List;

import org.cokeke.policysim.models.Round;

public record GameResponse(Long id, int seed, int noOfRounds, String stateName, Instant startDate, Instant endDate, UserResponse user, List<Round> rounds, Boolean completed) {}
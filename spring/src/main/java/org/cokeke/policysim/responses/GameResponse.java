package org.cokeke.policysim.responses;
import java.time.Instant;
import java.util.List;

import org.cokeke.policysim.dtos.RoundWithoutGame;

public record GameResponse(Long id, int seed, int noOfRounds,
    String stateId, Instant startDate, Instant endDate, 
    UserResponse user, List<RoundWithoutGame> rounds, 
    Boolean completed, Double score, int rank, String difficulty) {}
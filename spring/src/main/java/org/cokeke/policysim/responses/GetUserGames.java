package org.cokeke.policysim.responses;

import java.time.Instant;
import java.util.List;

import org.cokeke.policysim.dtos.RoundWithoutGame;

public record GetUserGames(Long id, int seed, int noOfRounds,
    String stateName, Instant startDate, Instant endDate, 
    List<RoundWithoutGame> rounds, Boolean completed, 
    Double score, int rank, String difficulty) {
    
}

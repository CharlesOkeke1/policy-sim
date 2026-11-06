package org.cokeke.policysim.responses;
import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Map;

public record GameReceiptResponse(
        // Title block
        Long gameId,
        String stateId,
        String stateName,
        boolean completed,
        String difficulty,
        int totalRounds,            // months the game ran for
        int seed,
        Duration playTime,          // endDate - startDate
        Instant generatedAt,

        // Result strip
        int finalRank,
        double finalScore,
        double scoreVsAverage,      // finalScore minus the player's average across their other completed games

        // "Your economy, start to finish"
        EconomySnapshot start,      // month 0
        EconomySnapshot end,        // final month

        // Final standings, rank order
        List<Standing> standings,

        // "Policies you chose": readable policy name -> months used, most used first
        Map<String, Integer> policies,

        // Footer
        String playerName
) {
    public record EconomySnapshot(
            double realGrowth,      // annualised
            double inflation,       // annualised
            double unemployment,
            double debtToGdp,
            double stability) {}

    public record Standing(
            int rank,
            String stateId,
            String stateName,
            boolean isPlayer,       // true for the user's city; the rest are "Computer"
            double score) {}
}
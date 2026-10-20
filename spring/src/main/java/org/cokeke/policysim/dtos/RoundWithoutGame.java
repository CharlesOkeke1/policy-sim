package org.cokeke.policysim.dtos;

public record RoundWithoutGame(
        Long id,
        int currentMonth,
        String stateId,
        String stateName,
        double population,
        double productivity,
        double publicCapital,
        double priceLevel,
        double lastInflation,
        double householdWealth,
        double governmentDebt,
        double stability,
        double taxRate,
        double spendingShare,
        double investmentShare,
        double transferShare,
        double roundScore,
        int roundRank
) {}
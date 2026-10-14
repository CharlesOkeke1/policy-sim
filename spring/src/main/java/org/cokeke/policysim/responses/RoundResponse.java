package org.cokeke.policysim.responses;

public record RoundResponse(Long id, int currentMonth, Long gameId, 
                            String stateId, String stateName, double population, double productivity,
                            double publicCapital, double priceLevel, double lastInflation, double householdWealth,
                            double governmentDebt, double stability, double taxRate, double spendingShare,
                            double investmentShare, double transferShare
) {}
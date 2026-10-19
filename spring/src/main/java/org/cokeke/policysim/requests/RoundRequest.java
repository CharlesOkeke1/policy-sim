package org.cokeke.policysim.requests;

public record RoundRequest(int currentMonth, String stateId, String stateName, double population, double productivity,
                           double publicCapital, double priceLevel, double lastInflation, double householdWealth,
                           double governmentDebt, double stability, double taxRate, double spendingShare, double investmentShare,
                           double transferShare, double output, double outputGap, double unemployment, double federalTax,
                           double allocation, double spending, double transfers, double interest, String policy,
                           double federalResourcePrice, double nationalNetWorth, double federalTaxShare) {}
package org.cokeke.policyengine.models; 

public record SpringRoundDTO(
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
    double transferShare
) {
    public SpringRoundDTO(StateEconomy state) {
        this(
            state.currentMonth(),
            state.id().toString(),
            state.name(),
            state.population(),
            state.productivity(),
            state.publicCapital(),
            state.priceLevel(),
            state.lastInflation(),
            state.householdWealth(),
            state.governmentDebt(),
            state.stability(),
            state.taxRate(),
            state.spendingShare(),
            state.investmentShare(),
            state.transferShare()
        );
    }
}
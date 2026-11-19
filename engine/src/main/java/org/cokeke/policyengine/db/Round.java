package org.cokeke.policyengine.db;

import org.cokeke.policyengine.models.SpringRoundDTO;

public class Round {
    private int currentMonth;
    private String stateId;
    private String stateName;
    private double population;
    private double productivity;
    private double publicCapital;
    private double priceLevel;
    private double lastInflation;
    private double householdWealth;
    private double governmentDebt;
    private double stability;
    private double taxRate;
    private double spendingShare;
    private double investmentShare;
    private double transferShare;
    private double output;
    private double outputGap;
    private double unemployment;
    private double federalTax;
    private double allocation;
    private double spending;
    private double transfers;
    private double interest;
    private String policy;
    private double resourcePrice;
    private double netWorth;
    private double federalTaxShare;
    private double roundScore;
    private int roundRank;

    public Round() {}

    public Round(SpringRoundDTO dto) {
        this.currentMonth = dto.currentMonth();
        this.stateId = dto.stateId();
        this.stateName = dto.stateName();
        this.population = dto.population();
        this.productivity = dto.productivity();
        this.publicCapital = dto.publicCapital();
        this.priceLevel = dto.priceLevel();
        this.lastInflation = dto.lastInflation();
        this.householdWealth = dto.householdWealth();
        this.governmentDebt = dto.governmentDebt();
        this.stability = dto.stability();
        this.taxRate = dto.taxRate();
        this.spendingShare = dto.spendingShare();
        this.investmentShare = dto.investmentShare();
        this.transferShare = dto.transferShare();
        this.output = dto.output();
        this.outputGap = dto.outputGap();
        this.unemployment = dto.unemployment();
        this.federalTax = dto.federalTax();
        this.allocation = dto.allocation();
        this.spending = dto.spending();
        this.transfers = dto.transfers();
        this.interest = dto.interest();
        this.policy = dto.policy();
        this.resourcePrice = dto.resourcePrice();
        this.netWorth = dto.netWorth();
        this.federalTaxShare = dto.federalTaxShare();
        this.roundScore = dto.roundScore();
        this.roundRank = dto.roundRank();
    }

    public SpringRoundDTO toSpringRoundDTO() {
        return new SpringRoundDTO(
            currentMonth,
            stateId,
            stateName,
            population,
            productivity,
            publicCapital,
            priceLevel,
            lastInflation,
            householdWealth,
            governmentDebt,
            stability,
            taxRate,
            spendingShare,
            investmentShare,
            transferShare,
            output,
            outputGap,
            unemployment,
            federalTax,
            allocation,
            spending,
            transfers,
            interest,
            policy,
            resourcePrice,
            netWorth,
            federalTaxShare,
            roundScore,
            roundRank
        );
    }

    public int getCurrentMonth() { return currentMonth; }
    public String getStateId() { return stateId; }
    public String getStateName() { return stateName; }
    public double getPopulation() { return population; }
    public double getProductivity() { return productivity; }
    public double getPublicCapital() { return publicCapital; }
    public double getPriceLevel() { return priceLevel; }
    public double getLastInflation() { return lastInflation; }
    public double getHouseholdWealth() { return householdWealth; }
    public double getGovernmentDebt() { return governmentDebt; }
    public double getStability() { return stability; }
    public double getTaxRate() { return taxRate; }
    public double getSpendingShare() { return spendingShare; }
    public double getInvestmentShare() { return investmentShare; }
    public double getTransferShare() { return transferShare; }
    public double getOutput() { return output; }
    public double getOutputGap() { return outputGap; }
    public double getUnemployment() { return unemployment; }
    public double getFederalTax() { return federalTax; }
    public double getAllocation() { return allocation; }
    public double getSpending() { return spending; }
    public double getTransfers() { return transfers; }
    public double getInterest() { return interest; }
    public String getPolicy() { return policy; }
    public double getResourcePrice() { return resourcePrice; }
    public double getNetWorth() { return netWorth; }
    public double getFederalTaxShare() { return federalTaxShare; }
    public double getRoundScore() { return roundScore; }
    public int getRoundRank() { return roundRank; }

    public void setCurrentMonth(int currentMonth) { this.currentMonth = currentMonth; }
    public void setStateId(String stateId) { this.stateId = stateId; }
    public void setStateName(String stateName) { this.stateName = stateName; }
    public void setPopulation(double population) { this.population = population; }
    public void setProductivity(double productivity) { this.productivity = productivity; }
    public void setPublicCapital(double publicCapital) { this.publicCapital = publicCapital; }
    public void setPriceLevel(double priceLevel) { this.priceLevel = priceLevel; }
    public void setLastInflation(double lastInflation) { this.lastInflation = lastInflation; }
    public void setHouseholdWealth(double householdWealth) { this.householdWealth = householdWealth; }
    public void setGovernmentDebt(double governmentDebt) { this.governmentDebt = governmentDebt; }
    public void setStability(double stability) { this.stability = stability; }
    public void setTaxRate(double taxRate) { this.taxRate = taxRate; }
    public void setSpendingShare(double spendingShare) { this.spendingShare = spendingShare; }
    public void setInvestmentShare(double investmentShare) { this.investmentShare = investmentShare; }
    public void setTransferShare(double transferShare) { this.transferShare = transferShare; }
    public void setOutput(double output) { this.output = output; }
    public void setOutputGap(double outputGap) { this.outputGap = outputGap; }
    public void setUnemployment(double unemployment) { this.unemployment = unemployment; }
    public void setFederalTax(double federalTax) { this.federalTax = federalTax; }
    public void setAllocation(double allocation) { this.allocation = allocation; }
    public void setSpending(double spending) { this.spending = spending; }
    public void setTransfers(double transfers) { this.transfers = transfers; }
    public void setInterest(double interest) { this.interest = interest; }
    public void setPolicy(String policy) { this.policy = policy; }
    public void setResourcePrice(double resourcePrice) { this.resourcePrice = resourcePrice; }
    public void setNetWorth(double netWorth) { this.netWorth = netWorth; }
    public void setFederalTaxShare(double federalTaxShare) { this.federalTaxShare = federalTaxShare; }
    public void setRoundScore(double roundScore) { this.roundScore = roundScore; }
    public void setRoundRank(int roundRank) { this.roundRank = roundRank; }
}

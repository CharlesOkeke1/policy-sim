package org.cokeke.policyengine.models;

import java.util.Objects;

public record StateEconomy(
    StateId id,
    String name,
    int currentMonth,
    // supply stocks
    double population,
    double productivity,
    double publicCapital,
    // price stocks
    double priceLevel,
    double lastInflation,
    // financial and social stocks
    double householdWealth,
    double governmentDebt,
    double stability,
    // levers
    double taxRate,
    double spendingShare,
    double investmentShare,
    double transferShare
) {
    public StateEconomy {
        Objects.requireNonNull(id, "ID cannot be null");
        Objects.requireNonNull(name, "Name");
        String p = id + ".";
        Checks.positive(p + "population", population);
        Checks.positive(p + "productivity", productivity);
        Checks.nonNegative(p + "publicCapital", publicCapital);
        Checks.positive(p + "priceLevel", priceLevel);
        Checks.finite(p + "lastInflation", lastInflation);
        Checks.finite(p + "householdWealth", householdWealth);
        Checks.finite(p + "governmentDebt", governmentDebt);
        Checks.inRange(p + "stability", stability, 0, 100);
        checkLever(p, taxRate, Lever.TAX_RATE);
        checkLever(p, spendingShare, Lever.SPENDING_SHARE);
        checkLever(p, investmentShare, Lever.INVESTMENT_SHARE);
        checkLever(p, transferShare, Lever.TRANSFER_SHARE);
    }

    private static void checkLever(String name, double value, Lever lever) {
        Checks.inRange(name + lever.name(), value, lever.getMin(), lever.getMax());
    }

    public double lever(Lever lever) {
        return switch (lever) {
            case TAX_RATE -> taxRate;
            case SPENDING_SHARE -> spendingShare;
            case INVESTMENT_SHARE -> investmentShare;
            case TRANSFER_SHARE -> transferShare;
            case DEBT -> throw new IllegalArgumentException("DEBT is not a lever that can be read directly");
        };
    }

    public StateEconomy withLever(Lever lever, double value) {
        return switch (lever) {
            case TAX_RATE -> new StateEconomy(id, name,  currentMonth, population, productivity, publicCapital, priceLevel, lastInflation, householdWealth, governmentDebt, stability, value, spendingShare, investmentShare, transferShare);
            case SPENDING_SHARE -> new StateEconomy(id, name,  currentMonth, population, productivity, publicCapital, priceLevel, lastInflation, householdWealth, governmentDebt, stability, taxRate, value, investmentShare, transferShare);
            case INVESTMENT_SHARE -> new StateEconomy(id, name,  currentMonth, population, productivity, publicCapital, priceLevel, lastInflation, householdWealth, governmentDebt, stability, taxRate, spendingShare, value, transferShare);
            case TRANSFER_SHARE -> new StateEconomy(id, name,  currentMonth, population, productivity, publicCapital, priceLevel, lastInflation, householdWealth, governmentDebt, stability, taxRate, spendingShare, investmentShare,value);
            case DEBT -> new StateEconomy(id,name, currentMonth, population ,productivity ,publicCapital ,priceLevel ,lastInflation ,householdWealth ,value ,stability ,taxRate ,spendingShare ,investmentShare ,transferShare);
        };
    }

    public StateEconomy withAccounts(double newHouseholdWealth, double newGovernmentDebt) {
        return new StateEconomy(id, name, currentMonth, population, productivity, publicCapital, priceLevel, lastInflation,
                newHouseholdWealth, newGovernmentDebt, stability,
                taxRate, spendingShare, investmentShare, transferShare);
    }

    public StateEconomy withSupply(double newPopulation, double newProductivity, double newPublicCapital) {
        return new StateEconomy(id, name, currentMonth, newPopulation, newProductivity, newPublicCapital, priceLevel, lastInflation,
                householdWealth, governmentDebt, stability,
                taxRate, spendingShare, investmentShare, transferShare);
    }

    public StateEconomy withPrices(double newPriceLevel, double newLastInflation) {
        return new StateEconomy(id, name, currentMonth, population, productivity, publicCapital, newPriceLevel, newLastInflation,
                householdWealth, governmentDebt, stability,
                taxRate, spendingShare, investmentShare, transferShare);
    }

    public StateEconomy withStability(double newStability) {
        return new StateEconomy(id, name, currentMonth, population, productivity, publicCapital, priceLevel, lastInflation,
                householdWealth, governmentDebt, newStability,
                taxRate, spendingShare, investmentShare, transferShare);
    }

    public StateEconomy withCurrentMonth(int newMonth) {
        return new StateEconomy(id, name, newMonth, population, productivity, publicCapital, priceLevel, lastInflation,
                householdWealth, governmentDebt, stability,
                taxRate, spendingShare, investmentShare, transferShare);
    }
}
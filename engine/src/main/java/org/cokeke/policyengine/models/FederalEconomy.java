package org.cokeke.policyengine.models;
import java.math.BigDecimal;

public record FederalEconomy(double resourcePrice, double netWorth, double federalTaxShare) {
    public FederalEconomy {
        Checks.positive("federal.resourcePrice", resourcePrice);
        Checks.finite("federal.netWorth", netWorth);
        Checks.inRange("federal.federalTaxShare", federalTaxShare, 0, 0.4);
    }

    public FederalEconomy withResourcePrice(double price) {
        return new FederalEconomy(price, netWorth, federalTaxShare);
    }

    public FederalEconomy withNetWorth(double worth) {
        return new FederalEconomy(resourcePrice, worth, federalTaxShare);
    }
}
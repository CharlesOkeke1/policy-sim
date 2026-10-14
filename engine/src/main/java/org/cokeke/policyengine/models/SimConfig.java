package org.cokeke.policyengine.models;


/**
 * Every model parameter. No number lives in engine code. Monthly units throughout.
 * Build with SimConfig.builder(); defaults() and sim(...) are the two presets.
 *
 * PLACEHOLDER marks parameters the blueprint never valued. Each placeholder is neutral:
 * with resourceOutput = 0 and allocationShare = 1 the federal level is a pure pass-through,
 * and campaignSpendShare = 0 means elections cost nothing. Calibrate them deliberately.
 */
public record SimConfig(
        // demand
        double consumeFromIncome,
        double consumeFromWealth,
        double interestRate,
        // federal
        double allocationShare,
        double allocationEqualWeight,
        double allocationPopulationWeight,
        double allocationOutputWeight,
        double resourceOutput,
        double resourcePersistence,
        double resourceVolatility,
        // supply
        double capitalShare,
        double depreciation,
        double productivityGrowth,
        double populationGrowth,
        // prices and labour
        double inflationTarget,
        double expectationAnchor,
        double phillipsSlope,
        double naturalUnemployment,
        double okunCoefficient,
        // elections
        int electionSpacing,
        double campaignSpendShare,
        // engine
        double identityTolerance,
        boolean simMode,
        double simFixedSpending) {

    public SimConfig {
        Checks.inRange("consumeFromIncome", consumeFromIncome, 0, 1);
        Checks.inRange("consumeFromWealth", consumeFromWealth, 0, 1);
        Checks.finite("interestRate", interestRate);
        Checks.inRange("allocationShare", allocationShare, 0, 1);
        Checks.nonNegative("allocationEqualWeight", allocationEqualWeight);
        Checks.nonNegative("allocationPopulationWeight", allocationPopulationWeight);
        Checks.nonNegative("allocationOutputWeight", allocationOutputWeight);
        double weightSum = allocationEqualWeight + allocationPopulationWeight + allocationOutputWeight;
        if (Math.abs(weightSum - 1.0) > 1e-12) {
            throw new IllegalArgumentException("Allocation weights must sum to 1, got " + weightSum);
        }
        Checks.nonNegative("resourceOutput", resourceOutput);
        Checks.inRange("resourcePersistence", resourcePersistence, 0, 1);
        Checks.nonNegative("resourceVolatility", resourceVolatility);
        Checks.inRange("capitalShare", capitalShare, 0, 1);
        Checks.inRange("depreciation", depreciation, 0, 1);
        Checks.finite("productivityGrowth", productivityGrowth);
        Checks.finite("populationGrowth", populationGrowth);
        Checks.finite("inflationTarget", inflationTarget);
        Checks.inRange("expectationAnchor", expectationAnchor, 0, 1);
        Checks.nonNegative("phillipsSlope", phillipsSlope);
        Checks.inRange("naturalUnemployment", naturalUnemployment, 0, 1);
        Checks.nonNegative("okunCoefficient", okunCoefficient);
        if (electionSpacing <= 0) throw new IllegalArgumentException("electionSpacing must be > 0");
        Checks.nonNegative("campaignSpendShare", campaignSpendShare);
        Checks.positive("identityTolerance", identityTolerance);
        Checks.nonNegative("simFixedSpending", simFixedSpending);
    }

    public static SimConfig defaults() {
        return builder().build();
    }

    /**
     * Godley and Lavoie Model SIM: no interest, no federal flows, no transfers,
     * supply and prices frozen, nominal spending fixed. The federal tax share must
     * also be 0 in the World you pass in (it lives on FederalEconomy).
     */
    public static SimConfig sim(double fixedSpending) {
        return builder()
                .interestRate(0)
                .resourceOutput(0)
                .simMode(true)
                .simFixedSpending(fixedSpending)
                .build();
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private double consumeFromIncome = 0.6;
        private double consumeFromWealth = 0.4;
        private double interestRate = 0.004;
        private double allocationShare = 1.0;              // PLACEHOLDER
        private double allocationEqualWeight = 0.35;
        private double allocationPopulationWeight = 0.40;
        private double allocationOutputWeight = 0.25;
        private double resourceOutput = 0.0;               // PLACEHOLDER
        private double resourcePersistence = 0.95;         // PLACEHOLDER
        private double resourceVolatility = 0.05;          // PLACEHOLDER
        private double capitalShare = 0.3;
        private double depreciation = 0.005;
        private double productivityGrowth = 0.001;
        private double populationGrowth = 0.002;
        private double inflationTarget = 0.002;
        private double expectationAnchor = 0.3;
        private double phillipsSlope = 0.05;
        private double naturalUnemployment = 0.05;
        private double okunCoefficient = 0.5;
        private int electionSpacing = 18;
        private double campaignSpendShare = 0.0;           // PLACEHOLDER
        private double identityTolerance = 1e-9;
        private boolean simMode = false;
        private double simFixedSpending = 0.0;

        private Builder() {}

        public Builder consumeFromIncome(double v) { consumeFromIncome = v; return this; }
        public Builder consumeFromWealth(double v) { consumeFromWealth = v; return this; }
        public Builder interestRate(double v) { interestRate = v; return this; }
        public Builder allocationShare(double v) { allocationShare = v; return this; }
        public Builder allocationWeights(double equal, double population, double output) {
            allocationEqualWeight = equal;
            allocationPopulationWeight = population;
            allocationOutputWeight = output;
            return this;
        }
        public Builder resourceOutput(double v) { resourceOutput = v; return this; }
        public Builder resourcePersistence(double v) { resourcePersistence = v; return this; }
        public Builder resourceVolatility(double v) { resourceVolatility = v; return this; }
        public Builder capitalShare(double v) { capitalShare = v; return this; }
        public Builder depreciation(double v) { depreciation = v; return this; }
        public Builder productivityGrowth(double v) { productivityGrowth = v; return this; }
        public Builder populationGrowth(double v) { populationGrowth = v; return this; }
        public Builder inflationTarget(double v) { inflationTarget = v; return this; }
        public Builder expectationAnchor(double v) { expectationAnchor = v; return this; }
        public Builder phillipsSlope(double v) { phillipsSlope = v; return this; }
        public Builder naturalUnemployment(double v) { naturalUnemployment = v; return this; }
        public Builder okunCoefficient(double v) { okunCoefficient = v; return this; }
        public Builder electionSpacing(int v) { electionSpacing = v; return this; }
        public Builder campaignSpendShare(double v) { campaignSpendShare = v; return this; }
        public Builder identityTolerance(double v) { identityTolerance = v; return this; }
        public Builder simMode(boolean v) { simMode = v; return this; }
        public Builder simFixedSpending(double v) { simFixedSpending = v; return this; }

        public SimConfig build() {
            return new SimConfig(consumeFromIncome, consumeFromWealth, interestRate,
                    allocationShare, allocationEqualWeight, allocationPopulationWeight, allocationOutputWeight,
                    resourceOutput, resourcePersistence, resourceVolatility,
                    capitalShare, depreciation, productivityGrowth, populationGrowth,
                    inflationTarget, expectationAnchor, phillipsSlope, naturalUnemployment, okunCoefficient,
                    electionSpacing, campaignSpendShare,
                    identityTolerance, simMode, simFixedSpending);
        }
    }
}

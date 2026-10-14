package org.cokeke.policyengine.functions;

import org.cokeke.policyengine.models.DemandResult;
import org.cokeke.policyengine.models.PriceResult;
import org.cokeke.policyengine.models.SimConfig;
import org.cokeke.policyengine.models.StateEconomy;

/**
 * Phase 6. Reads last month's price level and potential (supply stocks are still t-1 here).
 * Returns, does not write: phase 7 still needs the old price level.
 */
public final class PricePhase {
    private PricePhase() {}

    public static PriceResult computePrices(StateEconomy s, DemandResult d, SimConfig config) {
        if (config.simMode()) {
            return new PriceResult(0, 0, s.priceLevel(), config.naturalUnemployment());
        }
        double realOutput = d.output() / s.priceLevel();
        double gap = realOutput / Indicators.potentialOutput(s, config) - 1;

        double expected = config.expectationAnchor() * config.inflationTarget()
                + (1 - config.expectationAnchor()) * s.lastInflation();
        double inflation = expected + config.phillipsSlope() * gap;
        double priceLevel = s.priceLevel() * (1 + inflation);
        // Not bounded: goes negative once gap > naturalUnemployment / okunCoefficient. See open gap 5.
        double unemployment = config.naturalUnemployment() - config.okunCoefficient() * gap;

        return new PriceResult(gap, inflation, priceLevel, unemployment);
    }
}

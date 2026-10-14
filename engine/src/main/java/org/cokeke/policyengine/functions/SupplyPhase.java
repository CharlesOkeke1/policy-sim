package org.cokeke.policyengine.functions;

import org.cokeke.policyengine.models.DemandResult;
import org.cokeke.policyengine.models.SimConfig;
import org.cokeke.policyengine.models.StateEconomy;

/** Phase 7. Public capital, productivity, population. Must run before prices are written. */
public final class SupplyPhase {
    private SupplyPhase() {}

    public static StateEconomy growSupply(StateEconomy s, DemandResult d, SimConfig config) {
        if (config.simMode()) return s;
        double realInvestment = s.investmentShare() * d.spending() / s.priceLevel(); // priceLevel is still t-1
        double capital = (1 - config.depreciation()) * s.publicCapital() + realInvestment;
        double productivity = s.productivity() * (1 + config.productivityGrowth());
        double population = s.population() * (1 + config.populationGrowth());
        return s.withSupply(population, productivity, capital);
    }
}

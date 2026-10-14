package org.cokeke.policyengine.functions;

import org.cokeke.policyengine.models.SimConfig;
import org.cokeke.policyengine.models.StateId;
import org.cokeke.policyengine.models.StateEconomy;

import java.util.Map;
import java.util.random.RandomGenerator;

/** Phase 2. Exogenous federal resource price and revenue. */
public final class ResourcePhase {
    private ResourcePhase() {}

    /** ln p(t) = persistence * ln p(t-1) + volatility * N(0,1). Mean-reverts to 1, cannot reach 0. */
    public static double stepResourcePrice(double current, RandomGenerator rng, SimConfig config) {
        double logNext = config.resourcePersistence() * Math.log(current)
                + config.resourceVolatility() * rng.nextGaussian();
        return Math.exp(logNext);
    }

    /** resourceOutput * resourcePrice * population-weighted average price level (t-1). */
    public static double resourceRevenue(double resourcePrice, Map<StateId, StateEconomy> states, SimConfig config) {
        double weightedPrice = 0;
        double population = 0;
        for (StateEconomy s : states.values()) {
            weightedPrice += s.population() * s.priceLevel();
            population += s.population();
        }
        return config.resourceOutput() * resourcePrice * (weightedPrice / population);
    }
}

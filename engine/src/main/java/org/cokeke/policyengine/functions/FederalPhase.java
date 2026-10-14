package org.cokeke.policyengine.functions;

import org.cokeke.policyengine.models.DemandResult;
import org.cokeke.policyengine.models.FederalEconomy;
import org.cokeke.policyengine.models.FederalResult;
import org.cokeke.policyengine.models.SimConfig;
import org.cokeke.policyengine.models.StateEconomy;
import org.cokeke.policyengine.models.StateId;

import java.util.Map;
import java.util.TreeMap;

/** Phase 4. The barrier: needs every state's demand before anything is allocated. */
public final class FederalPhase {
    private FederalPhase() {}

    public static FederalResult computeFederal(FederalEconomy federal,
                                               Map<StateId, DemandResult> demand,
                                               Map<StateId, StateEconomy> states,
                                               double resourceRevenue,
                                               SimConfig config) {
        double taxes = 0;
        for (DemandResult d : demand.values()) taxes += d.taxes();
        double taxCollected = federal.federalTaxShare() * taxes;

        double pool = config.allocationShare() * (resourceRevenue + taxCollected);
        Map<StateId, Double> allocations = allocate(pool, states, demand, config);

        double allocated = 0;
        for (double f : allocations.values()) allocated += f;

        FederalEconomy next = settleFederal(federal, resourceRevenue, taxCollected, allocated);
        return new FederalResult(taxCollected, pool, allocations, allocated, next);
    }

    /** Each weight term sums to 1 across states, so the allocations sum to the pool. */
    public static Map<StateId, Double> allocate(double pool,
                                                Map<StateId, StateEconomy> states,
                                                Map<StateId, DemandResult> demand,
                                                SimConfig config) {
        double totalPopulation = 0;
        double totalOutput = 0;
        for (StateEconomy s : states.values()) {
            totalPopulation += s.population();
            totalOutput += demand.get(s.id()).output();
        }
        int count = states.size();

        Map<StateId, Double> allocations = new TreeMap<>();
        for (StateEconomy s : states.values()) {
            double weight = config.allocationEqualWeight() / count
                    + config.allocationPopulationWeight() * s.population() / totalPopulation
                    + config.allocationOutputWeight() * demand.get(s.id()).output() / totalOutput;
            allocations.put(s.id(), pool * weight);
        }
        return allocations;
    }

    public static FederalEconomy settleFederal(FederalEconomy federal, double resourceRevenue,
                                               double taxCollected, double allocated) {
        return federal.withNetWorth(federal.netWorth() + resourceRevenue + taxCollected - allocated);
    }
}

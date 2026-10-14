package org.cokeke.policyengine.models;


import java.util.Map;
import java.util.Objects;

public record FederalResult(
        double taxCollected,
        double pool,
        Map<StateId, Double> allocations,
        double allocated,
        FederalEconomy next) {

    public FederalResult {
        Checks.finite("taxCollected", taxCollected);
        Checks.finite("pool", pool);
        allocations = Sorted.copy(allocations, "allocations");
        allocations.forEach((id, f) -> Checks.finite("allocation." + id, f));
        Checks.finite("allocated", allocated);
        Objects.requireNonNull(next, "next");
    }
}

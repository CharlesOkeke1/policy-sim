package org.cokeke.policyengine.functions;

import org.cokeke.policyengine.models.Policy;
import org.cokeke.policyengine.models.PriceResult;
import org.cokeke.policyengine.models.SimConfig;
import org.cokeke.policyengine.models.StateEconomy;

/** Phase 8. */
public final class StabilityPhase {
    private StabilityPhase() {}

    /**
     * INCOMPLETE BY DESIGN: only the policy's stability impact is applied. The blueprint names
     * inflation and unemployment as inputs but gives no formula (open gap 2); that equation is
     * your call. prices and config are already in the signature so adding it changes no callers.
     * The [0, 100] bound is definitional, not a bug guard.
     */
    public static double nextStability(StateEconomy s, PriceResult prices, Policy applied, SimConfig config) {
        double next = s.stability() + applied.stabilityImpact();
        return Math.max(0, Math.min(100, next));
    }
}

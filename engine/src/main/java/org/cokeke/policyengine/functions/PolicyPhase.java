package org.cokeke.policyengine.functions;

import org.cokeke.policyengine.models.Lever;
import org.cokeke.policyengine.models.LeverChange;
import org.cokeke.policyengine.models.Policy;
import org.cokeke.policyengine.models.StateEconomy;

/** Phase 1. Moves levers only. */
public final class PolicyPhase {
    private PolicyPhase() {}

    public static StateEconomy applyPolicy(StateEconomy state, Policy policy) {
        StateEconomy out = state;
        for (LeverChange change : policy.changes()) {
            out = change.lever() == Lever.DEBT ? restructure(out, change) : applyLever(out, change);
        }
        return out;
    }

    private static StateEconomy applyLever(StateEconomy s, LeverChange change) {
        Lever lever = change.lever();
        double next = lever.bound(change.op().apply(s.lever(lever), change.amount()));
        return s.withLever(lever, next);
    }

    /**
     * Households are the creditors, so they lose exactly what the government sheds.
     * Without the matching haircut the sector identity fails in phase 5.
     */
    private static StateEconomy restructure(StateEconomy s, LeverChange change) {
        double newDebt = change.op().apply(s.governmentDebt(), change.amount());
        double haircut = s.governmentDebt() - newDebt;
        return s.withAccounts(s.householdWealth() - haircut, newDebt);
    }
}

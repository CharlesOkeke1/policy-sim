package org.cokeke.policyengine.functions;

import org.cokeke.policyengine.models.DemandResult;
import org.cokeke.policyengine.models.SimConfig;
import org.cokeke.policyengine.models.FederalEconomy;
import org.cokeke.policyengine.models.SimConfig;
import org.cokeke.policyengine.models.StateEconomy;
import org.cokeke.policyengine.models.StateId;
import org.cokeke.policyengine.models.World;

import java.util.Map;

/** Phase 5. Stocks change only through flows; the sector identity is checked every tick. */
public final class AccountingPhase {
    private AccountingPhase() {}

    public static StateEconomy settleAccounts(StateEconomy s, DemandResult d, double allocation,
        double federalTaxShare) {
        double wealth = s.householdWealth() + d.disposableIncome() - d.consumption();
        double debt = s.governmentDebt() + d.spending() + d.transfers() + d.interest()
                - (1 - federalTaxShare) * d.taxes() - allocation;
        return s.withAccounts(wealth, debt);
    }

    /**
     * sum(dWealth) - sum(dDebt) + dNetWorth must equal resource revenue.
     * Compares against the world BEFORE phase 1, so a broken debt restructuring is caught too.
     */
    public static void assertSectorIdentity(World previous,
                                            Map<StateId, StateEconomy> states,
                                            FederalEconomy federal,
                                            double resourceRevenue,
                                            Map<StateId, DemandResult> demand,
                                            SimConfig config) {
        double residual = federal.netWorth() - previous.federal().netWorth() - resourceRevenue;
        double nationalOutput = 0;
        for (StateEconomy now : states.values()) {
            StateEconomy before = previous.states().get(now.id());
            residual += (now.householdWealth() - before.householdWealth())
                    - (now.governmentDebt() - before.governmentDebt());
            nationalOutput += demand.get(now.id()).output();
        }
        double limit = config.identityTolerance() * Math.abs(nationalOutput);
        if (!(Math.abs(residual) <= limit)) { // negated form also fails on NaN
            throw new IllegalStateException(
                    "Sector identity broken in month " + (previous.month() + 1)
                            + ": residual " + residual + " exceeds " + limit);
        }
    }
}

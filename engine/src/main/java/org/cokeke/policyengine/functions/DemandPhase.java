package org.cokeke.policyengine.functions;

import org.cokeke.policyengine.models.DemandResult;
import org.cokeke.policyengine.models.SimConfig;
import org.cokeke.policyengine.models.StateEconomy;

/** Phase 3. Model SIM closed form, per state, independent of every other state. */
public final class DemandPhase {
    private DemandPhase() {}

    public static DemandResult computeDemand(StateEconomy s, int month, SimConfig config) {
        double interest = config.interestRate() * s.governmentDebt();

        double spending;
        double transfers;
        if (config.simMode()) {
            spending = config.simFixedSpending();
            transfers = 0;
        } else {
            double nominalPotential = Indicators.potentialOutput(s, config) * s.priceLevel();
            double share = s.spendingShare() + (isElectionMonth(month, config) ? config.campaignSpendShare() : 0);
            spending = share * nominalPotential;
            transfers = s.transferShare() * nominalPotential;
        }

        double fromIncome = config.consumeFromIncome();
        double fromWealth = config.consumeFromWealth();
        double output = (spending + fromIncome * (interest + transfers) + fromWealth * s.householdWealth())
                / (1 - fromIncome * (1 - s.taxRate()));
        double taxes = s.taxRate() * output;
        double disposableIncome = output - taxes + interest + transfers;
        double consumption = fromIncome * disposableIncome + fromWealth * s.householdWealth();

        return new DemandResult(output, taxes, disposableIncome, consumption, spending, transfers, interest);
    }

    static boolean isElectionMonth(int month, SimConfig config) {
        return month % config.electionSpacing() == 0;
    }
}

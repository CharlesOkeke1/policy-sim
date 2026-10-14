package org.cokeke.policyengine.functions;

import org.cokeke.policyengine.models.Lever;
import org.cokeke.policyengine.models.LeverChange;
import org.cokeke.policyengine.models.Op;
import org.cokeke.policyengine.models.Policy;
import org.cokeke.policyengine.models.PolicyId;

import java.util.Collection;
import java.util.Collections;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public final class Policies {
    private Policies() {}

    private static final Map<PolicyId, Policy> CATALOG = build();
    public static Policy get(PolicyId id) {
        Policy p = CATALOG.get(id);
        if (p == null) throw new IllegalArgumentException("No policy defined for " + id);
        return p;
    }

    public static Collection<Policy> all() {
        return CATALOG.values();
    }

    private static Map<PolicyId, Policy> build() {
        EnumMap<PolicyId, Policy> m = new EnumMap<>(PolicyId.class);
        put(m, PolicyId.HOLD, "Hold", 0.0);
        put(m, PolicyId.RAISE_TAXES, "Raise taxes", -1.5, new LeverChange(Lever.TAX_RATE, Op.ADD, 0.01));
        put(m, PolicyId.CUT_TAXES, "Cut taxes", 1.0, new LeverChange(Lever.TAX_RATE, Op.ADD, -0.01));
        put(m, PolicyId.EXPAND_SPENDING, "Expand spending", 1.0, new LeverChange(Lever.SPENDING_SHARE, Op.MULTIPLY, 1.05));
        put(m, PolicyId.AUSTERITY, "Austerity", -2.0, new LeverChange(Lever.SPENDING_SHARE, Op.MULTIPLY, 0.95));
        put(m, PolicyId.INVEST_INFRASTRUCTURE, "Invest in infrastructure", 0.5, new LeverChange(Lever.INVESTMENT_SHARE, Op.ADD, 0.05));
        put(m, PolicyId.HOUSEHOLD_SUPPORT, "Household support", 1.5, new LeverChange(Lever.TRANSFER_SHARE, Op.ADD, 0.01));
        put(m, PolicyId.DEBT_RESTRUCTURING, "Debt restructuring", -3.0, new LeverChange(Lever.DEBT, Op.MULTIPLY, 0.9));
        for (PolicyId id : PolicyId.values()) {
            if (!m.containsKey(id)) throw new ExceptionInInitializerError("Policy missing from catalog: " + id);
        }
        return Collections.unmodifiableMap(m);
    }

    private static void put(Map<PolicyId, Policy> m, PolicyId id, String name, double stability, LeverChange... changes) {
        m.put(id, new Policy(id, name, List.of(changes), stability));
    }
}

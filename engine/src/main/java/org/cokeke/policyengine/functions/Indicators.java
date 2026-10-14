package org.cokeke.policyengine.functions;

import org.cokeke.policyengine.models.EventOccurrence;
import org.cokeke.policyengine.models.EventType;
import org.cokeke.policyengine.models.SimConfig;
import org.cokeke.policyengine.models.StateEconomy;
import org.cokeke.policyengine.models.StateFlows;
import org.cokeke.policyengine.models.StateId;

import java.util.List;
import java.util.Map;
import java.util.function.ToDoubleFunction;

/** Derived values. Computed on demand, never stored. */
public final class Indicators {
    private Indicators() {}

    /** productivity * publicCapital^capitalShare * population^(1 - capitalShare). Real units per month. */
    public static double potentialOutput(StateEconomy s, SimConfig config) {
        double a = config.capitalShare();
        return s.productivity() * Math.pow(s.publicCapital(), a) * Math.pow(s.population(), 1 - a);
    }

    public static double realOutput(StateFlows f) {
        return f.output() / f.startPriceLevel();
    }

    public static double realGrowth(StateFlows now, StateFlows previous) {
        return realOutput(now) / realOutput(previous) - 1;
    }

    public static double annualise(double monthlyRate) {
        return Math.pow(1 + monthlyRate, 12) - 1;
    }

    /** Annual denominator: debt stock vs one month's output would be 12x too large. */
    public static double debtToGdp(StateEconomy s, StateFlows f) {
        return s.governmentDebt() / (12 * f.output());
    }

    /** taxes kept + allocation - spending - transfers. */
    public static double primaryBalance(StateFlows f) {
        return f.taxes() - f.federalTax() + f.allocation() - f.spending() - f.transfers();
    }

    public static double fiscalBalance(StateFlows f) {
        return primaryBalance(f) - f.interest();
    }

    public static double gdpPerCapita(StateFlows f) {
        return realOutput(f) / f.population();
    }

    public static double miseryIndex(StateFlows f) {
        return f.unemployment() + annualise(f.inflation());
    }

    /** Mean of the last `months` entries (fewer if history is shorter). History is oldest first. */
    public static double rolling(List<StateFlows> history, int months, ToDoubleFunction<StateFlows> metric) {
        if (months <= 0) throw new IllegalArgumentException("months must be > 0");
        if (history.isEmpty()) throw new IllegalArgumentException("history is empty");
        int from = Math.max(0, history.size() - months);
        double sum = 0;
        for (int i = from; i < history.size(); i++) sum += metric.applyAsDouble(history.get(i));
        return sum / (history.size() - from);
    }

    public static double nationalOutput(Map<StateId, StateFlows> month) {
        double total = 0;
        for (StateFlows f : month.values()) total += f.output();
        return total;
    }

    public static long crisisCount(List<EventOccurrence> log, StateId id) {
        return log.stream()
                .filter(e -> e.type() == EventType.DEBT_CRISIS && id.equals(e.state()))
                .count();
    }
}

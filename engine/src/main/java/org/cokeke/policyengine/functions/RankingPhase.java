package org.cokeke.policyengine.functions;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.cokeke.policyengine.models.SimConfig;
import org.cokeke.policyengine.models.StateEconomy;
import org.cokeke.policyengine.models.StateFlows;
import org.cokeke.policyengine.models.StateId;

/**
 * Phase 12. Scores and ranks every state on how well it was governed this month.
 *
 * Fairness rule: a state is judged on outcomes its policy can move, never on what it was handed at
 * seeding. London's size and productivity, or a lucky seed, must not buy a rank. So every metric is
 * scale-free (a rate, a ratio or a change), and the two stock-like measures are scored as this month's
 * change rather than their level:
 *
 *   unemployment            flows.unemployment                              lower is better
 *   inflation miss          |flows.inflation - inflationTarget|             lower is better
 *   supply growth           ln(potential per head, end / start)             higher is better
 *   debt drift              debt/potential at end minus at start            lower is better
 *   stability change        stability at end minus at start                 higher is better
 *
 * Each metric is standardised across states (z-score, sign set so higher is better), the five are
 * averaged with equal weight, the composite is standardised again and mapped to 0..100 through the
 * normal CDF. Scores are therefore relative standing this month: 50 is the average state.
 *
 * Ranks use competition ranking (1, 2, 2, 4): equal scores share a rank. Iteration is in StateId order
 * and nothing is random, so a seeded run ranks identically every time.
 *
 * Pure: reads its inputs, writes only roundScore and roundRank.
 */
public final class RankingPhase {
    private RankingPhase() {}

    /** Below this the spread is treated as zero: every state scores the same on that metric. */
    private static final double EPS = 1e-12;

    private enum Metric {
        UNEMPLOYMENT(false),
        INFLATION_MISS(false),
        SUPPLY_GROWTH(true),
        DEBT_DRIFT(false),
        STABILITY_CHANGE(true);

        final boolean higherIsBetter;
        Metric(boolean higherIsBetter) { this.higherIsBetter = higherIsBetter; }
    }

    /**
     * @param start  states as they entered the month (world.states() before phase 1)
     * @param end    states after phase 11
     * @param flows  this month's flows, one per state
     * @return       end, with roundScore and roundRank set on every state
     */
    public static SortedMap<StateId, StateEconomy> rank(Map<StateId, StateEconomy> start,
                                                        Map<StateId, StateEconomy> end,
                                                        Map<StateId, StateFlows> flows,
                                                        SimConfig config) {
        if (!end.keySet().equals(flows.keySet()) || !end.keySet().equals(start.keySet())) {
            throw new IllegalArgumentException("start, end and flows must cover the same states");
        }
        SortedMap<StateId, StateEconomy> out = new TreeMap<>();
        if (end.isEmpty()) return Collections.unmodifiableSortedMap(out);

        List<StateId> ids = new ArrayList<>(new TreeMap<>(end).keySet()); // fixed order
        int n = ids.size();
        Metric[] metrics = Metric.values();

        // 1. Raw metrics, one column per metric
        double[][] raw = new double[metrics.length][n];
        for (int i = 0; i < n; i++) {
            StateId id = ids.get(i);
            StateEconomy s0 = start.get(id);
            StateEconomy s1 = end.get(id);
            StateFlows f = flows.get(id);
            double pot0 = potential(s0, config);
            double pot1 = potential(s1, config);

            raw[Metric.UNEMPLOYMENT.ordinal()][i] = f.unemployment();
            raw[Metric.INFLATION_MISS.ordinal()][i] = Math.abs(f.inflation() - config.inflationTarget());
            raw[Metric.SUPPLY_GROWTH.ordinal()][i] =
                    Math.log(pot1 / s1.population()) - Math.log(pot0 / s0.population());
            raw[Metric.DEBT_DRIFT.ordinal()][i] = s1.governmentDebt() / pot1 - s0.governmentDebt() / pot0;
            raw[Metric.STABILITY_CHANGE.ordinal()][i] = s1.stability() - s0.stability();
        }

        // 2. Standardise each metric, orient so higher is better, average with equal weight
        double[] composite = new double[n];
        for (Metric m : metrics) {
            double[] col = raw[m.ordinal()];
            replaceNonFinite(col, m.higherIsBetter);
            double[] z = zScores(col);
            for (int i = 0; i < n; i++) composite[i] += (m.higherIsBetter ? z[i] : -z[i]) / metrics.length;
        }

        // 3. Standardise the composite and map to 0..100
        double[] zc = zScores(composite);
        double[] score = new double[n];
        for (int i = 0; i < n; i++) score[i] = clamp(100 * normalCdf(zc[i]), 0, 100);

        // 4. Competition ranking: rank = 1 + number of states with a strictly higher score
        for (int i = 0; i < n; i++) {
            int rank = 1;
            for (int j = 0; j < n; j++) if (score[j] > score[i]) rank++;
            out.put(ids.get(i), end.get(ids.get(i)).withRoundAndScore(score[i], rank));
        }
        return Collections.unmodifiableSortedMap(out);
    }

    /** Same production function the seeding calibration uses: A * K^a * L^(1-a). */
    private static double potential(StateEconomy s, SimConfig c) {
        double a = c.capitalShare();
        return s.productivity() * Math.pow(s.publicCapital(), a) * Math.pow(s.population(), 1 - a);
    }

    /**
     * Zero public capital gives zero potential, so the log and the ratio go non-finite. Such a state
     * takes the worst finite value in its column instead of poisoning the mean. All non-finite: zeros.
     */
    private static void replaceNonFinite(double[] col, boolean higherIsBetter) {
        double worst = Double.NaN;
        for (double v : col) {
            if (!Double.isFinite(v)) continue;
            if (Double.isNaN(worst) || (higherIsBetter ? v < worst : v > worst)) worst = v;
        }
        double fill = Double.isNaN(worst) ? 0 : worst;
        for (int i = 0; i < col.length; i++) if (!Double.isFinite(col[i])) col[i] = fill;
    }

    /** Population z-scores. No spread (or a single state) gives all zeros, never a divide by zero. */
    private static double[] zScores(double[] x) {
        int n = x.length;
        double mean = 0;
        for (double v : x) mean += v;
        mean /= n;
        double var = 0;
        for (double v : x) var += (v - mean) * (v - mean);
        double sd = Math.sqrt(var / n);
        double[] z = new double[n];
        if (sd < EPS) return z;
        for (int i = 0; i < n; i++) z[i] = (x[i] - mean) / sd;
        return z;
    }

    /** Logistic approximation of the standard normal CDF; absolute error under 0.01. */
    private static double normalCdf(double z) {
        return 1 / (1 + Math.exp(-1.702 * z));
    }

    private static double clamp(double v, double lo, double hi) {
        return Math.max(lo, Math.min(hi, v));
    }
}
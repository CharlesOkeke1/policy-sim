package org.cokeke.policyengine.functions;

import org.cokeke.policyengine.models.RankedOutput;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public final class GameRanking {
    private GameRanking() {}

    /** How hard drops below a city's own average are punished. 0 = plain weighted mean. */
    private static final double DOWNSIDE_PENALTY = 0.5;

    /**
     * Ranks cities on their whole-game score history (oldest month first).
     * Ties break by state id, so the result is deterministic.
     */
    public static SortedMap<String, RankedOutput> rank(Map<String, ? extends List<Double>> history) {
        if (history.isEmpty()) throw new IllegalArgumentException("history is empty");

        List<Map.Entry<String, Double>> scored = new ArrayList<>();
        for (Map.Entry<String, ? extends List<Double>> e : history.entrySet()) {
            scored.add(Map.entry(e.getKey(), aggregate(e.getKey(), e.getValue())));
        }

        scored.sort(Comparator
                .comparing(Map.Entry<String, Double>::getValue, Comparator.reverseOrder())
                .thenComparing(Map.Entry::getKey));

        SortedMap<String, RankedOutput> out = new TreeMap<>();
        for (int i = 0; i < scored.size(); i++) {
            Map.Entry<String, Double> e = scored.get(i);
            out.put(e.getKey(), new RankedOutput(i + 1, e.getValue()));
        }
        return Collections.unmodifiableSortedMap(out);
    }

        /**
     * Ranks every city on its whole-game score history (oldest month first)
     * and returns the result for one of them. Ties break by state id.
     */
    public static RankedOutput rank(Map<String, ? extends List<Double>> history, String stateId) {
        if (history.isEmpty()) throw new IllegalArgumentException("history is empty");
        if (!history.containsKey(stateId)) throw new IllegalArgumentException("Unknown state: " + stateId);

        List<Map.Entry<String, Double>> scored = new ArrayList<>();
        for (Map.Entry<String, ? extends List<Double>> e : history.entrySet()) {
            scored.add(Map.entry(e.getKey(), aggregate(e.getKey(), e.getValue())));
        }

        scored.sort(Comparator
                .comparing(Map.Entry<String, Double>::getValue, Comparator.reverseOrder())
                .thenComparing(Map.Entry::getKey));

        for (int i = 0; i < scored.size(); i++) {
            Map.Entry<String, Double> e = scored.get(i);
            if (e.getKey().equals(stateId)) return new RankedOutput(i + 1, e.getValue());
        }
        throw new IllegalStateException("unreachable: " + stateId + " was checked above");
    }

    /** Weighted mean (weights 1 -> 2, first month to last) minus a penalty for months below that mean. */
    static double aggregate(String stateId, List<Double> scores) {
        int n = scores.size();
        if (n == 0) throw new IllegalArgumentException(stateId + " has no scores");

        double weightSum = 0, weighted = 0;
        for (int i = 0; i < n; i++) {
            double s = scores.get(i);
            if (!Double.isFinite(s)) throw new IllegalArgumentException(stateId + " score " + i + " is not finite: " + s);
            double w = weight(i, n);
            weightSum += w;
            weighted += w * s;
        }
        double mean = weighted / weightSum;

        double downside = 0;
        for (int i = 0; i < n; i++) {
            double shortfall = Math.min(0, scores.get(i) - mean);
            downside += weight(i, n) * shortfall * shortfall;
        }
        double downsideDeviation = Math.sqrt(downside / weightSum);

        return mean - DOWNSIDE_PENALTY * downsideDeviation;
    }

    private static double weight(int i, int n) {
        return n == 1 ? 1.0 : 1.0 + (double) i / (n - 1);
    }
}
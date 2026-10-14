package org.cokeke.policyengine.models;


import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Immutable, key-ordered map copies.
 *
 * Why not Map.copyOf: its iteration order is randomised per JVM run. Floating-point sums
 * depend on order, so a replayed save game in a new process could drift. A TreeMap keyed
 * by StateId iterates the same way everywhere.
 */
public final class Sorted {
    private Sorted() {}

    public static <K extends Comparable<K>, V> SortedMap<K, V> copy(Map<K, V> source, String name) {
        Objects.requireNonNull(source, name);
        TreeMap<K, V> copy = new TreeMap<>();
        source.forEach((k, v) -> copy.put(
                Objects.requireNonNull(k, name + " key"),
                Objects.requireNonNull(v, name + " value for " + k)));
        return Collections.unmodifiableSortedMap(copy);
    }
}

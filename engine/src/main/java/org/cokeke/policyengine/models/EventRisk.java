package org.cokeke.policyengine.models;


import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.Objects;

/** Per-state event risk. Every EventType always has an entry. */
public record EventRisk(Map<EventType, Double> risk, Map<EventType, Integer> cooldownLeft) {
    public EventRisk {
        risk = complete(risk, "risk");
        cooldownLeft = complete(cooldownLeft, "cooldownLeft");
        risk.forEach((t, v) -> Checks.finite("risk." + t, v));
        cooldownLeft.forEach((t, v) -> { if (v < 0) throw new IllegalStateException("cooldown." + t + " < 0"); });
    }

    public static EventRisk none() {
        EnumMap<EventType, Double> r = new EnumMap<>(EventType.class);
        EnumMap<EventType, Integer> c = new EnumMap<>(EventType.class);
        for (EventType t : EventType.values()) { r.put(t, 0.0); c.put(t, 0); }
        return new EventRisk(r, c);
    }

    // new EnumMap<>(Map) throws on an empty non-EnumMap, so copy by hand.
    private static <V> Map<EventType, V> complete(Map<EventType, V> source, String name) {
        Objects.requireNonNull(source, name);
        EnumMap<EventType, V> copy = new EnumMap<>(EventType.class);
        for (EventType t : EventType.values()) {
            copy.put(t, Objects.requireNonNull(source.get(t), name + " missing " + t));
        }
        return Collections.unmodifiableMap(copy);
    }
}

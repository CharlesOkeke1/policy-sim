package org.cokeke.policyengine.models;


import java.util.Map;
import java.util.Objects;

/**
 * The single value the engine takes and returns. month = last completed month (0 after seeding).
 * baseline holds the seeded levers that a lost election reverts towards.
 */
public record World(
        int month,
        long seed,
        Map<StateId, StateEconomy> states,
        FederalEconomy federal,
        Map<StateId, EventRisk> eventRisk,
        Map<StateId, StateEconomy> baseline) {

    public World {
        if (month < 0) throw new IllegalArgumentException("month < 0");
        states = Sorted.copy(states, "states");
        Objects.requireNonNull(federal, "federal");
        eventRisk = Sorted.copy(eventRisk, "eventRisk");
        baseline = Sorted.copy(baseline, "baseline");
        if (states.isEmpty()) throw new IllegalArgumentException("World needs at least one state");
        states.forEach((id, s) -> {
            if (!id.equals(s.id())) throw new IllegalArgumentException("Key " + id + " holds state " + s.id());
        });
        if (!eventRisk.keySet().equals(states.keySet()))
            throw new IllegalArgumentException("eventRisk keys must match states");
        if (!baseline.keySet().equals(states.keySet()))
            throw new IllegalArgumentException("baseline keys must match states");
    }
}

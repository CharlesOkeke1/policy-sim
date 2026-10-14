package org.cokeke.policyengine.models;


import java.util.Objects;

/** state is null for national events; use isNational() rather than comparing to null. */
public record EventOccurrence(int month, StateId state, EventType type, double severity) {
    public EventOccurrence {
        Objects.requireNonNull(type, "type");
        Checks.finite("severity", severity);
    }

    public boolean isNational() {
        return state == null;
    }
}

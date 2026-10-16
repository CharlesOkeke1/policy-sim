package org.cokeke.policyengine.models;
import java.util.Objects;

/** Comparable so every state map iterates in a fixed order and ranking ties break deterministically. */
public record StateId(String value) implements Comparable<StateId> {
    public StateId {
        Objects.requireNonNull(value, "value");
        if (value.isBlank()) throw new IllegalArgumentException("StateId must not be blank");
    }

    @Override
    public int compareTo(StateId other) {
        return value.compareTo(other.value);
    }

    @Override
    public String toString() {
        return value;
    }
}

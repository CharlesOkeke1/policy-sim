package org.cokeke.policyengine.models;


import java.util.Objects;

public record ElectionResult(StateId state, int month, double approval, boolean incumbentWon) {
    public ElectionResult {
        Objects.requireNonNull(state, "state");
        Checks.inRange("approval", approval, 0, 1);
    }
}

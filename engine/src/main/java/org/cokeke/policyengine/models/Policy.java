package org.cokeke.policyengine.models;

import java.util.List;
import java.util.Objects;

/** A policy is data: lever changes plus a stability impact. It never names an outcome. */
public record Policy(PolicyId id, String name, List<LeverChange> changes, double stabilityImpact) {
    public Policy {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(name, "name");
        changes = List.copyOf(changes);
        Checks.finite("stabilityImpact", stabilityImpact);
    }
}

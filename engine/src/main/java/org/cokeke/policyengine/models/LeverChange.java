package org.cokeke.policyengine.models;


import java.util.Objects;

public record LeverChange(Lever lever, Op op, double amount) {
    public LeverChange {
        Objects.requireNonNull(lever, "lever");
        Objects.requireNonNull(op, "op");
        Checks.finite("amount", amount);
    }
}

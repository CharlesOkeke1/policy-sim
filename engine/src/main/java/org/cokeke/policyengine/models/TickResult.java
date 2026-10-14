package org.cokeke.policyengine.models;


import java.util.Objects;

public record TickResult(World next, MonthReport report) {
    public TickResult {
        Objects.requireNonNull(next, "next");
        Objects.requireNonNull(report, "report");
    }
}

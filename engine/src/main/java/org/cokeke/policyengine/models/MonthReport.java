package org.cokeke.policyengine.models;


import java.util.List;
import java.util.Map;
import java.util.Objects;

public record MonthReport(
        int month,
        Map<StateId, StateFlows> states,
        FederalFlows federal,
        List<EventOccurrence> events,
        List<ElectionResult> elections) {

    public MonthReport {
        states = Sorted.copy(states, "states");
        Objects.requireNonNull(federal, "federal");
        events = List.copyOf(events);
        elections = List.copyOf(elections);
    }
}

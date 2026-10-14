package org.cokeke.policyengine.models;
import java.util.Objects;

public record StateFlows(
        StateId id,
        int month,
        double startPriceLevel,
        double population,
        double output,
        double taxes,
        double federalTax,
        double disposableIncome,
        double consumption,
        double spending,
        double transfers,
        double interest,
        double allocation,
        double outputGap,
        double inflation,
        double unemployment,
        PolicyId policy) {

    public StateFlows {
        Objects.requireNonNull(id, "id");
        Objects.requireNonNull(policy, "policy");
        Checks.positive("startPriceLevel", startPriceLevel);
        Checks.positive("population", population);
        Checks.finite("output", output);
        Checks.finite("taxes", taxes);
        Checks.finite("federalTax", federalTax);
        Checks.finite("disposableIncome", disposableIncome);
        Checks.finite("consumption", consumption);
        Checks.finite("spending", spending);
        Checks.finite("transfers", transfers);
        Checks.finite("interest", interest);
        Checks.finite("allocation", allocation);
        Checks.finite("outputGap", outputGap);
        Checks.finite("inflation", inflation);
        Checks.finite("unemployment", unemployment);
    }
}

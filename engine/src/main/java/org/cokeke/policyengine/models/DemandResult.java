package org.cokeke.policyengine.models;

public record DemandResult(
        double output,
        double taxes,
        double disposableIncome,
        double consumption,
        double spending,
        double transfers,
        double interest) {

    public DemandResult {
        Checks.finite("output", output);
        Checks.finite("taxes", taxes);
        Checks.finite("disposableIncome", disposableIncome);
        Checks.finite("consumption", consumption);
        Checks.finite("spending", spending);
        Checks.finite("transfers", transfers);
        Checks.finite("interest", interest);
    }
}

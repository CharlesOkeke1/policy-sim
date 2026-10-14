package org.cokeke.policyengine.models;


public record PriceResult(double outputGap, double inflation, double priceLevel, double unemployment) {
    public PriceResult {
        Checks.finite("outputGap", outputGap);
        Checks.finite("inflation", inflation);
        Checks.positive("priceLevel", priceLevel);
        Checks.finite("unemployment", unemployment);
    }
}

package org.cokeke.policyengine.models;

public enum Lever {
    TAX_RATE(0.0, 0.6),
    SPENDING_SHARE(0.0, 0.5),
    INVESTMENT_SHARE(0.0, 1.0),
    TRANSFER_SHARE(0.0, 0.2),
    DEBT(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);

    private final double min;
    private final double max;

    Lever(double min, double max) {
        this.min = min;
        this.max = max;
    }

    public double getMin() { return min; }
    public double getMax() { return max; }

    public double bound(double value) {
        return Math.max(min, Math.min(max, value));
    }
}

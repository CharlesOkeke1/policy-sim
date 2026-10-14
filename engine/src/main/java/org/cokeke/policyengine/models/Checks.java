package org.cokeke.policyengine.models;

public final class Checks {
    private Checks() {}

    public static double finite(String name, double value) {
        if (!Double.isFinite(value)) {
            throw new IllegalStateException(name + " is not finite: " + value);
        }
        return value;
    }

    public static double positive(String name, double value) {
        finite(name, value);
        if (value <= 0) throw new IllegalStateException(name + " must be > 0, was " + value);
        return value;
    }

    public static double nonNegative(String name, double value) {
        finite(name, value);
        if (value < 0) throw new IllegalStateException(name + " must be >= 0, was " + value);
        return value;
    }

    public static double inRange(String name, double value, double min, double max) {
        finite(name, value);
        if (value < min || value > max) {
            throw new IllegalStateException(name + " = " + value + " is outside [" + min + ", " + max + "]");
        }
        return value;
    }
}

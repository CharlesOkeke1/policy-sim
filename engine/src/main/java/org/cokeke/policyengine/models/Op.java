package org.cokeke.policyengine.models;


public enum Op {
    ADD,
    MULTIPLY;

    public double apply(double current, double amount) {
        return switch (this) {
            case ADD -> current + amount;
            case MULTIPLY -> current * amount;
        };
    }
}

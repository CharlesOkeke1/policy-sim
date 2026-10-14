package org.cokeke.policyengine.models;


public record FederalFlows(int month, double resourceRevenue, double taxCollected, double pool, double allocated) {
    public FederalFlows {
        Checks.finite("resourceRevenue", resourceRevenue);
        Checks.finite("taxCollected", taxCollected);
        Checks.finite("pool", pool);
        Checks.finite("allocated", allocated);
    }
}

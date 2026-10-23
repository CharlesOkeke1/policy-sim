package org.cokeke.policysim.dtos;

public record Policy(String name, String policyId, double effectOnStability, 
    double policySpecificEffect, String affectedLever) {}
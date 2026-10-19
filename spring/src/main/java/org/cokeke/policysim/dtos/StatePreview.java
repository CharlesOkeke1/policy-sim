package org.cokeke.policysim.dtos;

public record StatePreview(
    String id,
    String name,
    double population,
    double productivity,
    double stability,
    String luck,
    String initials
) {}
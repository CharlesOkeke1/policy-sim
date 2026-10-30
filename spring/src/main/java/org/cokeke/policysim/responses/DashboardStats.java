package org.cokeke.policysim.responses;

import java.util.Map;

public record DashboardStats(
    Long userId,
    double avgScore,
    int finishedFirst,
    int monthsGoverned,
    Map<Integer, Integer> positions,
    int completed,
    int abandoned,
    int inProgress
) {}

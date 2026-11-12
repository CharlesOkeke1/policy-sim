package org.cokeke.policysim.responses;

import java.util.List;
import org.cokeke.policysim.dtos.CenterChartStat;

public record DashboardStats2(
    //List<CenterChartStat> stats,
    double unemployment,
    double inflation,
    double debt,
    double output,
    double avgFinalRank,
    double realGrowth,
    double stability,
    double householdWealth
) {}
package org.cokeke.policyengine.functions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.cokeke.policyengine.models.DemandResult;
import org.cokeke.policyengine.models.FederalEconomy;
import org.cokeke.policyengine.models.FederalFlows;
import org.cokeke.policyengine.models.FederalResult;
import org.cokeke.policyengine.models.GlobalGameState;
import org.cokeke.policyengine.models.MonthReport;
import org.cokeke.policyengine.models.Policy;
import org.cokeke.policyengine.models.PolicyId;
import org.cokeke.policyengine.models.PriceResult;
import org.cokeke.policyengine.models.SimConfig;
import org.cokeke.policyengine.models.SpringRoundDTO;
import org.cokeke.policyengine.models.StateEconomy;
import org.cokeke.policyengine.models.StateFlows;
import org.cokeke.policyengine.models.StateId;
import org.cokeke.policyengine.models.Stream;
import org.cokeke.policyengine.models.TickResult;
import org.cokeke.policyengine.models.World;

/**
 * One month = one call. Phases run in fixed order; each writes only its own outputs.
 *
 * Not yet wired (build order step 9): AI (states without an action Hold), events (phase 9)
 * and elections (phase 10). eventRisk and baseline are carried forward unchanged.
 */
public final class Engine {
    private  GlobalGameState currentState;
    public Engine(int seed) {
        this.currentState = initializeSession(seed);
    }

    public GlobalGameState getGameState() { return this.currentState; }

    public GlobalGameState initializeSession(int seed) {
        Map<StateId, StateEconomy> states = SeedingEngine.seedStates(seed);
        FederalEconomy fed = SeedingEngine.seedFederal(seed);
        Map<StateId, StateEconomy> baseline = SeedingEngine.baseline();
        World world = new World(0, seed, states, fed, baseline);
        
        SimConfig simConfig = SimConfig.defaults();
        GlobalGameState initConfig = new GlobalGameState(world, simConfig);

        return initConfig;
    }

    public List<SpringRoundDTO> getRound() {
        TickResult result = tick(currentState.getWorld(), SeedingEngine.randomPolicies(currentState.getWorld().seed()),
        currentState.getConfig());

        Map<StateId, StateEconomy> economies = result.getWorld().states();
        FederalEconomy fed = result.getWorld().federal();
        int currentMonth = result.getWorld().getMonth();
        Map<StateId, StateFlows> flows = result.getMonthReport().states();

        List<SpringRoundDTO> roundOut = new ArrayList<>();

        this.currentState.setWorld(result.getWorld());

        for (StateId id : flows.keySet()) {
            StateFlows flow = flows.get(id);
            StateEconomy economy = economies.get(id);
            SpringRoundDTO out = new SpringRoundDTO(currentMonth, id.toString(), economy.name(), economy.population(), 
            economy.productivity(), economy.publicCapital(), economy.priceLevel(), economy.lastInflation(),
            economy.householdWealth(), economy.governmentDebt(), economy.stability(), economy.taxRate(),
            economy.spendingShare(), economy.investmentShare(), economy.transferShare(), flow.output(), 
            flow.outputGap(), flow.unemployment(), flow.federalTax(), flow.allocation(), flow.spending(),
            flow.transfers(), flow.interest(), flow.policy().toString(), fed.resourcePrice(), fed.netWorth(), fed.federalTaxShare());

            roundOut.add(out);
        }

        return roundOut;        
    }

    public double getProgress(int total) {
        int currentMonth = this.currentState.getWorld().getMonth();
        return (double) (currentMonth * 100 / total);
    }

    public static TickResult tick(World world, Map<StateId, PolicyId> actions, SimConfig config) {
        for (StateId id : actions.keySet()) {
            if (!world.states().containsKey(id)) throw new IllegalArgumentException("Unknown state in actions: " + id);
        }
        int month = world.month() + 1;
        Map<StateId, StateEconomy> start = world.states();

        // 1. Policy
        Map<StateId, Policy> applied = new TreeMap<>();
        Map<StateId, StateEconomy> states = new TreeMap<>();
        for (StateEconomy s : start.values()) {
            
            Policy policy = Policies.get(actions.getOrDefault(s.id(), PolicyId.HOLD));
            applied.put(s.id(), policy);
            states.put(s.id(), PolicyPhase.applyPolicy(s, policy));
        }

        // 2. Exogenous
        double resourcePrice = ResourcePhase.stepResourcePrice(
                world.federal().resourcePrice(), Rng.rng(world.seed(), month, Stream.RESOURCE), config);
        FederalEconomy federal = world.federal().withResourcePrice(resourcePrice);
        double resourceRevenue = ResourcePhase.resourceRevenue(resourcePrice, states, config);

        // 3. Demand (independent per state)
        Map<StateId, DemandResult> demand = new TreeMap<>();
        for (StateEconomy s : states.values()) demand.put(s.id(), DemandPhase.computeDemand(s, month, config));

        // 4. Federal (barrier)
        FederalResult fed = FederalPhase.computeFederal(federal, demand, states, resourceRevenue, config);

        // 5. Accounting
        for (StateEconomy s : List.copyOf(states.values())) {
            states.put(s.id(), AccountingPhase.settleAccounts(
                    s, demand.get(s.id()), fed.allocations().get(s.id()), federal.federalTaxShare()));
        }
        AccountingPhase.assertSectorIdentity(world, states, fed.next(), resourceRevenue, demand, config);

        // 6. Prices and labour (computed only)
        Map<StateId, PriceResult> prices = new TreeMap<>();
        for (StateEconomy s : states.values()) prices.put(s.id(), PricePhase.computePrices(s, demand.get(s.id()), config));

        // 7. Supply (reads last month's price level), then write prices
        for (StateEconomy s : List.copyOf(states.values())) {
            PriceResult p = prices.get(s.id());
            states.put(s.id(), SupplyPhase.growSupply(s, demand.get(s.id()), config)
                    .withPrices(p.priceLevel(), p.inflation()));
        }

        // 8. Stability
        for (StateEconomy s : List.copyOf(states.values())) {
            states.put(s.id(), s.withStability(
                    StabilityPhase.nextStability(s, prices.get(s.id()), applied.get(s.id()), config)));
        }

        // 9. Events, 10. Elections: not yet implemented.

        // 11. Record
        for (StateEconomy s : List.copyOf(states.values())) {
            states.put(s.id(), s.withCurrentMonth(month));
        }
        MonthReport report = buildReport(month, start, demand, fed, prices, applied, resourceRevenue);
        World next = new World(month, world.seed(), states, fed.next(), world.baseline());
        return new TickResult(next, report);
    }

    private static MonthReport buildReport(int month, Map<StateId, StateEconomy> start,
    Map<StateId, DemandResult> demand, FederalResult fed, Map<StateId, PriceResult> prices,
    Map<StateId, Policy> applied, double resourceRevenue) {
        double federalTaxShare = fed.next().federalTaxShare();
        Map<StateId, StateFlows> flows = new TreeMap<>();
        for (StateEconomy s : start.values()) {
            DemandResult d = demand.get(s.id());
            PriceResult p = prices.get(s.id());
            flows.put(s.id(), new StateFlows(
                    s.id(), month, s.priceLevel(), s.population(),
                    d.output(), d.taxes(), federalTaxShare * d.taxes(),
                    d.disposableIncome(), d.consumption(), d.spending(), d.transfers(), d.interest(),
                    fed.allocations().get(s.id()),
                    p.outputGap(), p.inflation(), p.unemployment(),
                    applied.get(s.id()).id()));
        }
        FederalFlows federal = new FederalFlows(month, resourceRevenue, fed.taxCollected(), fed.pool(), fed.allocated());
        return new MonthReport(month, flows, federal, List.of(), List.of());
    }
}

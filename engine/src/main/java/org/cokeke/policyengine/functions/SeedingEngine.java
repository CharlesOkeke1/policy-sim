package org.cokeke.policyengine.functions;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.SplittableRandom;
import java.util.TreeMap;
import java.util.random.RandomGenerator;

import org.cokeke.policyengine.models.FederalEconomy;
import org.cokeke.policyengine.models.PolicyId;
import org.cokeke.policyengine.models.SimConfig;
import org.cokeke.policyengine.models.StateEconomy;
import org.cokeke.policyengine.models.StateId;
import org.cokeke.policyengine.models.Stream;
import org.cokeke.policyengine.models.World;

/**
 * Builds month 0. Follows the blueprint's seeding steps: draw levers, set spending so demand starts at
 * potential, put public capital and household wealth at their steady states, draw debt as a share of
 * annual output, then burn in on HOLD so every stock is consistent with the model before month 1.
 */
public final class SeedingEngine {
    private SeedingEngine() {}

    private static final double NOISE = 0.10;            // ±10% on population and productivity
    private static final double BASE_STABILITY = 60;     // noise keeps it in 54–66
    private static final int BURN_IN_MONTHS = 120;

    // Blueprint lever ranges (uniform draws)
    private static final double TAX_MIN = 0.15, TAX_MAX = 0.30;
    private static final double INVEST_MIN = 0.10, INVEST_MAX = 0.30;
    private static final double TRANSFER_MIN = 0.0, TRANSFER_MAX = 0.03;

    // Noise-free values used by baseline(): the middle of each range
    private static final double TAX_MID = (TAX_MIN + TAX_MAX) / 2;
    private static final double INVEST_MID = (INVEST_MIN + INVEST_MAX) / 2;
    private static final double TRANSFER_MID = (TRANSFER_MIN + TRANSFER_MAX) / 2;

    /** Population in millions; productivity is an index. Everything else is derived. */
    private record Profile(String id, String name, double population, double productivity) {}

    private static final List<Profile> PROFILES = List.of(
            new Profile("birmingham",    "Birmingham",    1.15, 1.00),
            new Profile("coventry",      "Coventry",      0.35, 0.95),
            new Profile("edinburgh",     "Edinburgh",     0.51, 1.15),
            new Profile("glasgow",       "Glasgow",       0.63, 0.95),
            new Profile("london",        "London",        8.90, 1.30),
            new Profile("manchester",    "Manchester",    0.55, 1.05),
            new Profile("milton-keynes", "Milton Keynes", 0.29, 1.05),
            new Profile("plymouth",      "Plymouth",      0.26, 0.90)
    );

    private static final double FEDERAL_TAX_SHARE = 0.10; // PLACEHOLDER; must be 0 for SIM-mode golden tests

    /** The noise-free world at mid-range levers, no burn-in. What a lost election reverts towards. */
    public static SortedMap<StateId, StateEconomy> baseline() {
        return baseline(SimConfig.defaults());
    }

    public static SortedMap<StateId, StateEconomy> baseline(SimConfig config) {
        SortedMap<StateId, StateEconomy> states = new TreeMap<>();
        for (Profile p : PROFILES) {
            StateEconomy e = calibrate(p, 1.0, 1.0, TAX_MID, INVEST_MID, TRANSFER_MID, BASE_STABILITY, config);
            states.put(e.id(), e);
        }
        return Collections.unmodifiableSortedMap(states);
    }

    public static SortedMap<StateId, StateEconomy> seedStates(int seed) {
        return seedStates(seed, SimConfig.defaults());
    }

    /** Pass the same config the game will run with: calibration and burn-in depend on it. */
    public static SortedMap<StateId, StateEconomy> seedStates(int seed, SimConfig config) {
        SortedMap<StateId, StateEconomy> states = new TreeMap<>();
        for (Profile p : PROFILES) {
            SplittableRandom rng = new SplittableRandom(streamSeed(seed, p.id().hashCode()));
            // Draw order is part of the seed contract: reordering these lines changes every world.
            double populationF   = noise(rng);
            double productivityF = noise(rng);
            double taxRate       = rng.nextDouble(TAX_MIN, TAX_MAX);
            double investment    = rng.nextDouble(INVEST_MIN, INVEST_MAX);
            double transfers     = rng.nextDouble(TRANSFER_MIN, TRANSFER_MAX);
            double stability     = BASE_STABILITY * noise(rng);
            StateEconomy e = calibrate(p, populationF, productivityF, taxRate, investment, transfers,
                    stability, config);
            states.put(e.id(), e);
        }
        return burnIn(seed, states, config);
    }

    public static FederalEconomy seedFederal(int seed) {
        SplittableRandom rng = new SplittableRandom(streamSeed(seed, "federal".hashCode()));
        return new FederalEconomy(
                1.0 * noise(rng), // resourcePrice
                0.0,              // netWorth starts at zero
                FEDERAL_TAX_SHARE);
    }

    /**
     * Places a state on its balanced-growth path at price level 1, output at potential and inflation at
     * target. Every stock then grows at the nominal rate g and stays in proportion to output. Per unit of
     * potential output (d = D / potential, s_G = actual spending / potential):
     *
     *   g_pot = g_A / (1 - a) + n                         real growth of potential when K grows with it
     *   g     = (1 + pi*) (1 + g_pot) - 1                 nominal growth
     *   H = D                                             household wealth is the state's debt, held by its households
     *   d     = (1 - a1)(1 - theta + psi) / (a2 + g - (1 - a1) r)   wealth that households want at that income
     *   s_G   = theta - psi - (r - g) d                   deficit exactly finances debt growth g D
     *   gamma = s_G + debtFeedback * d                    the lever that produces s_G under the fiscal rule
     *   K*    = (s_I s_G A / (delta + g_pot))^(1/(1-a)) L   investment covers depreciation plus growth
     *
     * Debt is derived, not drawn: the model decides how much debt is consistent with each state's levers.
     * Federal allocations do not exactly return each state's federal tax, so the burn-in absorbs the rest.
     */
    private static StateEconomy calibrate(Profile p, double populationF, double productivityF,
                                          double taxRate, double investment, double transfers,
                                          double stability, SimConfig c) {
        double population = p.population() * populationF;
        double productivity = p.productivity() * productivityF;
        double a = c.capitalShare();
        double r = c.interestRate();

        double realGrowth = c.productivityGrowth() / (1 - a) + c.populationGrowth();
        double g = (1 + c.inflationTarget()) * (1 + realGrowth) - 1;

        double a1 = c.consumeFromIncome();
        double d = (1 - a1) * (1 - taxRate + transfers) / (c.consumeFromWealth() + g - (1 - a1) * r);
        double actualShare = taxRate - transfers - (r - g) * d;
        double spendingShare = actualShare + c.debtFeedback() * d;

        double capital = Math.pow(investment * actualShare * productivity / (c.depreciation() + realGrowth), 1 / (1 - a))
                * population;
        double potential = productivity * Math.pow(capital, a) * Math.pow(population, 1 - a);
        double debt = d * potential;

        StateId id = new StateId(p.id());
        return new StateEconomy(
                id, p.name(), 0,
                population, productivity, capital,
                1.0, c.inflationTarget(),       // priceLevel, lastInflation
                debt, debt,                     // householdWealth = governmentDebt
                stability,
                taxRate, spendingShare, investment, transfers, 0, 0);
    }

    /** Runs the real engine on HOLD so stocks settle; month and history are discarded. */
    private static SortedMap<StateId, StateEconomy> burnIn(int seed, Map<StateId, StateEconomy> states, SimConfig config) {
        World world = new World(0, seed, states, seedFederal(seed), states);
        Map<StateId, PolicyId> hold = initialPolicies();
        for (int m = 0; m < BURN_IN_MONTHS; m++) {
            world = Engine.tick(world, hold, config).next();
        }
        SortedMap<StateId, StateEconomy> settled = new TreeMap<>();
        for (StateEconomy s : world.states().values()) {
            // Keep the settled stocks; restore the seeded stability and clear the burn-in rank
            // (burn-in is not played history)
            settled.put(s.id(), s.withStability(states.get(s.id()).stability()).withCurrentMonth(0)
                    .withRoundAndScore(0, 0));
        }
        return Collections.unmodifiableSortedMap(settled);
    }

    /** Independent stream per (seed, purpose). String.hashCode is specified, so this is stable across JVMs. */
    private static long streamSeed(int seed, int salt) {
        return ((long) seed << 32) ^ (salt & 0xFFFFFFFFL);
    }

    private static double noise(SplittableRandom rng) {
        return rng.nextDouble(1 - NOISE, 1 + NOISE);
    }

    public static SortedMap<StateId, PolicyId> initialPolicies() {
        SortedMap<StateId, PolicyId> policies = new TreeMap<>();
        for (Profile p : PROFILES) {
            policies.put(new StateId(p.id()), PolicyId.HOLD);
        }
        return Collections.unmodifiableSortedMap(policies);
    }

    /** Same seed every call: every state repeats one policy forever. Kept for server testing only. */
    public static SortedMap<StateId, PolicyId> randomPolicies(int seed) {
        PolicyId[] options = PolicyId.values();
        SortedMap<StateId, PolicyId> policies = new TreeMap<>();
        for (Profile p : PROFILES) {
            SplittableRandom rng = new SplittableRandom(streamSeed(seed, (p.id() + ":policy").hashCode()));
            policies.put(new StateId(p.id()), options[rng.nextInt(options.length)]);
        }
        return Collections.unmodifiableSortedMap(policies);
    }

    /** A fresh random policy per state for the month about to be played, chosen only from legal ones. */
    public static SortedMap<StateId, PolicyId> randomPolicies(World world, SimConfig config) {
        RandomGenerator rng = Rng.rng(world.seed(), world.month() + 1, Stream.AI);
        SortedMap<StateId, PolicyId> policies = new TreeMap<>();
        for (StateEconomy s : world.states().values()) { // sorted by StateId, so draws are deterministic
            List<PolicyId> options = Policies.available(s, config);
            policies.put(s.id(), options.get(rng.nextInt(options.size())));
        }
        return Collections.unmodifiableSortedMap(policies);
    }

    /** A fresh random policy per state each month. Pass the month being played (world.month() + 1). */
    public static SortedMap<StateId, PolicyId> randomPolicies(int seed, int month) {
        PolicyId[] options = PolicyId.values();
        RandomGenerator rng = Rng.rng(seed, month, Stream.AI);
        SortedMap<StateId, PolicyId> policies = new TreeMap<>();
        for (Profile p : PROFILES) { // PROFILES order is fixed, so draws are deterministic
            policies.put(new StateId(p.id()), options[rng.nextInt(options.length)]);
        }
        return Collections.unmodifiableSortedMap(policies);
    }
}
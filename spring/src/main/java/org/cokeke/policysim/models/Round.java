package org.cokeke.policysim.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "rounds")
public class Round {
        // ---------------------------------------------------------------
    // TODO: fields the UI needs. Uncomment as each screen gets wired.
    // ---------------------------------------------------------------

    // Ranking (game top bar, Compare States, My Games, Dashboard)
    // @Column(nullable = false)
    // private double score;            // from the engine's scoring, 0-100
    //
    // @Column(nullable = false)
    // private int rank;                // 1-8 among the cities in this game and month

    // Flows for the month (from StateFlows); needed to derive the indicators
    // @Column(nullable = false)
    // private double output;           // nominal output -> real growth and debt / GDP
    //
    // @Column(nullable = false)
    // private double outputGap;
    //
    // @Column(nullable = false)
    // private double unemployment;     // only exists in PriceResult, not StateEconomy
    //
    // @Column(nullable = false)
    // private double taxes;
    //
    // @Column(nullable = false)
    // private double federalTax;       // "You paid in" on the federal panel
    //
    // @Column(nullable = false)
    // private double allocation;       // "You received" on the federal panel
    //
    // @Column(nullable = false)
    // private double spending;
    //
    // @Column(nullable = false)
    // private double transfers;
    //
    // @Column(nullable = false)
    // private double interest;

    // What the city did this month ("Last policy" column, month report)
    // @Column(nullable = false, length = 32)
    // private String policy;           // the PolicyId name, e.g. "EXPAND_SPENDING"
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private int currentMonth;

    @Column(nullable = false)
    private String stateId;

    @Column(nullable = false)
    private String stateName;

    @Column(nullable = false)
    private double population;

    @Column(nullable = false)
    private double productivity;

    @Column(nullable = false)
    private double publicCapital;

    @Column(nullable = false)
    private double priceLevel;

    @Column(nullable = false)
    private double lastInflation;

    @Column(nullable = false)
    private double householdWealth;

    @Column(nullable = false)
    private double governmentDebt;

    @Column(nullable = false)
    private double stability;

    @Column(nullable = false)
    private double taxRate;

    @Column(nullable = false)
    private double spendingShare;

    @Column(nullable = false)
    private double investmentShare;

    @Column(nullable = false)
    private double transferShare;

    @ManyToOne
    @JoinColumn(name = "game_id", nullable = false)
    private Game game;

    public Long getId() { return this.id; }
    public int getCurrentMonth() { return this.currentMonth; }
    public String getStateId() { return this.stateId; }
    public String getStateName() { return this.stateName; }
    public double getPopulation() { return this.population; }
    public double getProductivity() { return this.productivity; }
    public double getPublicCapital() { return this.publicCapital; } 
    public double getPriceLevel() { return this.priceLevel; }
    public double getLastInflation() { return this.lastInflation; }
    public double getHouseholdWealth() { return this.householdWealth; }
    public double getGovernmentDebt() { return this.governmentDebt; }
    public double getStability() { return this.stability; }
    public double getTaxRate() { return this.taxRate; }
    public double getSpendingShare() { return this.spendingShare; }
    public double getInvestmentShare() { return this.investmentShare; }
    public double getTransferShare() { return this.transferShare; }
    public Game getGame() { return this.game; }

    public void setCurrentMonth(int currentMonth) { this.currentMonth = currentMonth; }
    public void setGame(Game game) { this.game = game; }

    protected Round() {}

    public Round(int currentMonth, String stateId, String stateName, double population, double productivity,
                 double publicCapital, double priceLevel, double lastInflation, double householdWealth,
                 double governmentDebt, double stability, double taxRate, double spendingShare,
                 double investmentShare, double transferShare, Game game) {
        this.currentMonth = currentMonth;
        this.stateId = stateId;
        this.stateName = stateName;
        this.population = population;
        this.productivity = productivity;
        this.publicCapital = publicCapital;
        this.priceLevel = priceLevel;
        this.lastInflation = lastInflation;
        this.householdWealth = householdWealth;
        this.governmentDebt = governmentDebt;
        this.stability = stability;
        this.taxRate = taxRate;
        this.spendingShare = spendingShare;
        this.investmentShare = investmentShare;
        this.transferShare = transferShare;
        this.game = game;
    }
}
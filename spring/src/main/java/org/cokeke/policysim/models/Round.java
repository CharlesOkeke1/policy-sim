package org.cokeke.policysim.models;

import jakarta.persistence.*;

@Entity
@Table(name = "rounds")
public class Round {
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
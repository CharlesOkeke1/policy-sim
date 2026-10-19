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

    // Flows for the month (from StateFlows); needed to derive the indicators
    @Column(nullable = false)
    private double output;           
    
    @Column(nullable = false)
    private double outputGap;
    
    @Column(nullable = false)
    private double unemployment;     
    
    @Column(nullable = false)
    private double federalTax;       
    
    @Column(nullable = false)
    private double allocation;       
    
    @Column(nullable = false)
    private double spending;
    
    @Column(nullable = false)
    private double transfers;
    
    @Column(nullable = false)
    private double interest;

    @Column(nullable = false, length = 32)
    private String policy;    
    
    @Column(nullable = false)
    private double federalResourcePrice;

    @Column(nullable = false)
    private double nationalNetWorth;

    @Column(nullable = false)
    private double federalTaxShare;

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
    public double getOutput() { return this.output; }
    public double getOutputGap() { return this.outputGap; }
    public double getUnemployment() { return this.unemployment; }
    public double getFederalTax() { return this.federalTax; }
    public double getAllocation() { return this.allocation; }
    public double getSpending() { return this.spending; }
    public double getTransfers() { return this.transfers; }
    public double getInterest() { return this.interest; }
    public String getPolicy() { return this.policy; }
    public double getFederalResourcePrice() { return this.federalResourcePrice; }
    public double getNationalNetWorth() { return this.nationalNetWorth; }
    public double getFederalTaxShare() { return this.federalTaxShare; }

    public void setCurrentMonth(int currentMonth) { this.currentMonth = currentMonth; }
    public void setGame(Game game) { this.game = game; }

    protected Round() {}

    public Round(int currentMonth, String stateId, String stateName, double population, double productivity,
             double publicCapital, double priceLevel, double lastInflation, double householdWealth,
             double governmentDebt, double stability, double taxRate, double spendingShare,
             double investmentShare, double transferShare, Game game, double output, double outputGap,
             double unemployment, double federalTax, double allocation, double spending,
             double transfers, double interest, String policy, double federalResourcePrice,
             double nationalNetWorth, double federalTaxShare) {
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
        this.output = output;
        this.outputGap = outputGap;
        this.unemployment = unemployment;
        this.federalTax = federalTax;
        this.allocation = allocation;
        this.spending = spending;
        this.transfers = transfers;
        this.interest = interest;
        this.policy = policy;
        this.federalResourcePrice = federalResourcePrice;
        this.nationalNetWorth = nationalNetWorth;
        this.federalTaxShare = federalTaxShare;
        this.game = game;
    }
}
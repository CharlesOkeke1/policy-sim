package org.cokeke.policysim.models;

import io.swagger.v3.oas.annotations.media.Schema;
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
    @Schema(example = "1", description = "The unique primary key identifier for the game record")
    private Long id;

    @Column(nullable = false)
    @Schema(example = "4", description = "The current simulation month elapsed")
    private int currentMonth;

    @Column(nullable = false)
    @Schema(example = "london", description = "The unique alphanumeric code of the state")
    private String stateId;

    @Column(nullable = false)
    @Schema(example = "London", description = "The plain text name of the simulation state")
    private String stateName;

    @Column(nullable = false)
    @Schema(example = "2500000.0", description = "Total state population count")
    private double population;

    @Column(nullable = false)
    @Schema(example = "1.25", description = "State workforce productivity index multiplier")
    private double productivity;

    @Column(nullable = false)
    @Schema(example = "450000000.0", description = "Value of public infrastructure and core capital")
    private double publicCapital;

    @Column(nullable = false)
    @Schema(example = "104.2", description = "Current macro price level index (CPI)")
    private double priceLevel;

    @Column(nullable = false)
    @Schema(example = "2.3", description = "Annualized inflation rate percentage from the last loop")
    private double lastInflation;

    @Column(nullable = false)
    @Schema(example = "75000.0", description = "Average calculated net household savings wealth")
    private double householdWealth;

    @Column(nullable = false)
    @Schema(example = "12000000.0", description = "Total outstanding government debt balance")
    private double governmentDebt;

    @Column(nullable = false)
    @Schema(example = "0.88", description = "Political and economic stability confidence rating (0.0 to 1.0)")
    private double stability;

    @Column(nullable = false)
    @Schema(example = "0.15", description = "Active flat tax rate percentage applied (0.15 = 15%)")
    private double taxRate;

    @Column(nullable = false)
    @Schema(example = "0.30", description = "Share of the state budget allocated to general public spending")
    private double spendingShare;

    @Column(nullable = false)
    @Schema(example = "0.25", description = "Share of the state budget directed into public investments")
    private double investmentShare;

    @Column(nullable = false)
    @Schema(example = "0.45", description = "Share of the state budget set for direct welfare transfers")
    private double transferShare;

    // Flows for the month (from StateFlows); needed to derive the indicators
    @Column(nullable = false)
    @Schema(example = "15000000.0", description = "Total gross economic output for the active cycle")
    private double output;           
    
    @Column(nullable = false)
    @Schema(example = "-0.015", description = "The structural output gap ratio (negative implies underutilization)")
    private double outputGap;
    
    @Column(nullable = false)
    @Schema(example = "0.052", description = "The measured unemployment rate percentage (0.052 = 5.2%)")
    private double unemployment;     
    
    @Column(nullable = false)
    @Schema(example = "2250000.0", description = "Total tax revenue collection extracted for the federal treasury")
    private double federalTax;       
    
    @Column(nullable = false)
    @Schema(example = "1800000.0", description = "Federal fiscal cash pool allocation received by the state")
    private double allocation;       
    
    @Column(nullable = false)
    @Schema(example = "3100000.0", description = "Actual cash spent on state operations this month")
    private double spending;
    
    @Column(nullable = false)
    @Schema(example = "1200000.0", description = "Actual cash distributed via household welfare programs")
    private double transfers;
    
    @Column(nullable = false)
    @Schema(example = "45000.0", description = "Interest service fees paid on the state's debt structure")
    private double interest;

    @Column(nullable = false, length = 32)
    @Schema(example = "AUSTERITY_BALANCED", description = "The codified economic policy index name currently active")
    private String policy;    
    
    @Column(nullable = false)
    @Schema(example = "88.5", description = "The market unit cost index of raw federal energy or resource pools")
    private double federalResourcePrice;

    @Column(nullable = false)
    @Schema(example = "520000000.0", description = "Aggregated national assets minus liability values")
    private double nationalNetWorth;

    @Column(nullable = false)
    @Schema(example = "0.12", description = "The comparative percentage share of federal tax contributions pool")
    private double federalTaxShare;

    @Column(nullable = false)
    @Schema(example = "2450.0", description = "Engine score points calculated for this state in the round")
    private double roundScore;

    @Column(nullable = false)
    @Schema(example = "3", description = "The competitive ranking placement order of this state for the round")
    private int roundRank;


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
    public double getRoundScore() { return roundScore; }
    public int getRoundRank() { return roundRank; }

    public void setCurrentMonth(int currentMonth) { this.currentMonth = currentMonth; }
    public void setGame(Game game) { this.game = game; }

    protected Round() {}

    public Round(int currentMonth, String stateId, String stateName, double population, double productivity,
             double publicCapital, double priceLevel, double lastInflation, double householdWealth,
             double governmentDebt, double stability, double taxRate, double spendingShare,
             double investmentShare, double transferShare, Game game, double output, double outputGap,
             double unemployment, double federalTax, double allocation, double spending,
             double transfers, double interest, String policy, double federalResourcePrice,
             double nationalNetWorth, double federalTaxShare, double score, int rank) {
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
        this.roundScore = score;
        this.roundRank = rank;
    }
}
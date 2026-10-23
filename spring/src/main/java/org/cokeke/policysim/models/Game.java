package org.cokeke.policysim.models;

import java.time.Instant;

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
@Table(name = "games")
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(example = "99", description = "User ID")
    private Long id;

    @Column(nullable = false)
    @Schema(example = "london", description = "ID of chosen state")
    private String stateId;

    @Column(nullable = false)
    @Schema(example = "99", description = "Game seed")
    private int seed;

    @Column(nullable = false)
    @Schema(example = "2026-01-01T00:00:00Z", description = "Game start time")
    private Instant startDate;

    @Column()
    @Schema(example = "2026-02-01T00:00:00Z", description = "Game end time")
    private Instant endDate;

    @Column(nullable = false)
    @Schema(example = "false", description = "Has the game been completed")
    private boolean completed;

    @Column(nullable = false)
    @Schema(example = "24", description = "Number of rounds")
    private int totalRounds;

    @Column(nullable = true)
    @Schema(example = "88.29", description = "Game Score")
    private double score;            // from the engine's scoring, 0-100
    //
    @Column(nullable = true)
    @Schema(example = "3", description = "Game Rank")
    private int rank;                // 1-8 among the cities in this game and month

    @Column(nullable = true)
    @Schema(example = "Easy", description = "Game Difficulty")
    private String difficulty; 

    @Column(nullable = true)
    @Schema(hidden = true)
    private String contractId; 

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    public Long getId() { return this.id; }
    public String getStateId() { return this.stateId; }
    public Instant getStartDate() { return this.startDate; }
    public Instant getEndDate() { return this.endDate; }
    public boolean isCompleted() { return this.completed; }
    public int getSeed() { return this.seed; }
    public double getScore() { return this.score; }
    public int getRank() { return this.rank; }
    public int getTotalRounds() { return this.totalRounds; }
    public User getUser() { return this.user; }
    public String getDifficulty() { return this.difficulty; }
    public String getContractId() { return this.contractId; }

    public void setStateId(String stateName) { this.stateId = stateName; }
    public void setStartDate(Instant startDate) { this.startDate = startDate; }
    public void setEndDate(Instant endDate) { this.endDate = endDate; }
    public void setCompleted(boolean completed) { this.completed = completed; }
    public void setUser(User user) { this.user = user; }
    public void setScore(double score) { this.score = score; }
    public void setDifficulty(String difficulty) { this.difficulty = difficulty; }
    public void setRank(int rank) { this.rank = rank; }
    public void setContractId(String id) { this.contractId = id; }

    protected Game() {}

    public Game(String stateName, User user, 
        int total, int seed, String diff) {
        this.stateId = stateName;
        this.startDate = Instant.now();
        this.endDate = null;
        this.score = 0;
        this.rank = 0;
        this.completed = false;
        this.user = user;
        this.totalRounds = total;
        this.seed = seed;
        this.difficulty = diff;
        this.contractId = null;
    }
}
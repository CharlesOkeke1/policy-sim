package org.cokeke.policysim.models;

import java.time.Instant;

import org.cokeke.policysim.common.GenerateSeed;

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
    private Long id;

    @Column(nullable = false)
    private String stateId;

    @Column(nullable = false)
    private int seed;

    @Column(nullable = false)
    private Instant startDate;

    @Column()
    private Instant endDate;

    @Column(nullable = false)
    private boolean completed;

    @Column(nullable = false)
    private int totalRounds;

    @Column(nullable = true)
    private double score;            // from the engine's scoring, 0-100
    //
    @Column(nullable = true)
    private int rank;                // 1-8 among the cities in this game and month

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

    public void setStateId(String stateName) { this.stateId = stateName; }
    public void setStartDate(Instant startDate) { this.startDate = startDate; }
    public void setEndDate(Instant endDate) { this.endDate = endDate; }
    public void setCompleted(boolean completed) { this.completed = completed; }
    public void setUser(User user) { this.user = user; }
    public void setScore(double score) { this.score = score; }
    public void setRank(int rank) { this.rank = rank; }

    protected Game() {}

    public Game(String stateName, User user, int total) {
        this.stateId = stateName;
        this.startDate = Instant.now();
        this.endDate = null;
        this.score = 0;
        this.rank = 0;
        this.completed = false;
        this.user = user;
        this.totalRounds = total;
        this.seed = GenerateSeed.getRandomSeed();
    }
}
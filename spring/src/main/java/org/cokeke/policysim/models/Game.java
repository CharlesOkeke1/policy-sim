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
    private String stateName;

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

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    public Long getId() { return this.id; }
    public String getStateName() { return this.stateName; }
    public Instant getStartDate() { return this.startDate; }
    public Instant getEndDate() { return this.endDate; }
    public boolean isCompleted() { return this.completed; }
    public int getSeed() { return this.seed; }
    public int getTotalRounds() { return this.totalRounds; }
    public User getUser() { return this.user; }

    public void setStateName(String stateName) { this.stateName = stateName; }
    public void setStartDate(Instant startDate) { this.startDate = startDate; }
    public void setEndDate(Instant endDate) { this.endDate = endDate; }
    public void setCompleted(boolean completed) { this.completed = completed; }
    public void setUser(User user) { this.user = user; }

    protected Game() {}

    public Game(String stateName, User user, int total) {
        this.stateName = stateName;
        this.startDate = Instant.now();
        this.endDate = null;
        this.completed = false;
        this.user = user;
        this.totalRounds = total;
        this.seed = GenerateSeed.getRandomSeed();
    }
}
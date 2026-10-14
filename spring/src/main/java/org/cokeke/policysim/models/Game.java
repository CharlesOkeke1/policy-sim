package org.cokeke.policysim.models;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "games")
public class Game {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String stateName;

    @Column(nullable = false)
    private Instant startDate;

    @Column(nullable = false)
    private Instant endDate;

    @Column(nullable = false)
    private boolean completed;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;


    public Long getId() { return this.id; }
    public String getStateName() { return this.stateName; }
    public Instant getStartDate() { return this.startDate; }
    public Instant getEndDate() { return this.endDate; }
    public boolean isCompleted() { return this.completed; }
    public User getUser() { return this.user; }

    public void setStateName(String stateName) { this.stateName = stateName; }
    public void setStartDate(Instant startDate) { this.startDate = startDate; }
    public void setEndDate(Instant endDate) { this.endDate = endDate; }
    public void setCompleted(boolean completed) { this.completed = completed; }
    public void setUser(User user) { this.user = user; }

    protected Game() {}

    public Game(String stateName, Instant startDate, User user) {
        this.stateName = stateName;
        this.startDate = startDate;
        this.endDate = null;
        this.completed = false;
        this.user = user;
    }
}
package org.cokeke.policysim.models;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "sessions")
public class Session {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private Long gameId;

    @Column(nullable = false, updatable = false)
    private Instant startTime;

    @Column(nullable = true)
    private Instant endTime;

    @Column(length = 45)
    private String engineToken;

    @Column(length = 45)
    private String engineRefreshToken;

    protected Session() {}

    public Session(Long userId, Long gameId) {
        this.userId = userId;
        this.gameId = gameId;
        this.startTime = Instant.now();
        this.endTime = null;
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public Long getGameId() { return gameId; }
    public Instant getStartTime() { return startTime; }
    public Instant getEndTime() { return endTime; }
    public String getEngineToken() { return engineToken; }
    public String getEngineRefreshToken() { return engineRefreshToken; }

    public void setEndTime(Instant endTime) { this.endTime = endTime; }
    public void setEngineToken(String engineToken) { this.engineToken = engineToken; }
    public void setEngineRefreshToken(String engineRefreshToken) { this.engineRefreshToken = engineRefreshToken; }
}
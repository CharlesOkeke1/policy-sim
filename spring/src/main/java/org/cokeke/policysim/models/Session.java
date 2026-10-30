package org.cokeke.policysim.models;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

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

    @Column()
    private String engineToken;

    protected Session() {}

    public Session(Long userId, Long gameId, String token) {
        this.userId = userId;
        this.gameId = gameId;
        this.startTime = Instant.now();
        this.endTime = null;
        this.engineToken = token;
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public Long getGameId() { return gameId; }
    public Instant getStartTime() { return startTime; }
    public Instant getEndTime() { return endTime; }
    public String getEngineToken() { return engineToken; }

    public void setEndTime(Instant endTime) { this.endTime = endTime; }
    public void setEngineToken(String engineToken) { this.engineToken = engineToken; }

    @Override
    public String toString() {
        return "Session {" +
        "id=" + id +
        ", userId=" + userId +
        ", gameId=" + gameId +
        ", startTime=" + startTime +
        ", endTime=" + endTime +
        ", engineToken='" + engineToken + '\'' +
        '}';
    }
}
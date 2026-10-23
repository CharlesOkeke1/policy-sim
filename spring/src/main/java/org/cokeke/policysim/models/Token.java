package org.cokeke.policysim.models;

import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "tokens")
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(example = "1", description = "The unique primary key identifier for the token record")
    private Long id;

    @Column(nullable = false, unique = true)
    @Schema(example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...", description = "The unique authentication or session token string")
    private String token;

    @Column(nullable = false)
    @Schema(example = "10482", description = "The unique identifier of the user this token belongs to")
    private Long userId;

    @Column(nullable = false)
    @Schema(example = "2026-01-01T01:00:00", description = "The exact date and time when this token officially expires")
    private LocalDateTime expirationTime;


    public Token(String token, Long userId) {
        this.token = token;
        this.userId = userId;
        this.expirationTime = LocalDateTime.now().plusMinutes(30);
    }

    public String getToken() { return this.token; }
    public Long getUserId() { return this.userId; }
    public LocalDateTime getExpirationTime() { return this.expirationTime; }
    public Long getId() { return this.id; }

    public void setToken(String token) { this.token = token; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setExpirationTime() { this.expirationTime = LocalDateTime.now().plusMinutes(15); }
    public void setExpirationTime(LocalDateTime time) { this.expirationTime = time; }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expirationTime);
    }

    protected Token() {}
}

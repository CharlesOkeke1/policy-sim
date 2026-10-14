package org.cokeke.policysim.models;

import java.time.LocalDateTime;

import jakarta.persistence.*;

@Entity
@Table(name = "tokens")
public class Token {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String token;

    @Column(nullable = false)
    private Long userId;

    @Column(nullable = false)
    private LocalDateTime expirationTime;

    public Token(String token, Long userId) {
        this.token = token;
        this.userId = userId;
        this.expirationTime = LocalDateTime.now().plusMinutes(15);
    }

    public String getToken() { return this.token; }
    public Long getUserId() { return this.userId; }
    public LocalDateTime getExpirationTime() { return this.expirationTime; }
    public Long getId() { return this.id; }

    public void setToken(String token) { this.token = token; }
    public void setUserId(Long userId) { this.userId = userId; }
    public void setExpirationTime() { this.expirationTime = LocalDateTime.now().plusMinutes(15); }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(this.expirationTime);
    }

    protected Token() {}
}

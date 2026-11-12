package org.cokeke.policysim.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(example = "1", description = "The unique primary key identifier for the user account")
    private Long id;

    @Column(nullable = false)
    @Schema(example = "player1@simulationgame.com", description = "The unique, verified email address of the user")
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false, unique = true)
    @Schema(example = "SimCommander2026", description = "The unique public display username chosen by the user")
    private String username;

    @Column(nullable = true)
    private Instant lastLogin;

    public Long getId() { return this.id; }
    public String getEmail() { return this.email; }
    public String getHash() { return this.passwordHash; }
    public String getUserName() { return username; }
    public Instant getLastLogin() { return this.lastLogin; }

    public void setEmail(String email) { this.email = email; }
    public void setUserName(String n) { this.username = n; }
    public void setHash(String passwordHash) { this.passwordHash = passwordHash; }
    public void setLastLogin(Instant lastLogin) { this.lastLogin = lastLogin; }

    protected  User() {}

    public User(String email, String pass, String name) {
        this.email = email;
        this.passwordHash = pass;
        this.username = name;
        this.lastLogin = null;
    }

    @Override
    public String toString() {
        return "User{id=" + id + ", username='" + username + "'}";
    }
}

package org.cokeke.policysim.models;

import java.time.Instant;
import java.time.LocalDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity
@Table(name = "users", indexes = {
    @Index(name = "idx_user_role", columnList= "role"),
    @Index(name = "idx_username", columnList= "username"),
    @Index(name = "idx_email", columnList = "email")
})
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

    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    private String theme;

    @Column(nullable = true)
    private LocalDateTime themeChangedLast;

    public Long getId() { return this.id; }
    public String getEmail() { return this.email; }
    public String getHash() { return this.passwordHash; }
    public String getUserName() { return username; }
    public Instant getLastLogin() { return this.lastLogin; }
    public Role getUserRole() { return this.role; }
    public boolean isSuperUser() { return this.role == Role.SUPER_USER; }
    public String getTheme() { return this.theme; }
    public LocalDateTime getThemeChangedLast() { return themeChangedLast; }

    public void setEmail(String email) { this.email = email; }
    public void setUserName(String n) { this.username = n; }
    public void setHash(String passwordHash) { this.passwordHash = passwordHash; }
    public void setUserRole(Role role) { this.role = role; }
    public void setLastLogin(Instant lastLogin) { this.lastLogin = lastLogin; }
    public void setTheme(String theme) { this.theme = theme; }
    public void setThemeChangedLast(LocalDateTime themeChangedLast) { this.themeChangedLast = themeChangedLast; }

    protected  User() {}

    public User(String email, String pass, String name) {
        this.email = email;
        this.passwordHash = pass;
        this.username = name;
        this.lastLogin = null;
        this.role = Role.USER;
        this.theme = "Light";
    }

    @Override
    public String toString() {
        return "User{id=" + id + ", username='" + username + "'}";
    }
}

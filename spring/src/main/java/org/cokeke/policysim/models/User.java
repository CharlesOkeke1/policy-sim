package org.cokeke.policysim.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

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
    @Schema(example = "$2a$12$6K2f7w9R...", description = "The secure BCrypt cryptographically hashed user password string")
    private String passwordHash;

    @Column(nullable = false)
    @Schema(example = "SimCommander2026", description = "The unique public display username chosen by the user")
    private String username;


    public Long getId() { return this.id; }
    public String getEmail() { return this.email; }
    public String getHash() { return this.passwordHash; }
    public String getUserName() { return username; }

    public void setEmail(String email) { this.email = email; }
    public void setUserName(String n) { this.username = n; }
    public void setHash(String passwordHash) { this.passwordHash = passwordHash; }

    protected  User() {}

    public User(String email, String pass, String name) {
        this.email = email;
        this.passwordHash = pass;
        this.username = name;
    }
}

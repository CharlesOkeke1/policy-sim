package org.cokeke.policysim.models;

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
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false)
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

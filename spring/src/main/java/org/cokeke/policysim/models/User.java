package org.cokeke.policysim.models;

import jakarta.persistence.*;

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
    private String name;

    public Long getId() { return this.id; }
    public String getEmail() { return this.email; }
    public String getHash() { return this.passwordHash; }
    public String getName() { return name; }

    public void setEmail(String email) { this.email = email; }
    public void setName(String n) { this.name = n; }
    public void setHash(String passwordHash) { this.passwordHash = passwordHash; }

    protected  User() {}

    public User(String email, String pass, String name) {
        this.email = email;
        this.passwordHash = pass;
        this.name = name;
    }
}

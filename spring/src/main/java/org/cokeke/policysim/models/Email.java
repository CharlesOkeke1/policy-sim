package org.cokeke.policysim.models;


import java.time.Instant;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "emails")
public class Email {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(example = "99", description = "Email ID")
    private Long id;

    @Column
    @Schema(example = "42", description = "Associated user ID; null if not associated with a user")
    private Long userId;

    @Column(nullable = false)
    private String senderEmail;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String subject;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = false)
    private String reason;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    protected Email() {}

    public Email(String senderEmail, String title,
                  String subject, String content, String reason) {
        this.userId = null;
        this.senderEmail = senderEmail;
        this.title = title;
        this.subject = subject;
        this.content = content;
        this.reason = reason;
        this.createdAt = Instant.now();
    }

    public Long getId() { return id; }
    public Long getUserId() { return userId; }
    public String getSenderEmail() { return senderEmail; }
    public String getTitle() { return title; }
    public String getSubject() { return subject; }
    public String getContent() { return content; }
    public String getReason() { return reason; }
    public Instant getCreatedAt() { return createdAt; }

    public void setUserId(Long userId) { this.userId = userId; }
}
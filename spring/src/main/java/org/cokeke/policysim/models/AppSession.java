package org.cokeke.policysim.models;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

/**
 * ARCHITECTURAL SUMMARY & DEVELOPMENT PLAN:
 * 
 * 1. Current Workflow: We are adding this 'AppSession' entity directly into the 
 *    PostgreSQL database tracking system. This supports multi-device tracking 
 *    so users can stay logged in on a laptop, tablet, and phone independently.
 * 
 * 2. Login Logic Rule: On every fresh login request, we will skip reusing or extending 
 *    old tokens. Instead, the backend will generate a brand-new access token and a 
 *    unique, 32-bit base64 refresh token, inserting a new row right into this session table.
 *    If the user's sessions refresh token has not expired then return it else create a new one.
 * 
 * 3. Testing & Future Redis Migration: We are writing this as a standard JPA/Hibernate 
 *    entity right now to iron out the client-side fetch wrapper logic and test session behaviors. 
 *    Once the workflow is completely satisfactory, this exact architecture will be copied 
 *    and migrated over to an in-memory Redis structure (@RedisHash) for high-performance execution.
 * 
 * 4. Whenever the user logs out, the session automatically should end
 */
@Entity
@Table(name = "app_sessions", indexes = {
    @Index(name = "idx_refresh_token", columnList = "refreshToken"),
    @Index(name = "idx_user_id", columnList = "userId")
})
public class AppSession {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; 

    @Column(nullable = false, unique = true)
    private String refreshToken; 

    @Column(nullable = false)
    private Long userId; 

    @Column(nullable = false)
    private String ipAddress; 

    @Column(nullable = false)
    //Should take the format, "OS, Browser". Eg, iOS Mobile Safari, Windows 11 Google Chrome
    private String deviceInfo; 

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime refreshTokenExpiryTime; // Lasts for 7 days

    @Column(nullable = false)
    private boolean revoked; 

    @Column(nullable = true)
    private LocalDateTime sessionExpiryTime;

    protected AppSession() {}

    public AppSession(String refreshToken, Long userId, String ipAddress, String deviceInfo) {
        this.refreshToken = refreshToken;
        this.userId = userId;
        this.ipAddress = ipAddress;
        this.deviceInfo = deviceInfo;
        this.refreshTokenExpiryTime = LocalDateTime.now().plusDays(7);
        this.createdAt = LocalDateTime.now();
        this.revoked = false;
        this.sessionExpiryTime = null;
    }

    public boolean isExpiredOrRevoked() {
        return this.revoked || LocalDateTime.now().isAfter(this.refreshTokenExpiryTime);
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getDeviceInfo() { return deviceInfo; }
    public void setDeviceInfo(String deviceInfo) { this.deviceInfo = deviceInfo; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getRefreshTokenExpiryTime() { return refreshTokenExpiryTime; }
    //public void setRefreshTokenExpiryTime(LocalDateTime expiresAt) { this.refreshTokenExpiryTime = expiresAt; }

    public boolean isRevoked() { return revoked; }
    public void setRevoked(boolean revoked) { this.revoked = revoked; }

    public LocalDateTime getSessionExpiryTime() { return this.sessionExpiryTime; }
    public void setSessionExpiryTime(LocalDateTime sessionExpiryTime) { this.sessionExpiryTime = sessionExpiryTime; }
}

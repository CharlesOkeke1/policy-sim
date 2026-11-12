package org.cokeke.policysim.services;

import java.time.LocalDateTime;
import java.util.List;

import org.cokeke.policysim.common.TokenGenerator;
import org.cokeke.policysim.exceptions.AppSessionNotFoundException;
import org.cokeke.policysim.exceptions.AppSessionExpiredException;
import org.cokeke.policysim.exceptions.InvalidRefreshTokenException;
import org.cokeke.policysim.models.AppSession;
import org.cokeke.policysim.models.Token;
import org.cokeke.policysim.repos.AppSessionRepository;
import org.cokeke.policysim.repos.TokenRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.cache.annotation.Cacheable;



/**
 * This now works as intended on a basic level. Because the engine still uses a users token,
 * the engine needs to get the refreshed token. But to avoid that, the GameSession entity will get its own token.
 * So up until i finish implementing that, this is just gonna do.
 */

@Service
public class AppSessionService {
    private final AppSessionRepository repo;
    private final TokenRepository tokenRepo;

    public AppSessionService(AppSessionRepository r, TokenRepository t) {
        this.repo = r;
        this.tokenRepo = t;
    }

    public List<AppSession> getAllSessions() {
        return repo.findAll();
    }

    @Cacheable (value = "session", key = "#id")
    public AppSession getSessionById(Long id) {
        return repo.findById(id).orElseThrow(() -> new AppSessionNotFoundException("Session does not exist"));
    }

    @Cacheable (value = "session", key = "#token")
    public AppSession getBySessionRefreshToken(String token) {
        return repo.getByRefreshToken(token).orElseThrow(() -> new AppSessionNotFoundException("Session does not exist"));
    }

    public AppSession createNewSession(Long userId, String ipAddress, String deviceInfo) {
        String refreshToken = TokenGenerator.generateRefreshToken();
        List<AppSession> sessions = repo.getByUserId(userId).orElse(null);
        AppSession existingSession = sessions.
        stream().filter(sess -> sess.getDeviceInfo().equals(deviceInfo)
        && sess.getIpAddress().equals(ipAddress)).findFirst().orElse(null);

        if (existingSession != null) {
            existingSession.setRevoked(true);
            existingSession.setSessionExpiryTime(LocalDateTime.now());
        }
        
        AppSession newSession = new AppSession(refreshToken, userId, ipAddress, deviceInfo);
        return repo.save(newSession);
    }

    @Transactional
    public Token getNewAuthToken(String refreshToken) {
        if (refreshToken == null || refreshToken.isBlank()) {
            throw new InvalidRefreshTokenException("Refresh token is missing");
        }

        AppSession session = repo.getByRefreshToken(refreshToken).orElse(null);

        if (session == null) throw new AppSessionNotFoundException("Session does not exist");
        if (session.isExpiredOrRevoked()) { 
            throw new AppSessionExpiredException("Refresh Token is now invalid");
        }

        // Craft a new token 
        String newToken = TokenGenerator.generateRefreshToken();
        Token newTokenObject = new Token(newToken, session.getUserId());

        // Delete the old token
        int count = tokenRepo.deleteByUserId(session.getUserId());
        System.out.println("Amount of tokens deleted " + count);
        return tokenRepo.save(newTokenObject);
    }

    public void revokeSession(String refToken) {
        AppSession session = repo.getByRefreshToken(refToken).orElseThrow(() -> new AppSessionNotFoundException("Session does not exist"));
        session.setRevoked(true);
        session.setSessionExpiryTime(LocalDateTime.now());
        session.setRefreshTokenExpiryTime(LocalDateTime.now());

        repo.save(session);
    }
}
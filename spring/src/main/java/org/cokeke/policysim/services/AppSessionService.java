package org.cokeke.policysim.services;

import java.time.LocalDateTime;
import java.util.List;

import org.cokeke.policysim.common.TokenGenerator;
import org.cokeke.policysim.exceptions.AppSessionAlreadyExistsException;
import org.cokeke.policysim.exceptions.AppSessionNotFoundException;
import org.cokeke.policysim.models.AppSession;
import org.cokeke.policysim.repos.AppSessionRepository;
import org.cokeke.policysim.repos.TokenRepository;
import org.springframework.stereotype.Service;
import org.cokeke.policysim.models.Token;


/**
 * This now works as intended on a basic level. Because the engine still uses a users token,
 * the engine needs to get the refreshed token. But to avoid that, the GameSession entity will get its own token.
 * So up until i finish implementing that, this is just gonna do.
 */

@Service
public class AppSessionService {
    private AppSessionRepository repo;
    private TokenRepository tokenRepo;

    public AppSessionService(AppSessionRepository r, TokenRepository t) {
        this.repo = r;
        this.tokenRepo = t;
    }

    public List<AppSession> getAllSessions() {
        return repo.findAll();
    }

    public AppSession getSessionById(Long id) {
        return repo.findById(id).orElseThrow(() -> new AppSessionNotFoundException("Session does not exist"));
    }

    public AppSession getBySessionRefreshToken(String token) {
        return repo.getByRefreshToken(token).orElseThrow(() -> new AppSessionNotFoundException("Session does not exist"));
    }

    public AppSession createNewSession(Long userId, String ipAddress, String deviceInfo) {
        String refreshToken = TokenGenerator.generateRefreshToken();
        AppSession existingSession = repo.getByUserId(userId).orElse(null);

        if (existingSession != null) {
            if (existingSession.getIpAddress().equals(ipAddress) && 
            existingSession.getDeviceInfo().equals(deviceInfo)) {
                throw new AppSessionAlreadyExistsException("Sessionalready exists");
            }
        }

        AppSession newSession = new AppSession(refreshToken, userId, ipAddress, deviceInfo);
        return repo.save(newSession);
    }

    public Token getNewAuthToken(String refreshToken) {
        AppSession session = repo.getByRefreshToken(refreshToken).orElse(null);

        if (session == null) throw new AppSessionNotFoundException("Session does not exist");
        if (session.isExpiredOrRevoked()) { 
            createNewSession(session.getUserId(), session.getIpAddress(), session.getDeviceInfo());
        }

        String newToken = TokenGenerator.generateRefreshToken();
        Token newTokenObject = new Token(newToken, session.getUserId());
        
        tokenRepo.save(newTokenObject);
        return newTokenObject;
    }

    public void revokeSession(String refToken) {
        AppSession session = repo.getByRefreshToken(refToken).orElseThrow(() -> new AppSessionNotFoundException("Session does not exist"));
        session.setRevoked(true);
        session.setSessionExpiryTime(LocalDateTime.now());

        repo.save(session);
    }
}
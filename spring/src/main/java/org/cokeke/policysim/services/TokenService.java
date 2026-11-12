package org.cokeke.policysim.services;

import java.time.LocalDateTime;
import java.util.List;

import org.cokeke.policysim.exceptions.InvalidTokenException;
import org.cokeke.policysim.exceptions.TokenExpiredException;
import org.cokeke.policysim.exceptions.UserNotFoundException;
import org.cokeke.policysim.models.Token;
import org.cokeke.policysim.models.Session;
import org.cokeke.policysim.models.User;
import org.cokeke.policysim.repos.TokenRepository;
import org.cokeke.policysim.repos.UserRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class TokenService {
    private final TokenRepository repo;
    private final UserRepository userRepo;
    private final GameSessionService gameSessionSvc;

    public TokenService(TokenRepository repo, UserRepository userRepo, GameSessionService gameSessionSvc) {
        this.repo = repo;
        this.userRepo = userRepo;
        this.gameSessionSvc = gameSessionSvc;
    }       

    @Transactional
    public String saveToken(Token token) {
        Long id = token.getUserId();
        repo.deleteByUserId(id);
        return repo.save(token).getToken();
    }

    public Token getToken(String token) {
        return repo.findByToken(token).orElseThrow(() -> new InvalidTokenException("Invalid token"));
    }

    public Token findByUserId(Long id) { 
        Token t = repo.findByUserId(id).orElse(null);
        if (t == null) throw new UserNotFoundException("User not found");
        return t;
    }

    public List<Token> getAllTokens() {
        return repo.findAll();
    }

    public Boolean isTokenExpired(Token t) {
        return t.isExpired();
    }

    public void deleteAllTokens() {
        repo.deleteAll();
    }

    public void inalidateToken(String tokenString) {
        Token token = getToken(tokenString);
        token.setExpirationTime(LocalDateTime.now());
    }

    public User getAuthenticatedUser(String tokenString) {
        Token token;
        if (tokenString.startsWith("engine")) {
            Session session = gameSessionSvc.getSessionByToken(tokenString);
            token = findByUserId(session.getUserId());
        } else {
            token = getToken(tokenString);
        }

        if (token == null || token.getToken().isBlank() || token.getToken() == null) {
            throw new InvalidTokenException("Token is invalid");
        }
        
        if (token.isExpired()) throw new TokenExpiredException("Token has expired");
        User user = userRepo.findById(token.getUserId()).orElseThrow(() -> new UserNotFoundException("User doesnt exist"));

        return user;
    }

    public LocalDateTime getTokenExpiryTime(String tokenString) {
        Token token = this.getToken(tokenString);
        return token.getExpirationTime();
    }
}
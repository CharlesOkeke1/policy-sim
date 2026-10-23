package org.cokeke.policysim.services;

import java.time.LocalDateTime;
import java.util.List;

import org.cokeke.policysim.exceptions.InvalidTokenException;
import org.cokeke.policysim.exceptions.TokenExpiredException;
import org.cokeke.policysim.exceptions.UserNotFoundException;
import org.cokeke.policysim.models.Token;
import org.cokeke.policysim.models.User;
import org.cokeke.policysim.repos.TokenRepository;
import org.cokeke.policysim.repos.UserRepository;
import org.springframework.stereotype.Service;


@Service
public class TokenService {
    private final TokenRepository repo;
    private final UserRepository userRepo;
    private final AppSessionService sessionSvc;

    public TokenService(TokenRepository repo, UserRepository userRepo, AppSessionService sessionSvc) {
        this.repo = repo;
        this.userRepo = userRepo;
        this.sessionSvc = sessionSvc;
    }

    public String saveToken(Token token) {
        return repo.save(token).getToken();
    }

    public Token getToken(String token) {
        return repo.findByToken(token).orElseThrow(() -> new InvalidTokenException("Invalid token"));
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
        Token token = this.getToken(tokenString);

        if (token.isExpired()) throw new TokenExpiredException("Token has expired");
        User user = userRepo.findById(token.getUserId()).orElseThrow(() -> new UserNotFoundException("User doesnt exist"));
        if (user == null) throw new UserNotFoundException("User does not exist");

        return user;
    }


    public LocalDateTime getTokenExpiryTime(String tokenString) {
        Token token = this.getToken(tokenString);
        return token.getExpirationTime();
    }
}
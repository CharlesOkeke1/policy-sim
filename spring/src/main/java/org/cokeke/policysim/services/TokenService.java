package org.cokeke.policysim.services;

import java.util.List;

import org.cokeke.policysim.exceptions.InvalidTokenException;
import org.cokeke.policysim.exceptions.TokenExpiredException;
import org.cokeke.policysim.exceptions.UserNotFoundException;
import org.cokeke.policysim.models.Token;
import org.cokeke.policysim.models.User;
import org.cokeke.policysim.repos.TokenRepository;
import org.springframework.stereotype.Service;


@Service
public class TokenService {
    private final TokenRepository repo;
    private final UserService userSvc;

    public TokenService(TokenRepository repo, UserService userSvc) {
        this.repo = repo;
        this.userSvc = userSvc;
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

    public User getAuthenticatedUser(String tokenString) {
        Token token = this.getToken(tokenString);

        if (token.isExpired()) throw new TokenExpiredException("Token has expired");
        User user = userSvc.getUserById(token.getUserId());
        if (user == null) throw new UserNotFoundException("User does not exist");

        return user;
    }
}
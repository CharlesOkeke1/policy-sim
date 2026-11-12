package org.cokeke.policysim.services;

import org.cokeke.policysimcontracts.GameContractService;
import org.cokeke.policysim.exceptions.GameSessionNotFoundException;
import org.cokeke.policysim.exceptions.InvalidContractException;
import org.cokeke.policysim.exceptions.GameNotFoundException;
import org.cokeke.policysim.repos.GameSessionRepository;
import org.cokeke.policysim.repos.GameRepository;
import org.cokeke.policysim.common.TokenGenerator;
import org.cokeke.policysim.models.Game;
import org.cokeke.policysim.models.Session;
import java.time.Instant;

import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;


/**
 * The session service is here to tighten lose ends between spring and server authentication
 * whilst implementing a feature I've been ignoring.
 * 
 * The flow is simple. The client clicks on create game, A game is created and so is the contract. 
 * The client gets the contract string, while spring saves the contractId. Then the user, still in the same creategame function, 
 * gives the paylyoad, with the contract, the users auth token and other client game init data to the engine server. 
 * The server then uses the contract service to verify the contract and extracts the contract claims, which include the contract id, gameId and userId.
 * 
 * Then the this is where the session service and controller come in. The session service will have access to the game repo. With the gameId,
 * it finds the associated game. When the game is found, the games contractId is cross checked with the server provided contractId.
 * If they match, then the session service will create a new session, employing org.cokeke.policysim.common.TokenGenerator to generate a token and refresh token for the server.
 * These tokens are tied to the session and then the endpoint, "/api/session" will create a new session and hand it back to the server.
 * Only at this point can the game actually start. Hoping all this can be done under a second and at most 1500ms.
 */

@Service
public class GameSessionService {
    private final GameContractService contractService;
    private final GameSessionRepository sessionRepo;
    private final GameRepository gameRepo;

    public GameSessionService(GameContractService contractService, 
    GameSessionRepository sessionRepo, GameRepository gsvc){
        this.contractService = contractService;
        this.sessionRepo = sessionRepo;
        this.gameRepo = gsvc;
    }

    public GameContractService getContractService() { return this.contractService; }

    public Session createGameSession(Long gameId, Long userId, String contractId) {
        Game game = gameRepo.findById(gameId).orElse(null);

        if (game == null) {
            throw new GameNotFoundException("Game not found");
        }

        if (contractId == null || !contractId.equals(game.getContractId())) {
            gameRepo.deleteById(game.getId());
            throw new InvalidContractException("Contract is invalid");
        }

        String engineToken = "engine" + gameId + TokenGenerator.generateTokenString();
        Session newSess = new Session(userId, gameId, engineToken);
        newSess = sessionRepo.save(newSess);

        game.setSessionId(newSess.getId());
        game.setStartDate(Instant.now());

        gameRepo.save(game);

        return newSess;
    }

    public Session resumeAGameSession(Long sessionId) {
        Session session = sessionRepo.findById(sessionId).orElseThrow(() -> new GameSessionNotFoundException("Game session does not exist"));
        // Give user a new token
        session.setEngineToken(TokenGenerator.generateTokenString());
        return session;
    }

    @Cacheable (value = "gameSession", key = "#token")
    public Session getSessionByToken(String token) {
        return sessionRepo.getByEngineToken(token).orElseThrow(() -> new GameSessionNotFoundException("Game session does not exist"));
    }
}
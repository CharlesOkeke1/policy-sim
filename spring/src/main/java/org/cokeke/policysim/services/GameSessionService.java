package org.cokeke.policysim.services;

import org.cokeke.policysimcontracts.GameContractService;
import org.cokeke.policysim.exceptions.GameSessionNotFoundException;
import org.cokeke.policysim.exceptions.InvalidContractException;
import org.cokeke.policysim.repos.GameSessionRepository;
import org.cokeke.policysim.repos.GameRepository;
import org.cokeke.policysim.common.TokenGenerator;
import org.cokeke.policysim.models.Game;
import org.cokeke.policysim.models.Session;
import java.time.Instant;

import org.springframework.stereotype.Service;

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
        if (game != null && !contractId.equals(game.getContractId())) {
            gameRepo.deleteById(game.getId());
            throw new InvalidContractException("Contract is invalid");
        }

        String engineToken = "engine" + gameId + TokenGenerator.generateTokenString();
        Session newSess = new Session(userId, gameId, engineToken);
        game.setSessionId(newSess.getId());
        game.setStartDate(Instant.now());

        return sessionRepo.save(newSess);
    }

    public Session getSessionByToken(String token) {
        return sessionRepo.getByEngineToken(token).orElseThrow(() -> new GameSessionNotFoundException("Game session does not exist"));
    }
}
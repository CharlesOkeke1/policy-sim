package org.cokeke.policysim.services;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

import org.cokeke.policysim.exceptions.GameNotFoundException;
import org.cokeke.policysim.exceptions.UserNotFoundException;
import org.cokeke.policysim.models.Game;
import org.cokeke.policysim.models.Round;
import org.cokeke.policysim.models.User;
import org.cokeke.policysim.repos.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GameService {
    private final GameRepository repo;
    private final RoundService roundSvc;
    private final UserService userService;

    @Autowired
    public GameService(GameRepository gameRepository, UserService userService, RoundService roundSvc) {
        this.repo = gameRepository;
        this.userService = userService;
        this.roundSvc = roundSvc;
    }

    public Game createGame(String stateName, Long userId, int totalRounds) {
        User user = userService.getUserById(userId);
        Game game = new Game(stateName, user, totalRounds);
        return repo.save(game);
    }

    public Game getGameById(Long id) {
        return repo.findById(id).orElseThrow(() -> new GameNotFoundException("Game doesn't seem to exist"));
    }

    public List<Game> getAllGames() {
        return repo.findAll();
    }

    public List<Game> getGamesByStatus(Boolean status) {
        return repo.findByCompleted(status);
    }

    public List<Game> getUserGames(Long userId) {
        User user = userService.getUserById(userId);
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }

        return repo.findByUserId(userId);
    }

    public List<Round> getAGamesRounds(Long gameId) {
        List<Round> rounds = roundSvc.getRoundsByGameId(gameId);

        return rounds;
    }

    public Game finishGame(Long gameId) {
        Game game = repo.findById(gameId).orElse(null);
        if (game == null) {
            throw new GameNotFoundException("Game not found");
        }  

        game.setCompleted(true);
        game.setEndDate(Instant.now());
        return repo.save(game);
    }

    public List<String> getStates() {
        String[] states = {"Coventry", "London", "Birmingham", "Manchester",
        "Edinburgh", "Glasgow", "Milton Keynes", "Plymouth"};

        return Arrays.asList(states);
    } 
}
package org.cokeke.policysim.services;

import org.cokeke.policysim.models.Game;
import org.cokeke.policysim.repos.GameRepository;
import org.cokeke.policysim.models.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.cokeke.policysim.exceptions.GameNotFoundException;
import org.cokeke.policysim.exceptions.UserNotFoundException;



import java.time.Instant;


import java.util.List;

@Service
public class GameService {
    private final GameRepository repo;
    private final UserService userService;

    @Autowired
    public GameService(GameRepository gameRepository, UserService userService) {
        this.repo = gameRepository;
        this.userService = userService;
    }

    public Game createGame(String stateName, Instant startDate, Long userId) {
        User user = userService.getUserById(userId);
        Game game = new Game(stateName, startDate, user);
        return repo.save(game);
    }

    public Game getGameById(Long id) {
        return repo.findById(id).orElse(null);
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

    public Game finishGame(Long gameId) {
        Game game = repo.findById(gameId).orElse(null);
        if (game == null) {
            throw new GameNotFoundException("Game not found");
        }  

        game.setCompleted(true);
        game.setEndDate(Instant.now());
        return repo.save(game);
    }
}
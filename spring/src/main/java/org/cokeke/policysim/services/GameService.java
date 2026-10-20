package org.cokeke.policysim.services;

import java.time.Instant;
import java.util.List;

import org.cokeke.policysim.dtos.StatePreview;
import org.cokeke.policysim.exceptions.GameNotFoundException;
import org.cokeke.policysim.exceptions.UserNotFoundException;
import org.cokeke.policysim.models.Game;
import org.cokeke.policysim.models.Round;
import org.cokeke.policysim.models.User;
import org.cokeke.policysim.repos.GameRepository;
import org.springframework.stereotype.Service;

@Service
public class GameService {
    private final GameRepository repo;
    private final RoundService roundSvc;
    private final UserService userService;
    private static final List<StatePreview> STATES = List.of(
        new StatePreview("birmingham", "Birmingham", 1.15, 1.00, 60, "Even start", "BHM"),
        new StatePreview("coventry", "Coventry", 0.35, 0.95, 60, "Tough start", "COV"),
        new StatePreview("edinburgh", "Edinburgh", 0.51, 1.15, 60, "Strong start", "EDI"),
        new StatePreview("glasgow", "Glasgow", 0.63, 0.95, 60, "Even start", "GLW"),
        new StatePreview("london", "London", 8.90, 1.30, 60, "Strong start", "LDN"),
        new StatePreview("manchester", "Manchester", 0.55, 1.05, 60, "Even start", "MAN"),
        new StatePreview("milton-keynes", "Milton Keynes", 0.29, 1.05, 60, "Even start", "MKC"),
        new StatePreview("plymouth", "Plymouth", 0.26, 0.90, 60, "Tough start", "PLY")
    );

    public GameService(GameRepository gameRepository, UserService userService, RoundService roundSvc) {
        this.repo = gameRepository;
        this.userService = userService;
        this.roundSvc = roundSvc;
    }

    public Game createGame(String stateName, Long userId, int totalRounds, int seed, String difficulty) {
        User user = userService.getUserById(userId);
        Game game = new Game(stateName, user, totalRounds, seed, difficulty);
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

    public Game finishGame(Long gameId, double score, int rank) {
        Game game = repo.findById(gameId).orElse(null);
        if (game == null) {
            throw new GameNotFoundException("Game not found");
        }  

        game.setCompleted(true);
        game.setEndDate(Instant.now());
        game.setScore(score);
        game.setRank(rank);
        return repo.save(game);
    }

    public List<StatePreview> getStates() {
        return this.STATES;
    } 

    public String getStateName(String stateId) {
        StatePreview state = this.STATES.stream().filter(s -> s.id().equals(stateId)).findFirst()
        .orElseThrow(() -> new IllegalArgumentException("State not found"));

        return state.name();
    }
}
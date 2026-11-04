package org.cokeke.policysim.services;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.cokeke.policysim.dtos.Policy;
import org.cokeke.policysim.dtos.ResumeGameSvcRes;
import org.cokeke.policysim.dtos.StatePreview;
import org.cokeke.policysim.exceptions.GameNotFoundException;
import org.cokeke.policysim.exceptions.GameSessionNotFoundException;
import org.cokeke.policysim.exceptions.UserNotFoundException;
import org.cokeke.policysim.models.Game;
import org.cokeke.policysim.models.Round;
import org.cokeke.policysim.models.Session;
import org.cokeke.policysim.models.User;
import org.cokeke.policysim.repos.GameRepository;
import org.cokeke.policysim.repos.GameSessionRepository;
import org.cokeke.policysim.responses.DashboardStats;
import org.cokeke.policysim.responses.GameResponse;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.CacheEvict;
//import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;

@Service
public class GameService {
    private final GameRepository repo;
    private final RoundService roundSvc;
    private final UserService userService;
    private final GameSessionRepository sessionRepo;
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

    public static final List<Policy> POLICIES = List.of(
        new Policy("Hold", "HOLD", 0.0, 0.0, "None"),
        new Policy("Raise taxes", "RAISE_TAXES", -1.5, 1.0, "Tax rate"),
        new Policy("Cut taxes", "CUT_TAXES", 1.0, -1.0, "Tax rate"),
        new Policy("Expand spending", "EXPAND_SPENDING", 1.0, 5.0, "Spending"),
        new Policy("Austerity", "AUSTERITY", -2.0, -5.0, "Spending"),
        new Policy("Invest in infrastructure", "INVEST_INFRASTRUCTURE", 0.5, 5.0, "Investment"),
        new Policy("Household support", "HOUSEHOLD_SUPPORT", 1.5, 1.0, "Transfer"),
        new Policy("Debt restructuring", "DEBT_RESTRUCTURING", -3.0, -10.0, "Government debt")
    );

    private static final Map<String, String> STATES_MAP = Map.of(
        "birmingham", "Birmingham",
        "coventry", "Coventry",
        "edinburgh", "Edinburgh",
        "glasgow", "Glasgow",
        "london", "London",
        "manchester", "Manchester",
        "milton-keynes", "Milton Keynes",
        "plymouth", "Plymouth"
    );

    public GameService(GameRepository gameRepository, UserService userService, RoundService roundSvc,
        GameSessionRepository sessionRepo
    ) {
        this.repo = gameRepository;
        this.userService = userService;
        this.roundSvc = roundSvc;
        this.sessionRepo = sessionRepo;
    }

    public Game createGame(String stateName, Long userId, int totalRounds, int seed, String difficulty) {
        User user = userService.getUserById(userId);
        Game game = new Game(stateName, user, totalRounds, seed, difficulty);
        return repo.save(game);
    }

    public void saveGameContractId(Long gameId, String contractId) {
        Game game = getGameById(gameId);
        game.setContractId(contractId);

        repo.save(game);
    }

    @Cacheable(value = "game", key = "#id")
    public Game getGameById(Long id) {
        return repo.findById(id).orElseThrow(() -> new GameNotFoundException("We cannot find game with specified id"));
    }

    @CacheEvict (value = "userGames", key = "#game.id")
    public void deleteById(Long id) {
        Game game = this.getGameById(id);
        repo.deleteById(game.getId());
    }

    @Cacheable (value = "games", key ="'allGames'")
    public List<Game> getAllGames() {
        return repo.findAll();
    }

    public List<Game> getGamesByStatus(Boolean status) {
        return repo.findByCompleted(status);
    }

    @Cacheable (value = "userGames", key = "#userId")
    public List<Game> getUserGames(Long userId) {
        User user = userService.getUserById(userId);
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }

        return repo.findByUserId(userId);
    }

    @Cacheable(value = "gameRounds", key = "#gameId")
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

        Session gSession = sessionRepo.findById(game.getSessionId()).orElseThrow(() -> new GameSessionNotFoundException("Game session not found"));
        gSession.setEndTime(Instant.now());
        return repo.save(game);
    }

    @Cacheable(value = "states", key = "'allStates'")
    public List<StatePreview> getStates() {
        return STATES;
    } 

    @Cacheable(value = "stateName", key = "#stateId")
    public String getStateName(String stateId) {
        return STATES_MAP.get(stateId);
    }

    public GameResponse gameToGameResponse(Game g) {
        List<Round> gRounds = roundSvc.getRoundsByGameId(g.getId());
        GameResponse res = new GameResponse(g.getId(), g.getSeed(), 
        g.getTotalRounds(), g.getStateId(), g.getStartDate(), g.getEndDate(),
        userService.userToUserResponse(g.getUser()), roundSvc.roundListToRoundListWithoutGameObject(gRounds), 
        g.isCompleted(), g.getScore(), g.getRank(), g.getDifficulty());

        return res;
    }

    @Cacheable(value = "policies", key = "'allPolicies'")
    public List<Policy> getPolicies() {
        return POLICIES;
    }

    @Cacheable(value = "userDashboardStats1", key = "#id")
    public DashboardStats getUserDashboardStats(Long id) {
        List<Game> games = getUserGames(id);
        double totalScore = 0;
        int totalMonths = 0;
        int completed = 0; int abandoned = 0; int inProgress = 0;
        Map<Integer, Integer> positionMap = new HashMap<>();

        if (games.isEmpty()) {
            return new DashboardStats(id, 0, 0, 0, new HashMap<>(), 0, 0, 0);
        }

        for (Game g : games) {
            // Tally up scores
            totalScore += g.getScore();

            List<Round> rounds = getAGamesRounds(g.getId());
            if (rounds.isEmpty()) continue;

            // Tally up total Months
            totalMonths += rounds.get(rounds.size() - 1).getCurrentMonth();
            
            //Compute Positions
            if (positionMap.containsKey(g.getRank())) {
                positionMap.put(g.getRank(), positionMap.get(g.getRank()) + 1);
            } else {
                positionMap.put(g.getRank(), 1);
            }

            // Tally up completion
            if (g.isCompleted()) {
                if (g.getTotalRounds() == rounds.get(rounds.size() - 1).getCurrentMonth()) {
                    completed += 1;
                } else {
                    abandoned += 1;
                }
            } else {
                inProgress += 1;
            }
        }

        for (int i = 1; i <= STATES.size(); i++)  {
            if (!positionMap.containsKey(i)) { positionMap.put(i,0); }
        }

        return new DashboardStats(id, totalScore/games.size(), positionMap.get(1), totalMonths,
        positionMap, completed, abandoned, inProgress);
    }

    public ResumeGameSvcRes resumeGame(Long gameId) {
        Game game = getGameById(gameId);
        
        if (game.isCompleted()) {
            throw new IllegalStateException("Cannot resume a completed game");
        }

        List<Round> rounds = getAGamesRounds(gameId);
        Session session = sessionRepo.findById(game.getSessionId()).orElseThrow(() -> new GameSessionNotFoundException("No active session found for this game"));
        return new ResumeGameSvcRes(gameToGameResponse(game), session, roundSvc.roundListToRoundListWithoutGameObject(rounds));
    }
}
package org.cokeke.policysim.controllers;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.cokeke.policysim.common.ApiResponse;
import org.cokeke.policysim.dtos.Difficulty;
import org.cokeke.policysim.dtos.Policy;
import org.cokeke.policysim.dtos.StatePreview;
import org.cokeke.policysim.dtos.ResumeGameSvcRes;
import org.cokeke.policysimcontracts.SpringContractClaims;
import org.cokeke.policysim.common.AuthContextHolder;
import org.cokeke.policysim.common.Environment;
import org.cokeke.policysim.models.Game;
import org.cokeke.policysim.models.Session;
import org.cokeke.policysim.models.Round;
import org.cokeke.policysim.requests.CreateGameRequest;
import org.cokeke.policysim.requests.CreateGameSessionRequest;
import org.cokeke.policysim.requests.FinishGameRequest;
import org.cokeke.policysim.responses.DashboardStats;
import org.cokeke.policysim.responses.GameResponse;
import org.cokeke.policysim.responses.GameSessionInitResponse;
import org.cokeke.policysim.responses.GetUserGames;
import org.cokeke.policysim.responses.GameSessionResponse;
import org.cokeke.policysim.services.GameService;
import org.cokeke.policysim.services.GameSessionService;
import org.cokeke.policysim.services.RoundService;
import org.cokeke.policysim.services.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@RequestMapping ("/api")
@Tag(name = "Game", description = "Handles most game endpoints for mangaging user game data")
public class GameController {
    private final GameService gameService;
    private final RoundService roundService;
    private final UserService uSvc;
    private final GameSessionService sessionService;
    private final AuthContextHolder holder;

    public GameController(GameService gameService, RoundService roundService, 
        GameSessionService s, AuthContextHolder holder, UserService uSvc) {
        this.gameService = gameService;
        this.roundService = roundService;
        this.sessionService = s;
        this.holder = holder;
        this.uSvc = uSvc;
    }

    @GetMapping(Environment.GET_USERS_GAMES)
    public ResponseEntity<ApiResponse<List<GetUserGames>>> getUserGames(@PathVariable Long userId) {
        List<Game> games = gameService.getUserGames(userId);

        List<GetUserGames> gameResponses = games.stream()
                .map(game -> new GetUserGames(game.getId(), game.getSeed(), game.getTotalRounds(), 
                gameService.getStateName(game.getStateId()), game.getStartDate(), game.getEndDate(),
                roundService.getRoundsWithoutGameObjectByGameId(game.getId()), game.isCompleted(), game.getScore(), 
                game.getRank(), game.getDifficulty()))
                .toList();
        return ResponseEntity.ok(ApiResponse.ok(gameResponses));
    }

    @PostMapping(Environment.GAMES)
    public ResponseEntity<ApiResponse<GameSessionResponse>> createGame(@RequestBody CreateGameRequest req) {
        Game game = gameService.createGame(req.stateId(), holder.getCurrentUserId(), req.rounds(), req.seed(), req.difficulty());
        List<Round> rounds = new ArrayList<>();

        SpringContractClaims contractResult = sessionService.getContractService().createContract(game.getUser().getId(), game.getId());
        String sessionId = null;
        gameService.saveGameContractId(game.getId(), contractResult.contractId());

        GameSessionResponse gameResponse = new GameSessionResponse(sessionId, contractResult.contract(),
            new GameResponse(game.getId(), game.getSeed(), game.getTotalRounds(), game.getStateId(), game.getStartDate(), 
            game.getEndDate(), uSvc.userToUserResponse(game.getUser()), roundService.roundListToRoundListWithoutGameObject(rounds), 
            game.isCompleted(), game.getScore(), game.getRank(), game.getDifficulty()));
        ApiResponse<GameSessionResponse> body = ApiResponse.ok(201, gameResponse);
        return ResponseEntity
                .created(URI.create("/api/games"))
                .body(body);
    } 

    @PostMapping(Environment.GAME_SESSION) 
    public ResponseEntity<ApiResponse<GameSessionInitResponse>> createGameSession(@RequestBody CreateGameSessionRequest req) {
        Session session = sessionService.createGameSession(req.gameId(), req.userId(), req.contractId());
        GameSessionInitResponse res = new GameSessionInitResponse(
            session.getId(),
            session.getUserId(),
            session.getGameId(),
            session.getStartTime(),
            session.getEndTime(),
            session.getEngineToken()
        );
        return ResponseEntity.ok(ApiResponse.ok(res));
    }

    @GetMapping(Environment.GAMES)
    public ResponseEntity<ApiResponse<List<GameResponse>>> getGames() {
        List<Game> games = gameService.getAllGames();
        List<GameResponse> out = new ArrayList<>();

        for (Game g : games) {
            out.add(gameService.gameToGameResponse(g));
        }
        return ResponseEntity.ok(ApiResponse.ok(out));
    }
    

    @PutMapping(Environment.GAME_BY_ID)
    public ResponseEntity<ApiResponse<GameResponse>> finishGame(@PathVariable Long gameId, @RequestBody FinishGameRequest req) {
        Game game = gameService.finishGame(gameId, req.score(), req.rank());
        GameResponse gameResponse = gameService.gameToGameResponse(game);
        return ResponseEntity.ok(ApiResponse.ok(gameResponse));
    }

    @PutMapping(Environment.ABANDON_GAME)
    public ResponseEntity<ApiResponse<GameResponse>> abandonGame(@PathVariable Long gameId, @RequestBody FinishGameRequest req) {
        Game game = gameService.finishGame(gameId, req.score(), req.rank());
        GameResponse gameResponse = gameService.gameToGameResponse(game);
        return ResponseEntity.ok(ApiResponse.ok(gameResponse));
    }

    @GetMapping(Environment.GAME_BY_ID)
    public ResponseEntity<ApiResponse<GameResponse>> getGame(@PathVariable Long gameId) {
        Game game = gameService.getGameById(gameId);
        GameResponse gameResponse = gameService.gameToGameResponse(game);
        return ResponseEntity.ok(ApiResponse.ok(gameResponse));
    }

    @GetMapping(Environment.STATES)
    public ResponseEntity<ApiResponse<List<StatePreview>>> getStates() {
        List<StatePreview> states = gameService.getStates();
        return ResponseEntity.ok(ApiResponse.ok(states));
    }

    @GetMapping(Environment.DIFFICULTY)
    public ResponseEntity<ApiResponse<List<Difficulty>>> getDifficulties() {
        List<Difficulty> diffs = List.of(new Difficulty("Easy", "Rivals pick policies loosely and make frequent mistakes."),
        new Difficulty("Normal", "Rivals react to inflation and debt, but not perfectly."), 
        new Difficulty("Hard", "Rivals read the numbers every month and rarely slip"));
        return ResponseEntity.ok(ApiResponse.ok(diffs));
    }

    @GetMapping(Environment.POLICY)
    public ResponseEntity<ApiResponse<List<Policy>>> getPolicies() {
        return ResponseEntity.ok(ApiResponse.ok(gameService.getPolicies()));
    }

    @GetMapping(Environment.DASHBOARD_STATS) 
    public ResponseEntity<ApiResponse<DashboardStats>> getUserDashboardStats(@PathVariable Long id) {
        DashboardStats stats = gameService.getUserDashboardStats(id);
        return ResponseEntity.ok(ApiResponse.ok(stats));
    }

    @GetMapping(Environment.SESSION_BY_TOKEN)
    public ResponseEntity<ApiResponse<Session>> getSessionsByToken(@PathVariable String sessionToken) {
        Session session = sessionService.getSessionByToken(sessionToken);
        return ResponseEntity.ok(ApiResponse.ok(session));
    }

    @PostMapping(Environment.RESUME_GAME)
    public ResponseEntity<ApiResponse<ResumeGameSvcRes>> resumeGame(@PathVariable Long gameId) {
        ResumeGameSvcRes result = gameService.resumeGame(gameId);
        return ResponseEntity.ok(ApiResponse.ok(result));
    }
}
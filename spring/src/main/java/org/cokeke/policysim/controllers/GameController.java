package org.cokeke.policysim.controllers;

import org.cokeke.policysim.models.Game;
import org.cokeke.policysim.requests.CreateGameRequest;
import org.cokeke.policysim.common.ApiResponse;
import org.cokeke.policysim.common.AuthContextHolder;
import org.cokeke.policysim.responses.GameResponse;
import org.cokeke.policysim.responses.GameSessionResponse;
import org.cokeke.policysim.services.GameService;
import org.cokeke.policysim.models.Round;
import org.cokeke.policysim.services.RoundService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import org.cokeke.policysim.services.GameSessionService;
import org.cokeke.policysim.services.UserService;
import org.springframework.web.bind.annotation.RequestParam;


@RestController
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

    @GetMapping("/api/users/{userId}/games")
    public ResponseEntity<ApiResponse<List<GameResponse>>> getUserGames(@PathVariable Long userId) {
        List<Game> games = gameService.getUserGames(userId);

        List<GameResponse> gameResponses = games.stream()
                .map(game -> new GameResponse(game.getId(), game.getSeed(), game.getTotalRounds(), game.getStateName(), game.getStartDate(), game.getEndDate(), uSvc.userToUserResponse(game.getUser()), roundService.getRoundsByGameId(game.getId()), game.isCompleted()))
                .toList();
        return ResponseEntity.ok(ApiResponse.ok(gameResponses));
    }

    @PostMapping("/api/games")
    public ResponseEntity<ApiResponse<GameSessionResponse>> createGame(@RequestBody CreateGameRequest req) {
        Game game = gameService.createGame(req.stateName(), holder.getCurrentUserId(), req.rounds());
        List<Round> rounds = new ArrayList<>();

        String contract = sessionService.getContractService().createContract(game.getUser().getId(), game.getId());
        String sessionId = "Test" + game.getId();
        GameSessionResponse gameResponse = new GameSessionResponse(sessionId, contract,
            new GameResponse(game.getId(), game.getSeed(), game.getTotalRounds(), game.getStateName(), game.getStartDate(), 
            game.getEndDate(), uSvc.userToUserResponse(game.getUser()), rounds, game.isCompleted()));
        ApiResponse<GameSessionResponse> body = ApiResponse.ok(201, gameResponse);
        return ResponseEntity
                .created(URI.create("/api/games/" + gameResponse.sessionId()))
                .body(body);
    } 

    @GetMapping("/api/games")
    public ResponseEntity<ApiResponse<List<GameResponse>>> getGames() {
        List<Game> games = gameService.getAllGames();
        List<GameResponse> out = new ArrayList<>();

        for (Game g : games) {
            List<Round> gRounds = roundService.getRoundsByGameId(g.getId());
            GameResponse res = new GameResponse(g.getId(), g.getSeed(), 
            g.getTotalRounds(), g.getStateName(), g.getStartDate(), g.getEndDate(),
            uSvc.userToUserResponse(g.getUser()), gRounds, g.isCompleted());

            out.add(res);
        }

        return ResponseEntity.ok(ApiResponse.ok(out));
    }
    

    @PutMapping("/api/games/{gameId}")
    public ResponseEntity<ApiResponse<GameResponse>> finishGame(@PathVariable Long gameId) {
        Game game = gameService.finishGame(gameId);
        List<Round> rounds = roundService.getRoundsByGameId(game.getId());
        GameResponse gameResponse = new GameResponse(game.getId(), game.getSeed(), game.getTotalRounds(), game.getStateName(), game.getStartDate(), game.getEndDate(), uSvc.userToUserResponse(game.getUser()), rounds, game.isCompleted());
        return ResponseEntity.ok(ApiResponse.ok(gameResponse));
    }

    @GetMapping("/api/games/{gameId}")
    public ResponseEntity<ApiResponse<GameResponse>> getGame(@PathVariable Long gameId) {
        Game game = gameService.getGameById(gameId);
        if (game == null) {
            return ResponseEntity.notFound().build();
        }
        List<Round> rounds = roundService.getRoundsByGameId(game.getId());
        GameResponse gameResponse = new GameResponse(game.getId(), game.getSeed(), game.getTotalRounds(), game.getStateName(), game.getStartDate(), game.getEndDate(), uSvc.userToUserResponse(game.getUser()), rounds, game.isCompleted());
        return ResponseEntity.ok(ApiResponse.ok(gameResponse));
    }

    @GetMapping("/api/games/states")
    public ResponseEntity<ApiResponse<List<String>>> getStates() {
        List<String> states = gameService.getStates();
        return ResponseEntity.ok(ApiResponse.ok(states));
    }
}
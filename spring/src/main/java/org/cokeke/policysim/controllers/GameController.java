package org.cokeke.policysim.controllers;

import org.cokeke.policysim.models.Game;
import org.cokeke.policysim.requests.CreateGameRequest;
import org.cokeke.policysim.common.ApiResponse;
import org.cokeke.policysim.responses.GameResponse;
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

@RestController
public class GameController {
    private final GameService gameService;
    private final RoundService roundService;

    public GameController(GameService gameService, RoundService roundService) {
        this.gameService = gameService;
        this.roundService = roundService;
    }

    @GetMapping("/api/users/{userId}/games")
    public ResponseEntity<ApiResponse<List<GameResponse>>> getUserGames(@PathVariable Long userId) {
        List<Game> games = gameService.getUserGames(userId);
        List<Round> rounds = roundService.getRoundsByGameId(userId);

        List<GameResponse> gameResponses = games.stream()
                .map(game -> new GameResponse(game.getId(), game.getStateName(), game.getStartDate(), game.getEndDate(), game.getUser(), roundService.getRoundsByGameId(game.getId()), game.isCompleted()))
                .toList();
        return ResponseEntity.ok(ApiResponse.ok(gameResponses));
    }

    @PostMapping("/api/games")
    public ResponseEntity<ApiResponse<GameResponse>> createGame(@RequestBody CreateGameRequest req) {
        Game game = gameService.createGame(req.stateName(), req.startDate(), req.userId());
        List<Round> rounds = new ArrayList<>();
        GameResponse gameResponse = new GameResponse(game.getId(), game.getStateName(), game.getStartDate(), game.getEndDate(), game.getUser(), rounds, game.isCompleted());
        ApiResponse<GameResponse> body = ApiResponse.ok(201, gameResponse);
        return ResponseEntity
                .created(URI.create("/api/games/" + gameResponse.id()))
                .body(body);
    } 

    @PutMapping("/api/games/{gameId}")
    public ResponseEntity<ApiResponse<GameResponse>> finishGame(@PathVariable Long gameId) {
        Game game = gameService.finishGame(gameId);
        List<Round> rounds = roundService.getRoundsByGameId(game.getId());
        GameResponse gameResponse = new GameResponse(game.getId(), game.getStateName(), game.getStartDate(), game.getEndDate(), game.getUser(), rounds, game.isCompleted());
        return ResponseEntity.ok(ApiResponse.ok(gameResponse));
    }

    @GetMapping("/api/games/{gameId}")
    public ResponseEntity<ApiResponse<GameResponse>> getGame(@PathVariable Long gameId) {
        Game game = gameService.getGameById(gameId);
        if (game == null) {
            return ResponseEntity.notFound().build();
        }
        List<Round> rounds = roundService.getRoundsByGameId(game.getId());
        GameResponse gameResponse = new GameResponse(game.getId(), game.getStateName(), game.getStartDate(), game.getEndDate(), game.getUser(), rounds, game.isCompleted());
        return ResponseEntity.ok(ApiResponse.ok(gameResponse));
    }

    // @PutMapping("/api/games/{gameId}/rounds")
    // public ResponseEntity<ApiResponse<GameResponse>> addRoundsToGame(@PathVariable Long gameId, @RequestBody List<Round> rounds) {
    //     Game game = gameService.addRoundsToGame(gameId, rounds);
    //     GameResponse gameResponse = new GameResponse(game.getId(), game.getStateName(), game.getStartDate(), game.getEndDate(), game.getUser(), game.getRounds(), game.isCompleted());
    //     return ResponseEntity.ok(ApiResponse.ok(gameResponse));
    // }
}
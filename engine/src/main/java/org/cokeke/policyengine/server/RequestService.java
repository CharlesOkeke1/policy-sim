package org.cokeke.policyengine.server;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import org.cokeke.policyengine.db.*;
import org.cokeke.policyengine.functions.Engine;
import org.cokeke.policyengine.models.EngineServerResponse;
import org.cokeke.policyengine.models.FinishGameDTO;
import org.cokeke.policyengine.models.GameConfig;
import org.cokeke.policyengine.models.ApiResponse;
import org.cokeke.policyengine.models.PolicyId;
import org.cokeke.policyengine.models.RankedOutput;
import org.cokeke.policyengine.models.SessionDTO;
import org.cokeke.policyengine.models.SessionInitDTO;
import org.cokeke.policyengine.models.SpringRoundDTO;
import org.cokeke.policyengine.models.SpringUploadData;
import org.cokeke.policyengine.models.StateId;
import org.cokeke.policysimcontracts.GameContractClaims;
import org.cokeke.policysimcontracts.GameContractService;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JavaType;

public class RequestService {
    public final GameContractService contractService;
    private final ObjectMapper mapper;
    private Engine engine;
    public GameConfig gameConfig;

    // These have actually become redundant because the we a running a client-side heartbeat
    // This means the logic remains but the client will be the one to implement it
    private final AtomicInteger heartbeatFailures = new AtomicInteger(0);
    private static final int MAX_PING_FAILURES = 3;


    private static final String PROD_URL = "https://policysimapi.charlesokeke.com";
    private static final String DEV_URL = "http://localhost:8080";
    private final HttpClient client;

    private final LettuceGameRepository dbRepo = RedisDB.getRepository();

    public RequestService(GameContractService contractService, ObjectMapper mapper, HttpClient client) {
        this.contractService = contractService;
        this.mapper = mapper;
        this.client = client;
    }

    public Object handle(String type, JsonNode payload) {
        return switch (type) {
            case "INITIALIZE_SESSION" ->
                initializeSession(payload);

            case "ADVANCE_ROUND" ->
                getRound(payload);

            case "GET_GAME_PROGRESS" ->
                getProgress();
                
            case "FINISH_GAME" -> 
                finishGame(payload);
            
            case "SAVE_GAME" ->
                saveGame(payload);

            case "PING_SERVER" ->
                getClientPing(payload);

            case "PAUSE_GAME" -> 
                pauseGame();

            case "ABANDON_GAME" ->
                /**
                 * FINISH GAME AND ABANDON GAME DO THE EXACT SAME THING.
                 * An abandoned game is a finished game where the user didnt finish all the rounds.
                 */
                finishGame(payload); 

            case "RESUME_GAME" ->
                resumeGame(payload);

            default ->
                throw new IllegalArgumentException(
                    "Unknown request type: " + type
                );
        };
    }

    /**
     * Takes the contract provided by the client, calls on the contract 
     * service to verify the contract and returns the contract claims.
     * @param contract The string provided by the client
     * @return claims The GameContractClaims object returned from verification
     */
    public GameContractClaims connect(String contract) {
        GameContractClaims claims = contractService.verifyContract(contract);
        return claims;
    }

    /**
     * Handles the client ping to let the server know that the user is still connected.
     * This serves as a client-based heartbeat protocol to manage connection status.
     * 
     * @param payload The incoming ping data or metadata from the client.
     * @return A string response of "pong" to confirm the server is connected.
     */
    public String getClientPing(JsonNode payload) {
        String ping = payload.get("networkPing").asText();

        if (this.heartbeatFailures.get() >= MAX_PING_FAILURES) {
            cleanUp();
        }

        if (ping.isEmpty()) this.heartbeatFailures.getAndIncrement();
        else this.heartbeatFailures.getAndSet(0);

        return "pong";
    }

    /**
     * Handles game initialization. Takes the client passed payload and extracts the necessary data.
     * The server then uses the connect function to verify the contract and extract the claims which contain vital game data not provided in the payload
     * After that the server will use the claims and user token once to post to the backend requesting a new game session.
     * The returned session will contain the engine instance's own authentication token used for future requests.
     * After all this the game can start.
     * 
     * @param payload a JsonNode payload sent from the client.
     * @return server response. A string and response time in this case.
     * 
     *  */ 

    private EngineServerResponse<String> initializeSession(JsonNode payload) {
        String contract = payload.get("contract").asText();

        GameContractClaims claims = connect(contract);
        int seed = payload.get("game").get("seed").asInt();
        
        int rounds = payload.get("game").get("noOfRounds").asInt();
        String difficulty = payload.get("game").get("difficulty").asText();
        String token = payload.get("authToken").asText();

        SessionDTO session = startGameSession(claims.gameId(), claims.userId(), claims.contractId(), token);
        if (session == null) return new EngineServerResponse<String>("Failed to create session", ZonedDateTime.now().format(DateTimeFormatter.ISO_INSTANT));

        this.engine = new Engine(seed);
        this.gameConfig = new GameConfig(0, rounds, claims.gameId(), seed, session.engineToken(), difficulty, claims.userId());

        String time = ZonedDateTime.now().format(DateTimeFormatter.ISO_INSTANT);
        return new EngineServerResponse<>("Game successfully initialized", time);
    }

    /**
     * Takes a json payload from the client and extracts the gameId, seed, rounds, difficulty, token and userId.
     * Add the preliminary scores and ranks to the scoresMap for each state. Does not touch the pendingUpdates List though.
     * The server then uses the extracted data to continue the game by passing past rounds, current month and seed to the engine and gameConfig.
     * @param payload containing a session object, game object and a list of past rounds.
     * @return Success message and timestamp.
     */
    public EngineServerResponse<String> resumeGame(JsonNode payload) {
        Long gameId = payload.get("gameId").asLong();
        int seed = payload.get("seed").asInt();
        int totalRounds = payload.get("noOfRounds").asInt();
        
        String difficulty = payload.get("difficulty").asText();
        Long userId = payload.get("userId").asLong(); 
        
        String engineToken = payload.get("engineToken").asText();

        GameObject game = dbRepo.getGame(gameId);
        List<SpringRoundDTO> roundsList = game.getRoundsAsSpringDTO();
        if (game.getGameId() == gameId && !roundsList.isEmpty()) System.out.println("Game successfully loaded from redis");

        int currentMonth = !roundsList.isEmpty() ? roundsList.get(roundsList.size() - 1).currentMonth() : 0;

        this.gameConfig = new GameConfig(currentMonth, totalRounds, gameId, seed, engineToken, difficulty, userId);
        this.engine = new Engine(roundsList, seed, currentMonth); // Alternative engine init for resuming games.

        String time = ZonedDateTime.now().format(DateTimeFormatter.ISO_INSTANT);
        return new EngineServerResponse<>("Game successfully resumed", time);
    }

    /**
     * Server uses the gameId, userId, contractId and token to post to the backend, requesting a new session.
     * On success, the session will be returned for initializeSession().
     * 
     * @param gameId Contract claims returned gameId
     * @param userId Contract claims returned userId
     * @param contractId Contract claims returned contract id
     * @param token One time use user authentication token.
     * 
     * @return Session Object returned from backend.
     */
    public SessionDTO startGameSession(Long gameId, Long userId, String contractId, String token) {
        String api = PROD_URL + "/api/games/session";
        try {
            SessionInitDTO body = new SessionInitDTO(gameId, userId, contractId);
            ApiResponse<SessionDTO> response = postToSpring("POST", body, api, SessionDTO.class, token);
            return response.result();
        } catch (Exception e) {
            e.printStackTrace();
            throw new IllegalArgumentException("Cannot create game", e);
        }
    }

    private <T> ApiResponse<T> postToSpring(
        String method,
        Object body,
        String url,
        Class<T> resultType,
        String token
    ) {
        if (token == null) token = gameConfig.getAuthToken();
        
        try {
            String stringified = body == null
                ? ""
                : mapper.writeValueAsString(body);

            HttpRequest.BodyPublisher publisher = body == null
                ? HttpRequest.BodyPublishers.noBody()
                : HttpRequest.BodyPublishers.ofString(stringified);

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .method(method, publisher)
                .build();

            HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString()
            );

            JavaType responseType = mapper.getTypeFactory()
                .constructParametricType(ApiResponse.class, resultType);

            System.out.println("Response code: " + response.statusCode());
            System.out.println("Request path: " + request.uri().toString().substring(24));
            return mapper.readValue(response.body(), responseType);

        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    private EngineServerResponse<List<SpringRoundDTO>> getRound(JsonNode payload) {
        String policyId = payload.get("policyId").asText();
        String state = payload.get("stateId").asText();
        PolicyId id = PolicyId.valueOf(policyId);
        StateId stateId = new StateId(state);

        List<SpringRoundDTO> rounds = requireEngine().getRound(stateId, id);

        dbRepo.saveRound(gameConfig.getGameId(), rounds);        
        gameConfig.setCurrentMonth(rounds.get(0).currentMonth());

        /**
         * This will most likely be changed as a better approach will require changing a lot of stuff at the moment.
         */

        String time = ZonedDateTime.now().format(DateTimeFormatter.ISO_INSTANT);
        return new EngineServerResponse<List<SpringRoundDTO>>(rounds, time);
    }

    public String saveGame(JsonNode payload) {
        try {
            pushToSpring(); // Save all game rounds.
        } catch (Exception e) {
            e.printStackTrace();
        }
        

        Long gameId = gameConfig.getGameId();
        String stateId = payload.get("stateId").asText();
        RankedOutput rankedState = dbRepo.rankState(gameId, stateId);

        double score = rankedState.score();
        int rank = rankedState.rank();
        
        String api = PROD_URL + "/api/games/save/" + gameId;
        try {
            FinishGameDTO body = new FinishGameDTO(score, rank);
            ApiResponse<Object> response = postToSpring("POST", body, api, Object.class, null);
            
            return "Game saved successfully";
        } catch (Exception e) {
            throw new IllegalArgumentException("Save game request failed");
        }  
    }

    public String pauseGame() {
        return "Game paused Successfully";
    }

    /**
     * When the game finished, the status should be set as complete, Instant.now() for end date, 
     * 
     * @return
     */
    private String finishGame(JsonNode payload) {
        try {
            pushToSpring(); // Save all game rounds.
        } catch (Exception e) {
            e.printStackTrace();
        }

        Long gameId = gameConfig.getGameId();
        String stateId = payload.get("stateId").asText();
        RankedOutput rankedState = dbRepo.rankState(gameId, stateId);

        double score = rankedState.score();
        int rank = rankedState.rank();
        
        String api = PROD_URL + "/api/games/" + gameId;
        try {
            FinishGameDTO body = new FinishGameDTO(score, rank);
            ApiResponse<Object> response = postToSpring("PUT", body, api, Object.class, null);
            
            cleanUp();
            dbRepo.deleteGame(gameId);
            
            if (response.code() != 200) throw new IllegalStateException("Saving game failed");
            return "Game finished successfully";
        } catch (Exception e) {
            throw new IllegalArgumentException("Finish game request failed");
        }  
    }

    public void pushToSpring() {
        String api = PROD_URL + "/api/games/rounds/batchUpload";
        Long gameId = gameConfig.getGameId();
        GameObject game = dbRepo.getGame(gameId);
        SpringUploadData payload = new SpringUploadData(game.getRoundsAsSpringDTO(), gameConfig.getGameId());

        try {
            postToSpring("POST", payload, api, Object.class, null);
        } catch (Exception e) {
            throw new IllegalArgumentException("Batched game data save failed");
        }     
    }
    

    private double getProgress() {
        return requireEngine().getProgress(gameConfig.getTotalRounds());
    }

    private Engine requireEngine() {
        if (this.engine == null) {
            throw new IllegalStateException("Session has not been initialized");
        }

        return this.engine;
    }

    /**
     * Call when the game finishes.
     */
    public void cleanUp() {
        this.engine = null;
        this.gameConfig = null;
        this.heartbeatFailures.set(0);
    }
}
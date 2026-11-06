package org.cokeke.policyengine.server;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.cokeke.policyengine.functions.Engine;
import org.cokeke.policyengine.functions.GameRanking;
import org.cokeke.policyengine.models.EngineServerResponse;
import org.cokeke.policyengine.models.FinishGameDTO;
import org.cokeke.policyengine.models.GameConfig;
import org.cokeke.policyengine.models.PolicyId;
import org.cokeke.policyengine.models.RankedOutput;
import org.cokeke.policyengine.models.SessionDTO;
import org.cokeke.policyengine.models.SessionInitDTO;
import org.cokeke.policyengine.models.SpringRoundDTO;
import org.cokeke.policyengine.models.SpringUploadData;
import org.cokeke.policyengine.models.StateId;
import org.cokeke.policysimcontracts.GameContractClaims;
import org.cokeke.policysimcontracts.GameContractService;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RequestService {
    public final GameContractService contractService;
    private final ObjectMapper mapper = new ObjectMapper();
    private Engine engine;
    public GameConfig gameConfig;
    private final Map<String, List<Double>> scoresMap = new ConcurrentHashMap<>();
    private final List<SpringRoundDTO> pendingUpdates = Collections.synchronizedList(new ArrayList<>());

    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();
    private boolean schedulerStarted = false;
    private final AtomicInteger heartbeatFailures = new AtomicInteger(0);
    private static final int MAX_PING_FAILURES = 3;

    private final HttpClient client = HttpClient.newBuilder().build();

    public RequestService(GameContractService contractService) {
        this.contractService = contractService;
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

        if (ping.isEmpty()) {
            this.heartbeatFailures.getAndIncrement();
        }

        this.heartbeatFailures.getAndSet(0);
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
        if (session == null) return new EngineServerResponse<>("Failed to create session", ZonedDateTime.now().format(DateTimeFormatter.ISO_INSTANT));

        this.engine = new Engine(seed);
        this.gameConfig = new GameConfig(0, rounds, claims.gameId(), seed, session.engineToken(), difficulty, claims.userId());

        if (!schedulerStarted) {
            scheduler.scheduleAtFixedRate(
                this::pushToSpring,
                1,
                1,
                TimeUnit.MINUTES
            );

            schedulerStarted = true;
        }
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
        Long gameId = payload.get("game").get("id").asLong();
        int seed = payload.get("game").get("seed").asInt();
        
        int rounds = payload.get("game").get("noOfRounds").asInt();
        String difficulty = payload.get("game").get("difficulty").asText();
        String token = payload.get("session").get("engineToken").asText();
        Long userId = payload.get("session").get("userId").asLong();
        List<SpringRoundDTO> roundsList = mapper.convertValue(
            payload.get("rounds"),
            new TypeReference<List<SpringRoundDTO>>() {}
        );        
        int currentMonth = !roundsList.isEmpty() ? roundsList.get(roundsList.size() - 1).currentMonth() : 0;

        this.gameConfig = new GameConfig(currentMonth, rounds, gameId, seed, token, difficulty, userId);
        this.engine = new Engine(roundsList, seed, currentMonth); // Alternative engine init for resuming games.

        for (SpringRoundDTO round : roundsList) {
            if (scoresMap.containsKey(round.stateId())) {
                List<Double> scores = scoresMap.get(round.stateId());
                scores.add(round.roundScore());
                scoresMap.put(round.stateId(), scores);
            } else {
                List<Double> scores = new ArrayList<>();
                scores.add(round.roundScore());
                scoresMap.put(round.stateId(), scores);
            }
        }

        if (!schedulerStarted) {
            scheduler.scheduleAtFixedRate(
                this::pushToSpring,
                1,
                1,
                TimeUnit.MINUTES
            );

            schedulerStarted = true;
        }
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
        String api = "http://localhost:8080/api/games/session";

        try {
            String body = mapper.writeValueAsString(
                new SessionInitDTO(gameId, userId, contractId)
            );

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(api))
                .header("Accept", "application/json")
                .header("Authorization", "Bearer " + token)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

            System.out.println(request.uri());
            HttpResponse<String> response = HttpClient.newHttpClient().send(
                request,
                HttpResponse.BodyHandlers.ofString()
            );

            JsonNode root = mapper.readTree(response.body());
            return mapper.treeToValue(root.get("result"), SessionDTO.class);

        } catch (Exception e) {
            throw new IllegalArgumentException("Cannot create game", e);
        }
    }

    private EngineServerResponse<List<SpringRoundDTO>> getRound(JsonNode payload) {
        String policyId = payload.get("policyId").asText();
        String state = payload.get("stateId").asText();
        PolicyId id = PolicyId.valueOf(policyId);
        StateId stateId = new StateId(state);

        List<SpringRoundDTO> rounds = requireEngine().getRound(stateId, id);
        
        gameConfig.setCurrentMonth(rounds.get(0).currentMonth());
        pendingUpdates.addAll(rounds);

        /**
         * This will most likely be changes as a better approach will require changing a lot of stuff at the moment.
         */
        for (SpringRoundDTO round : rounds) {
            if (scoresMap.containsKey(round.stateId())) {
                List<Double> scores = scoresMap.get(round.stateId());
                scores.add(round.roundScore());
                scoresMap.put(round.stateId(), scores);
            } else {
                List<Double> scores = new ArrayList<>();
                scores.add(round.roundScore());
                scoresMap.put(round.stateId(), scores);
            }
        }
        String time = ZonedDateTime.now().format(DateTimeFormatter.ISO_INSTANT);
        return new EngineServerResponse<>(rounds, time);
    }

    public String saveGame(JsonNode payload) {
        Long gameId = gameConfig.getGameId();
        String stateId = payload.get("stateId").asText();
        RankedOutput rankedState = GameRanking.rank(scoresMap, stateId);

        double score = rankedState.score();
        int rank = rankedState.rank();
        
        String api = "http://localhost:8080/api/games/save/" + gameId;
        if (!pendingUpdates.isEmpty()) {
            pushToSpring();
        }
        try {
            String body = mapper.writeValueAsString(new FinishGameDTO(score, rank));
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(api))
                .header("Authorization", "Bearer " + this.gameConfig.getAuthToken())
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            handleBatchResponse(response, "Game finished successfully");
            return "Game saved Successfully";
        } catch (Exception e) {
            throw new IllegalArgumentException("Finish game request failed");
        }  
    }

    public String pauseGame() {
        if (!pendingUpdates.isEmpty()) {
            pushToSpring();
        }
        return "Game paused Successfully";
    }

    /**
     * When the game finished, the status should be set as complete, Instant.now() for end date, 
     * 
     * @return
     */
    private String finishGame(JsonNode payload) {
        Long gameId = gameConfig.getGameId();
        String stateId = payload.get("stateId").asText();
        RankedOutput rankedState = GameRanking.rank(scoresMap, stateId);

        double score = rankedState.score();
        int rank = rankedState.rank();
        
        String api = "http://localhost:8080/api/games/" + gameId;
        if (!pendingUpdates.isEmpty()) {
            pushToSpring();
        }
        try {
            String body = mapper.writeValueAsString(new FinishGameDTO(score, rank));
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(api))
                .header("Authorization", "Bearer " + this.gameConfig.getAuthToken())
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(body))
                .build();

            System.out.println(request.uri());

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            handleBatchResponse(response, "Game finished successfully");
            cleanUp();
            return "Game succeffully completed";
        } catch (Exception e) {
            throw new IllegalArgumentException("Finish game request failed");
        }  
    }

    /**
     * Function should be called every minute using the batched queue
    */
    public void pushToSpring() {
        String api = "http://localhost:8080/api/games/rounds/batchUpload";
        SpringUploadData payload = new SpringUploadData(pendingUpdates, gameConfig.getGameId());

        try {
            if (!pendingUpdates.isEmpty()) {
                String jsonBody = mapper.writeValueAsString(payload);
                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(api))
                    .header("Authorization", "Bearer " + this.gameConfig.getAuthToken())
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

                System.out.println(request.uri());

                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                
                handleBatchResponse(response, "Batch saved successfully");
                pendingUpdates.clear();  
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Batched game data save failed");
        }     
    }
    

    private double getProgress() {
        return requireEngine().getProgress(gameConfig.getTotalRounds());
    }

    public void handleBatchResponse(HttpResponse<String> response, String message) {
        int status = response.statusCode();

        if (status >= 200 && status < 300) {
            return;
        }

        System.err.println("Batch request failed with status: " + status);
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
        try {
            if (!pendingUpdates.isEmpty()) {
                pushToSpring();
            }
        
        } catch(Exception e) {
            System.out.println("Emergency round persistence failed during server clean up");
        } finally {
            if (scheduler != null && !scheduler.isShutdown()) {
                scheduler.shutdown();

                try {
                    // If scheduler still hasnt shutdown after 5 seconds, force it.
                    if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                        scheduler.shutdownNow(); 
                    }
                } catch (InterruptedException e) {
                    scheduler.shutdownNow(); 
                    Thread.currentThread().interrupt();
                }
            }

            scoresMap.clear();
            pendingUpdates.clear();
            this.engine = null;
        }
    }
}
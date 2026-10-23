package org.cokeke.policyengine.server;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
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
import org.cokeke.policyengine.models.RankedOutput;
import org.cokeke.policyengine.models.SpringRoundDTO;
import org.cokeke.policyengine.models.SpringUploadData;
import org.cokeke.policysimcontracts.GameContractClaims;
import org.cokeke.policysimcontracts.GameContractService;

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
    private AtomicInteger heartbeatFailures = new AtomicInteger(0);
    private static final int MAX_PING_FAILURES = 3;

    public RequestService(GameContractService contractService) {
        this.contractService = contractService;
    }

    public Object handle(String type, JsonNode payload) {
        return switch (type) {
            case "INITIALIZE_SESSION" ->
                initializeSession(payload);

            case "ADVANCE_ROUND" ->
                getRound();

            case "GET_GAME_PROGRESS" ->
                getProgress();
                
            case "FINISH_GAME" -> 
                finishGame(payload);
            
            case "SAVE_GAME" ->
                saveGame();

            case "PING_SERVER" ->
                getClientPing(payload);

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

    private EngineServerResponse<String> initializeSession(JsonNode payload) {
        String contract = payload.get("contract").asText();

        GameContractClaims claims = connect(contract);
        int seed = payload.get("game").get("seed").asInt();
        
        int rounds = payload.get("game").get("noOfRounds").asInt();
        String difficulty = payload.get("game").get("difficulty").asText();
        String token = payload.get("authToken").asText();

        this.engine = new Engine(seed);
        this.gameConfig = new GameConfig(0, rounds, claims.gameId(), seed, token, difficulty);

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

    private EngineServerResponse<List<SpringRoundDTO>> getRound() {
        List<SpringRoundDTO> rounds = requireEngine().getRound();
        
        gameConfig.setCurrentMonth(rounds.get(0).currentMonth());
        pendingUpdates.addAll(rounds);

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

    public String saveGame() {
        if (!pendingUpdates.isEmpty()) {
            pushToSpring();
        }
        return "Game saved Successfully";
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

            System.out.println("FINISH GAME [PUT]=>" + api + " at " + LocalDateTime.now());

            HttpResponse<String> response = HttpClient.newHttpClient()
            .send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Status: " + response.statusCode());
            System.out.println("Response: " + response.body());
            
            handleBatchResponse(response, "Game finished successfully");
            return "Game succeffully completed";
        } catch (Exception e) {
            throw new IllegalArgumentException("Payload doesnt meet requirements");
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

                System.out.println("PUSH TO DB [POST]=> " + api + " at " + LocalDateTime.now());
                IO.print(gameConfig.getAuthToken());
                System.out.println("Request body: " + jsonBody);

                HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());
                System.out.println("Status: " + response.statusCode());
                System.out.println("Response: " + response.body());
                
                handleBatchResponse(response, "Batch saved successfully");
                pendingUpdates.clear();  
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Payload doesnt meet requirements");
        }     
    }
    

    private double getProgress() {
        return requireEngine().getProgress(gameConfig.getTotalRounds());
    }

    public void handleBatchResponse(HttpResponse<String> response, String message) {
        int status = response.statusCode();

        if (status >= 200 && status < 300) {
            System.out.println(message);
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
package org.cokeke.policyengine.server;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.util.ArrayList;
import java.util.List;

import org.cokeke.policyengine.functions.Engine;
import org.cokeke.policyengine.models.GameConfig;
import org.cokeke.policyengine.models.RoundData;
import org.cokeke.policyengine.models.SpringRoundDTO;
import org.cokeke.policyengine.models.SpringUploadData;
import org.cokeke.policyengine.models.StateEconomy;
import org.cokeke.policysimcontracts.GameContractClaims;
import org.cokeke.policysimcontracts.GameContractService;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class RequestService {
    public final GameContractService contractService;
    private final ObjectMapper mapper = new ObjectMapper();
    private Engine engine;
    public GameConfig gameConfig;
    List<StateEconomy> pendingUpdates = new ArrayList<>();
    private final ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

    private boolean schedulerStarted = false;

    public RequestService(GameContractService contractService) {
        this.contractService = contractService;
    }

    public Object handle(String type, JsonNode payload) {
        return switch (type) {
            case "INITIALIZE_SESSION" ->
                initializeSession(payload);
            case "GET_ROUND" ->
                getRound(payload);
            case "GET_GAME_PROGRESS" ->
                getProgress();
            default ->
                throw new IllegalArgumentException(
                    "Unknown request type: " + type
                );
        };
    }

    public GameContractClaims connect(String token) {
        GameContractClaims claims = contractService.verifyContract(token);
        return claims;
    }

    private String initializeSession(JsonNode payload) {
        String contract = payload.get("contract").asText();

        GameContractClaims claims = connect(contract);
        int seed = payload.get("game").get("seed").asInt();
        
        int rounds = payload.get("game").get("noOfRounds").asInt();
        String token = payload.get("authToken").asText();

        this.engine = new Engine(seed);
        this.gameConfig = new GameConfig(1, rounds, claims.gameId(), seed, token);

        if (!schedulerStarted) {
            scheduler.scheduleAtFixedRate(
                this::pushToSpring,
                1,
                1,
                TimeUnit.MINUTES
            );

            schedulerStarted = true;
        }

        return "Game successfully initialized";
    }

    private RoundData getRound(JsonNode payload) {
        RoundData rounds = requireEngine().getRound();

        List<StateEconomy> stateList = rounds.getStates();
        
        gameConfig.setCurrentMonth(rounds.getCurrentMonth());
        pendingUpdates.addAll(stateList);
        return rounds;
    }

    /**
     * When the game finished, the status should be set as complete, Instant.now() for end date, 
     * 
     * @return
     */
    // private void finishGame(JsonNode payload) {
        
    // }
    /**
     * Function should be called every minute using the batched queue
    */
    public void pushToSpring() {
        String api = "http://localhost:8080/api/games/rounds/batchUpload";
        List<SpringRoundDTO> list = new ArrayList<>();
        for (StateEconomy state : pendingUpdates) {
            list.add(new SpringRoundDTO(state));
        }
        SpringUploadData payload = new SpringUploadData(list, gameConfig.getGameId());

        try {
            if (list.size() > 0) {
                String jsonBody = mapper.writeValueAsString(payload);
                HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(api))
                    .header("Authorization", "Bearer " + this.gameConfig.getAuthToken())
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();

                System.out.println("POST " + api);
                IO.print(gameConfig.getAuthToken());
                System.out.println("Request body: " + jsonBody);

                HttpResponse<String> response = HttpClient.newHttpClient()
                .send(request, HttpResponse.BodyHandlers.ofString());
                System.out.println("Status: " + response.statusCode());
                System.out.println("Response: " + response.body());
                
                handleBatchResponse(response);
                pendingUpdates.clear();  
            }
        } catch (Exception e) {
            throw new IllegalArgumentException("Payload doesnt meet requirements");
        }     
    }
    

    private double getProgress() {
        return requireEngine().getProgress(gameConfig.getTotalRounds());
    }

    public void handleBatchResponse(HttpResponse<String> response) {
        int status = response.statusCode();

        if (status >= 200 && status < 300) {
            System.out.println("Batch saved successfully");
            return;
        }

        System.err.println("Batch request failed with status: " + status);
        // TODO: retry or queue the batch for later
    }

    

    private Engine requireEngine() {
        if (this.engine == null) {
            throw new IllegalStateException("Session has not been initialized");
        }

        return this.engine;
    }
}
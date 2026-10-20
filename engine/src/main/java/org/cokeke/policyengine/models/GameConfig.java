package org.cokeke.policyengine.models;

public class GameConfig {
    private int currentMonth;
    private int totalRounds;
    private Long gameId;
    private int seed;
    private String authToken;
    private boolean gameStarted;
    private String difficulty;

    public GameConfig(int month, int rounds, Long gameId, 
        int seed, String authToken, String difficulty) {
        this.currentMonth = month;
        this.totalRounds = rounds;
        this.gameId = gameId;
        this.seed = seed;
        this.authToken = authToken;
        this.gameStarted = true;
        this.difficulty = difficulty;
    }

    public int getCurrentMonth() { return this.currentMonth; }
    public int getSeed() { return this.seed; }
    public int getTotalRounds() { return this.totalRounds; }
    public Long getGameId() { return this.gameId; }
    public String getAuthToken() { return this.authToken; }
    public boolean hasGameStarted() { return this.gameStarted; }
    public String getDifficulty() { return this.difficulty; }

    public void setCurrentMonth(int c) { this.currentMonth = c; }
}

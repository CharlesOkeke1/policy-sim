package org.cokeke.policysim.common;

public class Environment {
    // UserController.java
    public static boolean IS_PRODUCTION = false;
    public static final String LOGIN = "/users/login";
    public static final String SIGNUP = "/users/new";
    public static final String GET_TOKENS = "/tokens";
    public static final String USERS = "/users";
    public static final String GET_USERNAMES = "/usernames";
    public static final String LOGOUT = "/users/logout";
    public static final String USER_BY_ID = "/users/{id}";
    public static final String CHANGE_PASSWORD = "/users/change-password";
    public static final String REFRESH = "/users/refresh";
    public static final String ME = "/users/me";
    public static final String EMAIL_SUPPORT = "/users/support";


    // GameController.java
    public static final String GET_USERS_GAMES = "/users/{userId}/games";
    public static final String GAMES = "/games";
    public static final String GAME_SESSION = "/games/session";
    public static final String GAME_BY_ID = "/games/{gameId}";
    public static final String SAVE_GAME = "/games/save/{gameId}";
    public static final String PAGINATED_GAMES_BY_ID = "/games/{userId}/paginated";
    public static final String ABANDON_GAME = "/games/abandon-game/{gameId}";
    public static final String STATES = "/games/states";
    public static final String DIFFICULTY = "/games/difficulty";
    public static final String POLICY = "/games/policies";
    public static final String DASHBOARD_STATS = "/games/{id}/stats";
    public static final String SESSION_BY_TOKEN = "/games/session/{sessionToken}";
    public static final String RESUME_GAME = "/games/{gameId}/resume";


    // RoundController.java
    public static final String ROUNDS_BY_GAME_ID = "/games/{gameId}/rounds";
    public static final String BATCH_UPLOAD_ROUNDS = "/games/rounds/batchUpload";
    public static final String ROUND_BY_ID = "/games/rounds/{roundId}";


    public Environment() {}
}

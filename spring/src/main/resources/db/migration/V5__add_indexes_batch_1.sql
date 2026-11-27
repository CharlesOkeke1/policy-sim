BEGIN;

    -- App Session's Indexes
    CREATE INDEX IF NOT EXISTS app_sessions_idx_user_id ON app_sessions(user_id);
    CREATE INDEX IF NOT EXISTS app_sessions_idx_createdAt ON app_sessions(created_at);
    CREATE INDEX IF NOT EXISTS app_sessions_idx_refresh_token ON app_sessions(refresh_token);

    -- Game's Indexes
    CREATE INDEX IF NOT EXISTS games_idx_user_id ON games(user_id);
    CREATE INDEX IF NOT EXISTS games_idx_completed ON games(completed);
    CREATE INDEX IF NOT EXISTS games_idx_difficulty ON games(difficulty);
    CREATE INDEX IF NOT EXISTS games_idx_stateId ON games(state_id);
    CREATE INDEX IF NOT EXISTS games_idx_startDate ON games(start_date);
    CREATE INDEX IF NOT EXISTS games_idx_sessionId ON games(session_id);

    --Round's Indexed
    CREATE INDEX IF NOT EXISTS rounds_idx_gameId ON rounds(game_id);
    CREATE INDEX IF NOT EXISTS rounds_idx_stateId ON rounds(state_id);
    CREATE INDEX IF NOT EXISTS rounds_idx_stateName ON rounds(state_name);

COMMIT;
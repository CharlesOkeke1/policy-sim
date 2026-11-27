BEGIN;
    -- Game Session's Indexes
    CREATE INDEX IF NOT EXISTS idx_session_user_id ON sessions(user_id);
    CREATE INDEX IF NOT EXISTS idx_session_game_id ON sessions(game_id);
    CREATE INDEX IF NOT EXISTS idx_session_token ON sessions(engine_token);
    CREATE INDEX IF NOT EXISTS idx_session_start_time ON sessions(start_time);

    -- Token's Indexes
    CREATE INDEX IF NOT EXISTS idx_tokens_userId ON tokens(user_id);
    CREATE INDEX IF NOT EXISTS idx_tokens_token_string ON tokens(token);
    CREATE INDEX IF NOT EXISTS idx_tokens_expiry ON tokens(expiration_time);

    -- User's Indexes
    CREATE INDEX IF NOT EXISTS idx_user_role ON users(role);
    CREATE INDEX IF NOT EXISTS idx_username ON users(username);
    CREATE INDEX IF NOT EXISTS idx_email ON users(email);
COMMIT;
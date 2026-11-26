package org.cokeke.policyengine.db;

import org.cokeke.policyengine.common.EnvConfig;

import io.lettuce.core.*;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.sync.RedisCommands;

public class RedisDB {
    private static RedisClient client;
    private static StatefulRedisConnection<String, String> connection;
    private static LettuceGameRepository repo;
    private static RedisCommands<String, String> commands;

    public static void init() {
        if (client != null) return;

        client = RedisClient.create(
            "redis://:" + EnvConfig.get("ENGINE_REDIS_PASSWORD") + "@redis-engine:6379"
        );
        
        connection = client.connect();
        commands = connection.sync();
        repo = new LettuceGameRepository(commands);
    }

    public static LettuceGameRepository getRepository() {
        if (repo == null) throw new IllegalStateException("Redis DB has not been initialized");
        return repo;
    }

    public static void shutdown() {
        if (connection != null) connection.close();
        if (client != null) client.shutdown();
    }
}

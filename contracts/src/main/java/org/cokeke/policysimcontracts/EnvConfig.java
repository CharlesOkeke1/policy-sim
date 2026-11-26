package org.cokeke.policysimcontracts;

import io.github.cdimascio.dotenv.Dotenv;

public class EnvConfig {
    private static final Dotenv dotenv = Dotenv.configure()
        .directory("/app")
        .filename(".env")
        .load();

    public static String get(String key) {
        return dotenv.get(key, System.getenv(key));
    }
}
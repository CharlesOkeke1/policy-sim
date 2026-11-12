package org.cokeke.policysim.common;

import io.github.cdimascio.dotenv.Dotenv;

public class EnvConfig {
    private static final Dotenv dotenv = Dotenv.configure()
            .directory("./")
            .filename(".env")
            .load();

    public static String get(String key) {
        return dotenv.get(key, System.getenv(key));
    }
}
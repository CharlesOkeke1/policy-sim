package org.cokeke.policysim.external;

import org.cokeke.policysim.common.EnvConfig;
import org.flywaydb.core.Flyway;

public class FlywayRepair {
    public static void main(String[] args) {
        Flyway flyway = Flyway.configure()
                .dataSource(
                        EnvConfig.get("DB_URL"),
                        EnvConfig.get("DB_USERNAME"),
                        EnvConfig.get("DB_PASSWORD")
                )
                .load();

        flyway.repair();

        System.out.println("Flyway repair completed.");
    }
}
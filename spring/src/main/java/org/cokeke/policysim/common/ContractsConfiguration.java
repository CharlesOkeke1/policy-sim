package org.cokeke.policysim.common;

import org.cokeke.policysimcontracts.GameContractService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.Base64;


@Configuration
public class ContractsConfiguration {
    @Bean
    public GameContractService gameContractService(JacksonConfiguration objectMapper) {
        byte[] secret = Base64.getDecoder()
                .decode(EnvConfig.get("CONTRACTS_SECRET"));

        return new GameContractService(
                secret,
                objectMapper.objectMapper(),
                "spring-backend",
                "game-engine",
                300
        );
    }
}
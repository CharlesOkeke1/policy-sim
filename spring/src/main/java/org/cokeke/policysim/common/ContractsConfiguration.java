package org.cokeke.policysim.common;

import org.cokeke.policysimcontracts.GameContractService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.Base64;


@Configuration
public class ContractsConfiguration {
    @Bean
    public GameContractService gameContractService(JacksonConfiguration objectMapper, 
    @Value("${contracts.secret}") String secretBase64) {
        byte[] secret = Base64.getDecoder()
                .decode(secretBase64);

        return new GameContractService(
                secret,
                objectMapper.objectMapper(),
                "spring-backend",
                "game-engine",
                300
        );
    }
}
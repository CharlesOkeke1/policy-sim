package org.cokeke.policysimcontracts;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public final class GameContractService {

    private static final String HMAC_ALGORITHM = "HmacSHA256";

    private static final Base64.Encoder ENCODER =
            Base64.getUrlEncoder().withoutPadding();

    private static final Base64.Decoder DECODER =
            Base64.getUrlDecoder();

    private final byte[] secret;
    private final ObjectMapper objectMapper;
    private final SecureRandom random = new SecureRandom();

    private final String issuer;
    private final String audience;
    private final long lifetimeSeconds;

    public GameContractService(byte[] secret, ObjectMapper objectMapper,
    String issuer, String audience, long lifetimeSeconds) {
        if (secret == null || secret.length < 32) {
            throw new IllegalArgumentException("HMAC secret must contain at least 32 bytes");
        }

        if (lifetimeSeconds <= 0) {
            throw new IllegalArgumentException(
                    "Contract lifetime must be positive"
            );
        }

        this.secret = secret.clone();
        this.objectMapper = objectMapper;
        this.issuer = issuer;
        this.audience = audience;
        this.lifetimeSeconds = lifetimeSeconds;
    }

    public String createContract(Long userId, Long gameId) {
        if (userId == null || gameId == null) {
            throw new IllegalArgumentException(
                    "User ID and game ID are required"
            );
        }

        long now = Instant.now().getEpochSecond();

        GameContractClaims claims = new GameContractClaims(UUID.randomUUID().toString(),
        userId, gameId, issuer, audience, now, now + lifetimeSeconds);

        try {
            byte[] json = objectMapper.writeValueAsBytes(claims);
            String payload = ENCODER.encodeToString(json);

            // Sign the encoded payload.
            String signature = ENCODER.encodeToString(
                    sign(payload.getBytes(StandardCharsets.US_ASCII))
            );

            // Format: payload.signature
            return payload + "." + signature;

        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Could not serialize game contract", e);
        }
    }

    public GameContractClaims verifyContract(String token) {
        if (token == null || token.isBlank()) {
            throw new InvalidContractException("Contract is missing");
        }

        String[] parts = token.split("\\.", -1);
        if (parts.length != 2 || parts[0].isEmpty() || parts[1].isEmpty()) {
            throw new InvalidContractException("Malformed contract");
        }

        try {
            byte[] suppliedSignature = DECODER.decode(parts[1]);
            byte[] expectedSignature = sign(parts[0].getBytes(StandardCharsets.US_ASCII));

            if (!MessageDigest.isEqual(
                    expectedSignature, suppliedSignature)) {
                throw new InvalidContractException(
                        "Invalid contract signature"
                );
            }

            byte[] payload = DECODER.decode(parts[0]);
            GameContractClaims claims = objectMapper.readValue(payload, GameContractClaims.class);
            long now = Instant.now().getEpochSecond();

            if (claims.contractId() == null || claims.contractId().isBlank() || claims.userId() == null
            || claims.gameId() == null) {
                throw new InvalidContractException("Required contract claims are missing");
            }

            if (!issuer.equals(claims.issuer())) {
                throw new InvalidContractException(
                        "Unexpected contract issuer"
                );
            }

            if (!audience.equals(claims.audience())) {
                throw new InvalidContractException(
                        "Contract is intended for another service"
                );
            }

            if (claims.issuedAt() <= 0
                    || claims.expiresAt() <= claims.issuedAt()
                    || claims.expiresAt() - claims.issuedAt()
                            > lifetimeSeconds) {
                throw new InvalidContractException("Invalid contract lifetime");
            }

            if (claims.issuedAt() > now) {
                throw new InvalidContractException("Contract was issued in the future");
            }

            if (claims.expiresAt() <= now) {
                throw new InvalidContractException("Contract has expired");
            }

            return claims;

        
        } catch (InvalidContractException e) {
            throw e;

        } catch (IllegalArgumentException e) {
            throw new InvalidContractException("Malformed contract encoding", e);

        } catch (JsonProcessingException e) {
            throw new InvalidContractException("Invalid contract payload", e);

        } catch (IOException e) {
            throw new InvalidContractException("Invalid contract payload", e);
        }

    }

    private byte[] sign(byte[] data) {
        try {
            Mac mac = Mac.getInstance(HMAC_ALGORITHM);

            mac.init(new SecretKeySpec(secret, HMAC_ALGORITHM));

            return mac.doFinal(data);

        } catch (GeneralSecurityException e) {
            throw new IllegalStateException(
                    "Could not sign contract", e
            );
        }
    }
}

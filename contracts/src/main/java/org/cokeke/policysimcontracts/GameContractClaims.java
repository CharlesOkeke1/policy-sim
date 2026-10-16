package org.cokeke.policysimcontracts;

public record GameContractClaims(
        String contractId,
        Long userId,
        Long gameId,
        String issuer,
        String audience,
        long issuedAt,
        long expiresAt
) {}
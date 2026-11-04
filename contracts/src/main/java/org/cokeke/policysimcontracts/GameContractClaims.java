package org.cokeke.policysimcontracts;

public record GameContractClaims(
        String contractId,
        Long userId,
        Long gameId,
        String issuer,
        String audience,
        long issuedAt,
        long expiresAt
) {
        @Override
        public String toString() {
        return "GameContractClaims{" +
                "contractId='" + contractId + '\'' +
                ", userId=" + userId +
                ", gameId=" + gameId +
                ", issuer='" + issuer + '\'' +
                ", audience='" + audience + '\'' +
                ", issuedAt=" + issuedAt +
                ", expiresAt=" + expiresAt +
                '}';
        }
}
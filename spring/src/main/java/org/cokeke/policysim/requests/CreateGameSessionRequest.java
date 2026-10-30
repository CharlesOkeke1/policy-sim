package org.cokeke.policysim.requests;

public record CreateGameSessionRequest(Long gameId, Long userId, String contractId) {}

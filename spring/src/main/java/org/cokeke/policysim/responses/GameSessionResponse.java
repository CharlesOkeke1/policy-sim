package org.cokeke.policysim.responses;

public record GameSessionResponse(String sessionId, String contract, GameResponse game) {}
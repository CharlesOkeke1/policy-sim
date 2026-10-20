package org.cokeke.policysim.requests;

public record CreateGameRequest(String stateId, int rounds, int seed, String difficulty) {}
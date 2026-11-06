package org.cokeke.policysim.requests;

public record PaginatedGameRequest(int start, int stop, Long gameId) {}
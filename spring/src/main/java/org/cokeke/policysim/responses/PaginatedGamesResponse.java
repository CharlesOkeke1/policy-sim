package org.cokeke.policysim.responses;

import java.util.List;

public record PaginatedGamesResponse(List<GameResponse> games, int gameCount) {}
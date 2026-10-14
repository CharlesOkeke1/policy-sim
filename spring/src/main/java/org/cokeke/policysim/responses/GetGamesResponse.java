package org.cokeke.policysim.responses;

import org.cokeke.policysim.models.Game;
import java.util.List;

public record GetGamesResponse(List<Game> games) {}
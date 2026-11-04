package org.cokeke.policysim.dtos;

import java.util.List;

import org.cokeke.policysim.models.Game;
import org.cokeke.policysim.models.Session;
import org.cokeke.policysim.responses.GameResponse;

public record ResumeGameSvcRes(GameResponse game, Session session, List<RoundWithoutGame> rounds) {}

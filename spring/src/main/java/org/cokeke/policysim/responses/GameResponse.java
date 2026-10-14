package org.cokeke.policysim.responses;
import java.time.Instant;

import org.cokeke.policysim.models.User;
import org.cokeke.policysim.models.Round;
import java.util.List;

public record GameResponse(Long id, String stateName, Instant startDate, Instant endDate, User user, List<Round> rounds, Boolean completed) {}
package org.cokeke.policysim.requests;

import java.util.List;

import org.cokeke.policysim.dtos.SpringRoundDTO;
import org.cokeke.policysim.models.Round;

public record BatchRoundUploadRequest(List<SpringRoundDTO> rounds, Long gameId) {}

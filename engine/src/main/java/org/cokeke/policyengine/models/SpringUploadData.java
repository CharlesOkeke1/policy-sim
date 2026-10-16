package org.cokeke.policyengine.models;

import java.util.List;
public record SpringUploadData(List<SpringRoundDTO> rounds, Long gameId) {}
package org.cokeke.policyengine.models;

public record SessionDTO(Long id, Long userId, Long gameId,
    String startTime, String endTime, String engineToken) {}
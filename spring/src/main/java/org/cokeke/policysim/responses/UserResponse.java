package org.cokeke.policysim.responses;

public record UserResponse(Long id, String email, String name, String authToken) {}

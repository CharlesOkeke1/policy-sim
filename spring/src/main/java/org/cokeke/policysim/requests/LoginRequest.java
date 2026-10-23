package org.cokeke.policysim.requests;

public record LoginRequest(String email, String password, String ipAddress, String deviceInfo) {}
package org.cokeke.policysim.requests;

public record LogoutRequest(String token, String refreshToken) {}

package org.cokeke.policysim.requests;

public record ChangePasswordRequest(String oldPassword, String newPassword) {}

package org.cokeke.policyengine.models; 

public record ApiResponse<T>(
    int code,
    String message,
    String error,
    T result
) {}
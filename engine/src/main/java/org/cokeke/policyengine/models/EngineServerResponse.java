package org.cokeke.policyengine.models;

public record EngineServerResponse<T>(T result, String responseTime) {}

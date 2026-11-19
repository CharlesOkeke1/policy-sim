package org.cokeke.policyengine.db;

public class RedisOperationFailedException extends RuntimeException {
    public RedisOperationFailedException(String msg) { super(msg); }
}
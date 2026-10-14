package org.cokeke.policysim.exceptions;

public class TokenExpiredException extends RuntimeException {
    public TokenExpiredException(String msg) { super(msg); }
}
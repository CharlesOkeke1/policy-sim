package org.cokeke.policysim.exceptions;

public class InvalidRefreshTokenException extends RuntimeException {
    public InvalidRefreshTokenException(String msg) { super(msg); }
}

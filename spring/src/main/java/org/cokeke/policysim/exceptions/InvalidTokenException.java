package org.cokeke.policysim.exceptions;

public class InvalidTokenException extends InvalidCredentialsException {
    public InvalidTokenException(String msg) { super(msg); }
}
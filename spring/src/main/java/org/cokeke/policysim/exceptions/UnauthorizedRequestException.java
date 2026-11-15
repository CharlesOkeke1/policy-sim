package org.cokeke.policysim.exceptions;

public class UnauthorizedRequestException extends RuntimeException {
    public UnauthorizedRequestException(String msg) { super(msg); }
}
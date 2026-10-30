package org.cokeke.policysim.exceptions;

public class AppSessionExpiredException extends RuntimeException {
    public AppSessionExpiredException(String msg) { super(msg); }
}

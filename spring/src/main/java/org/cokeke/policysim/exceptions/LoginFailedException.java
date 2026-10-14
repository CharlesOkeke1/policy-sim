package org.cokeke.policysim.exceptions;

public class LoginFailedException extends InvalidCredentialsException {
    public LoginFailedException(String msg) { super(msg); }
}
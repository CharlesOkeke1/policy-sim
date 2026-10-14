package org.cokeke.policysim.exceptions;

public class ProcessFailureException extends RuntimeException {
    public ProcessFailureException(String message) {
        super(message);
    }
}
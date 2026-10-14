package org.cokeke.policysim.common;

import org.cokeke.policysim.exceptions.GameNotFoundException;
import org.cokeke.policysim.exceptions.LoginFailedException;
import org.cokeke.policysim.exceptions.TokenExpiredException;
import org.cokeke.policysim.exceptions.UserAlreadyExistsException;
import org.cokeke.policysim.exceptions.UserNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(UserNotFoundException.class)
    public ProblemDetail handleUserNotFound(UserNotFoundException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.NOT_FOUND, 
            ex.getMessage()
        );
        
        problemDetail.setTitle("User Not Found");
        // problemDetail.setType(URI.create("https://policysim.com"));
        // problemDetail.setInstance(URI.create("/api/users/login"));
        
        return problemDetail;
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ProblemDetail handleUserAlreadyExists(UserAlreadyExistsException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.CONFLICT, 
            ex.getMessage()
        );
        
        problemDetail.setTitle("User Already Exists");
        // problemDetail.setType(URI.create("https://policysim.com"));
        // problemDetail.setInstance(URI.create("/api/users/new"));
        
        return problemDetail;
    }


    @ExceptionHandler(GameNotFoundException.class)
    public ProblemDetail handleGameNotFound(GameNotFoundException ex) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(
            HttpStatus.NOT_FOUND, 
            ex.getMessage()
        );
        
        problemDetail.setTitle("Game Not Found");
        // problemDetail.setType(URI.create("https://policysim.com"));
        // problemDetail.setInstance(URI.create("/api/games"));
        
        return problemDetail;
    }

    @ExceptionHandler(LoginFailedException.class)
    public ProblemDetail handleLoginFailed(RuntimeException ex) {
        log.warn("Login attempt failed", ex.getMessage());
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(
            HttpStatus.UNAUTHORIZED,
            "Invalid email or password"
        );

        detail.setTitle("Login Failed");
        // detail.setType(URI.create("https://policysim.com"));
        // detail.setInstance(URI.create("/api/users/login"));

        return detail;
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ProblemDetail handleTokenExpired(TokenExpiredException ex) {
        ProblemDetail detail = ProblemDetail.forStatusAndDetail(
            HttpStatusCode.valueOf(500),
            "Token has expired"
        );

        detail.setTitle("Token Expired");
        return detail;
    }
}

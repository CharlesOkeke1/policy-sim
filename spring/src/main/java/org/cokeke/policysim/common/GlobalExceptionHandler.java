package org.cokeke.policysim.common;

import org.cokeke.policysim.exceptions.GameNotFoundException;
import org.cokeke.policysim.exceptions.LoginFailedException;
import org.cokeke.policysim.exceptions.TokenExpiredException;
import org.cokeke.policysim.exceptions.UserAlreadyExistsException;
import org.cokeke.policysim.exceptions.UserNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity
            .status(HttpStatus.valueOf(400))
            .body(ApiResponse.error(400, ex.getMessage()));
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<String>>  handleUserAlreadyExists(UserAlreadyExistsException ex) {        
        return ResponseEntity
            .status(HttpStatus.valueOf(400))
            .body(ApiResponse.error(400, ex.getMessage()));
    }


    @ExceptionHandler(GameNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleGameNotFound(GameNotFoundException ex) {
        return ResponseEntity
            .status(HttpStatus.valueOf(400))
            .body(ApiResponse.error(400, ex.getMessage()));
    }

    @ExceptionHandler(LoginFailedException.class)
    public ResponseEntity<ApiResponse<String>> handleLoginFailed(RuntimeException ex) {
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(ApiResponse.error(401, ex.getMessage()));
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ApiResponse<String>> handleTokenExpired(TokenExpiredException ex) {
        return ResponseEntity
            .status(HttpStatus.valueOf(500))
            .body(ApiResponse.error(500, ex.getMessage()));
    }
}

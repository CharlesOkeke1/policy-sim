package org.cokeke.policysim.common;

import org.cokeke.policysim.exceptions.AppSessionAlreadyExistsException;
import org.cokeke.policysim.exceptions.AppSessionExpiredException;
import org.cokeke.policysim.exceptions.AppSessionNotFoundException;
import org.cokeke.policysim.exceptions.EmailSendException;
import org.cokeke.policysim.exceptions.GameNotFoundException;
import org.cokeke.policysim.exceptions.InvalidContractException;
import org.cokeke.policysim.exceptions.InvalidRequestException;
import org.cokeke.policysim.exceptions.InvalidTokenException;
import org.cokeke.policysim.exceptions.LoginFailedException;
import org.cokeke.policysim.exceptions.RoundNotFoundException;
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
            .status(HttpStatus.valueOf(404))
            .body(ApiResponse.error(404, ex.getMessage(), ErrorCode.RESOURCE_NOT_FOUND));
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<String>>  handleUserAlreadyExists(UserAlreadyExistsException ex) {        
        return ResponseEntity
            .status(HttpStatus.valueOf(409))
            .body(ApiResponse.error(409, ex.getMessage(), ErrorCode.RESOURCE_ALREADY_EXISTS));
    }


    @ExceptionHandler(GameNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleGameNotFound(GameNotFoundException ex) {
        return ResponseEntity
            .status(HttpStatus.valueOf(404))
            .body(ApiResponse.error(404, ex.getMessage(), ErrorCode.RESOURCE_NOT_FOUND));
    }

    @ExceptionHandler(LoginFailedException.class)
    public ResponseEntity<ApiResponse<String>> handleLoginFailed(RuntimeException ex) {
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(ApiResponse.error(401, ex.getMessage(), ErrorCode.RESOURCE_REQUIRES_AUTHORIZATION));
    }

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ApiResponse<String>> handleTokenExpired(TokenExpiredException ex) {
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(ApiResponse.error(401, ex.getMessage(), ErrorCode.RESOURCE_EXPIRED));
    }

    @ExceptionHandler(AppSessionNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleSessionNotFound(AppSessionNotFoundException ex) {
        return ResponseEntity
            .status(HttpStatus.valueOf(404))
            .body(ApiResponse.error(404, ex.getMessage(), ErrorCode.RESOURCE_NOT_FOUND));
    }

    @ExceptionHandler(AppSessionExpiredException.class)
    public ResponseEntity<ApiResponse<String>> handleSessionExpired(AppSessionExpiredException ex) {
        return ResponseEntity
            .status(HttpStatus.UNAUTHORIZED)
            .body(ApiResponse.error(401, ex.getMessage(), ErrorCode.RESOURCE_EXPIRED));
    }

    @ExceptionHandler(AppSessionAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<String>> handleSessionAlreadyExists(AppSessionAlreadyExistsException ex) {
        return ResponseEntity
            .status(HttpStatus.CONFLICT)
            .body(ApiResponse.error(409, ex.getMessage(), ErrorCode.RESOURCE_ALREADY_EXISTS));
    }

    @ExceptionHandler(RoundNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleRoundNotFound(RoundNotFoundException ex) {
        return ResponseEntity
            .status(HttpStatus.valueOf(404))
            .body(ApiResponse.error(404, ex.getMessage(), ErrorCode.RESOURCE_NOT_FOUND));
    }

    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ApiResponse<String>> handleInvalidToken(InvalidTokenException ex) {
        return ResponseEntity
            .status(HttpStatus.valueOf(404))
            .body(ApiResponse.error(404, ex.getMessage(), ErrorCode.RESOURCE_NOT_FOUND));
    }

    @ExceptionHandler(InvalidContractException.class)
    public ResponseEntity<ApiResponse<String>> handleInvalidContract(InvalidContractException ex) {
        return ResponseEntity
            .status(HttpStatus.valueOf(400))
            .body(ApiResponse.error(400, ex.getMessage(), ErrorCode.INVALID_REQUEST));
    }
    
    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ApiResponse<String>> handleInvalidRequest(InvalidRequestException ex) {
        return ResponseEntity
            .status(HttpStatus.valueOf(400))
            .body(ApiResponse.error(400, ex.getMessage(), ErrorCode.INVALID_REQUEST));
    }

    @ExceptionHandler(EmailSendException.class)
    public ResponseEntity<ApiResponse<String>> handleInvalidRequest(EmailSendException ex) {
        return ResponseEntity
            .status(HttpStatus.valueOf(500))
            .body(ApiResponse.error(500, ex.getMessage(), ErrorCode.INTERNAL_SERVER_ERROR));
    }
}
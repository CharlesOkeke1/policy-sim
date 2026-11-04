package org.cokeke.policysim.controllers;

import java.util.List;

import org.cokeke.policysim.common.ApiResponse;
import org.cokeke.policysim.models.Token;
import org.cokeke.policysim.services.TokenService;
import org.springframework.http.ResponseEntity;
import io.swagger.v3.oas.annotations.Hidden;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api")
@Tag(name = "Tokens", description = "Handles all token operations for user authentication")

public class TokenController {
    private final TokenService tokenService;

    public TokenController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @Hidden
    @GetMapping("/tokens")
    public ResponseEntity<ApiResponse<List<Token>>> getAllTokens() {
        List<Token> tokens = tokenService.getAllTokens();

        return ResponseEntity.ok(ApiResponse.ok(tokens));
    }

    @Hidden
    @DeleteMapping("/tokens")
    public ResponseEntity<ApiResponse<String>> deleteAllTokens() {
        tokenService.deleteAllTokens();

        return ResponseEntity.ok(ApiResponse.ok("All tokens successfully deleted."));
    }

    @GetMapping("/tokens/{userId}")
    public ResponseEntity<ApiResponse<String>> getUserToken(@PathVariable Long userId) {
        Token t = tokenService.findByUserId(userId);
        return ResponseEntity.ok(ApiResponse.ok(t.getToken())); 
    }

}
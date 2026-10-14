package org.cokeke.policysim.controllers;

import java.util.List;

import org.cokeke.policysim.common.ApiResponse;
import org.cokeke.policysim.models.Token;
import org.cokeke.policysim.services.TokenService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TokenController {
    private final TokenService tokenService;

    public TokenController(TokenService tokenService) {
        this.tokenService = tokenService;
    }

    @GetMapping("/api/tokens")
    public ResponseEntity<ApiResponse<List<Token>>> getAllTokens() {
        List<Token> tokens = tokenService.getAllTokens();

        return ResponseEntity.ok(ApiResponse.ok(tokens));
    }

    @DeleteMapping("/api/tokens")
    public ResponseEntity<ApiResponse<String>> deleteAllTokens() {
        tokenService.deleteAllTokens();

        return ResponseEntity.ok(ApiResponse.ok("All tokens successfully deleted."));
    }

}
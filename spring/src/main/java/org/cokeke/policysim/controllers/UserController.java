package org.cokeke.policysim.controllers;

import java.net.URI;
import java.util.List;

import org.cokeke.policysim.common.ApiResponse;
import org.cokeke.policysim.common.Environment;
import org.cokeke.policysim.common.TokenGenerator;
import org.cokeke.policysim.models.Token;
import org.cokeke.policysim.models.User;
import org.cokeke.policysim.requests.ChangePasswordRequest;
import org.cokeke.policysim.requests.CreateUserRequest;
import org.cokeke.policysim.requests.LoginRequest;
import org.cokeke.policysim.responses.ChangePasswordResponse;
import org.cokeke.policysim.responses.UserResponse;
import org.cokeke.policysim.services.TokenService;
import org.cokeke.policysim.services.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {
    private final TokenGenerator tokenGenerator;
    private final UserService userSvc;
    private final TokenService tokenSvc;

    public UserController(TokenGenerator tokenGenerator, UserService userSvc, TokenService tokenSvc) {
        this.tokenGenerator = tokenGenerator;
        this.userSvc = userSvc;
        this.tokenSvc = tokenSvc;
    }


    @PostMapping(Environment.loginCall)
    public ResponseEntity<ApiResponse<UserResponse>> loginUser(@RequestBody LoginRequest req) {
        User u = userSvc.loginUser(req.email(), req.password());
        Token tokenObj = new Token(tokenGenerator.generateTokenString(), u.getId());
        tokenSvc.saveToken(tokenObj);

        ResponseCookie cookie = ResponseCookie.from("auth_token", tokenObj.getToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(tokenObj.getExpirationTime().getSecond())
                .sameSite("Strict")
                .build();

        UserResponse userResponse = new UserResponse(u.getId(), u.getEmail(), u.getName(), tokenObj.getToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(ApiResponse.ok(userResponse));
    }

    @PostMapping(Environment.signUpCall)
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@RequestBody CreateUserRequest req) {
        User u = userSvc.createUser(req.email(), req.password(), req.name());
        UserResponse userResponse = new UserResponse(u.getId(), u.getEmail(), u.getName(), null);
        ApiResponse<UserResponse> body = ApiResponse.ok(201, userResponse);
        return ResponseEntity
                .created(URI.create("/api/users/" + userResponse.id()))
                .body(body);
    }

    @GetMapping("/api/users")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        List<User> users = userSvc.getAllUsers();
        List<UserResponse> userResponses = users.stream()
                .map(u -> new UserResponse(u.getId(), u.getEmail(), u.getName(), null))
                .toList();
        return ResponseEntity.ok(ApiResponse.ok(userResponses));
    }

    @GetMapping("/api/users/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUser(@PathVariable Long id) {
        User u = userSvc.getUserById(id);
        UserResponse resp = new UserResponse(u.getId(), u.getEmail(), u.getName(), null);
        return ResponseEntity.ok(ApiResponse.ok(resp));
    }


    @PutMapping("/api/users/{id}/password")
    public ResponseEntity<ApiResponse<ChangePasswordResponse>> changePassword(@PathVariable Long id, @RequestBody ChangePasswordRequest request) {
        ChangePasswordResponse response = new ChangePasswordResponse(userSvc.changePassword(id, request.oldPassword(), request.newPassword()));
        return ResponseEntity.ok(ApiResponse.ok(response));
    }
}
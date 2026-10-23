package org.cokeke.policysim.controllers;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

import org.cokeke.policysim.common.ApiResponse;
import org.cokeke.policysim.common.AuthContextHolder;
import org.cokeke.policysim.common.Environment;
import org.cokeke.policysim.common.TokenGenerator;
import org.cokeke.policysim.dtos.LoginUserServiceRes;
import org.cokeke.policysim.models.AppSession;
import org.cokeke.policysim.models.Token;
import org.cokeke.policysim.models.User;
import org.cokeke.policysim.requests.ChangePasswordRequest;
import org.cokeke.policysim.requests.CreateUserRequest;
import org.cokeke.policysim.requests.LoginRequest;
import org.cokeke.policysim.requests.LogoutRequest;
import org.cokeke.policysim.requests.NewTokenRequest;
import org.cokeke.policysim.responses.ChangePasswordResponse;
import org.cokeke.policysim.responses.LoginResponse;
import org.cokeke.policysim.responses.NewTokenResponse;
import org.cokeke.policysim.responses.UserResponse;
import org.cokeke.policysim.services.AppSessionService;
import org.cokeke.policysim.services.TokenService;
import org.cokeke.policysim.services.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@Tag(name = "Users", description = "Handles all user authentication endpoints, along with some user data")
public class UserController {
    private final UserService userSvc;
    private final TokenService tokenSvc;
    private final AppSessionService sessionSvc;
    private final AuthContextHolder holder;

    public UserController(UserService userSvc, TokenService tokenSvc, 
        AuthContextHolder holder, AppSessionService sessionSvc) {
        this.userSvc = userSvc;
        this.tokenSvc = tokenSvc;
        this.holder = holder;
        this.sessionSvc = sessionSvc;
    }

    @PostMapping(Environment.loginCall)
    public ResponseEntity<ApiResponse<LoginResponse>> loginUser(@RequestBody LoginRequest req) {
        LoginUserServiceRes res = userSvc.loginUser(req.email(), req.password(), req.ipAddress(), req.deviceInfo());
        User u = res.user();
        AppSession session = res.session();

        String tokenString = TokenGenerator.generateTokenString();
        Token tokenObj = new Token(tokenString, u.getId());
        tokenSvc.saveToken(tokenObj);

        ResponseCookie cookie = ResponseCookie.from("auth_token", tokenObj.getToken())
                .httpOnly(true)
                .secure(true)
                .path("/")
                .maxAge(tokenObj.getExpirationTime().getSecond())
                .sameSite("Strict")
                .build();

        LocalDateTime expTime = tokenSvc.getTokenExpiryTime(tokenString);

        LoginResponse userResponse = new LoginResponse(u.getId(), u.getEmail(),
        u.getUserName(), tokenString, expTime, session.getRefreshToken(), session.getCreatedAt(), session.getId());

        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, cookie.toString())
            .body(ApiResponse.ok(userResponse));
    }

    @PostMapping("/api/users/logout")
    public ResponseEntity<ApiResponse<String>> logoutUser(@RequestBody LogoutRequest req) {
        userSvc.logoutUser(req.token(), req.refreshToken());
        return ResponseEntity.ok(ApiResponse.ok("Logout successful"));
    }

    @PostMapping("/api/users")
    public ResponseEntity<ApiResponse<UserResponse>> createUser(@RequestBody CreateUserRequest req) {
        User u = userSvc.createUser(req.username(), req.email(), req.password());
        UserResponse userResponse = new UserResponse(u.getId(), u.getEmail(), u.getUserName(), null);
        ApiResponse<UserResponse> body = ApiResponse.ok(201, userResponse);
        return ResponseEntity
                .created(URI.create("/api/users/" + userResponse.id()))
                .body(body);
    }

    @GetMapping("/api/users")
    public ResponseEntity<ApiResponse<List<UserResponse>>> getAllUsers() {
        List<User> users = userSvc.getAllUsers();
        List<UserResponse> userResponses = users.stream()
                .map(u -> new UserResponse(u.getId(), u.getEmail(), u.getUserName(), null))
                .toList();
        return ResponseEntity.ok(ApiResponse.ok(userResponses));
    }

    @GetMapping("/api/usernames")
    public ResponseEntity<ApiResponse<List<String>>> getAllUserNames() {
        List<String> usernames = userSvc.getAllUserNames();
        return ResponseEntity.ok(ApiResponse.ok(usernames));
    }

    @GetMapping("/api/users/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getUser(@PathVariable Long id) {
        User u = userSvc.getUserById(id);
        UserResponse resp = new UserResponse(u.getId(), u.getEmail(), u.getUserName(), null);
        return ResponseEntity.ok(ApiResponse.ok(resp));
    }

    @PutMapping("/api/users/change-password")
    public ResponseEntity<ApiResponse<ChangePasswordResponse>> changePassword(@RequestBody ChangePasswordRequest request) {
        ChangePasswordResponse response = new ChangePasswordResponse(userSvc.changePassword(holder.getCurrentUserId(), request.oldPassword(), request.newPassword()));
        return ResponseEntity.ok(ApiResponse.ok(response));
    }

    @PostMapping("/api/users/refresh")
    public ResponseEntity<ApiResponse<NewTokenResponse>> getNewAuthToken(@RequestBody NewTokenRequest req) {
        Token newToken = sessionSvc.getNewAuthToken(req.refreshToken());
        return ResponseEntity.ok(ApiResponse.ok(new NewTokenResponse(newToken.getToken(), req.refreshToken())));
    }

    @Hidden
    @DeleteMapping("/api/users/{id}")
    public ResponseEntity<ApiResponse<String>> deleteAccount(@PathVariable Long id) {
        userSvc.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.ok("Account deleted successfully"));
    }
}
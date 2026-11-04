package org.cokeke.policysim.services;

import java.util.List;

import org.cokeke.policysim.dtos.LoginUserServiceRes;
import org.cokeke.policysim.exceptions.AppSessionExpiredException;
import org.cokeke.policysim.exceptions.InvalidCredentialsException;
import org.cokeke.policysim.exceptions.LoginFailedException;
import org.cokeke.policysim.exceptions.UserAlreadyExistsException;
import org.cokeke.policysim.exceptions.UserNotFoundException;
import org.cokeke.policysim.models.AppSession;
import org.cokeke.policysim.models.User;
import org.cokeke.policysim.repos.UserRepository;
import org.cokeke.policysim.responses.UserResponse;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.CacheEvict;


@Service
public class UserService {
    private final UserRepository repo;
    private final PasswordEncoder encoder;
    private final AppSessionService sessionSvc;
    private final TokenService tokenSvc;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
        AppSessionService sessionSvc, TokenService tokenSvc
    ) {
        this.repo = userRepository;
        this.encoder = passwordEncoder;
        this.sessionSvc = sessionSvc;
        this.tokenSvc = tokenSvc;
    }

    public User createUser(String name, String email, String password) {
        User existingUser = repo.findByEmail(email).orElse(null);

        if (existingUser != null) {
            throw new UserAlreadyExistsException("User with this username already exists");
        }

        String passwordHash = encoder.encode(password.trim());
        User user = new User(email.trim().toLowerCase(), passwordHash, name);
        return repo.save(user);
    }

    @Cacheable (value = "user", key="#user.id")
    public User getUserById(Long id) {
        User user = this.getUser(id, "User with ID: " + id + " does not exist");
        return user;
    }

    @Cacheable (value = "user", key="#user.email")
    public User getUserByEmail(String email) {
        User user = this.getUser(email);
        return user;
    }

    public List<User> getAllUsers() {
        return repo.findAll();
    }

    @Cacheable (value = "user", key="#user.id")
    public User updateUser(Long id, String email, String name) {
        User user = repo.findById(id).orElse(null);
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }

        if (email != null && !email.isEmpty()) {
            user.setEmail(email);
        }
        if (name != null && !name.isEmpty()) {
            user.setUserName(name);
        }

        return repo.save(user);
    }

    public String changePassword(Long id, String currPassword, String newPassword) {
        User user = this.getUser(id, "User with ID: " + id + " does not exist");

        if (!encoder.matches(currPassword, user.getHash())) {
            throw new InvalidCredentialsException("Invalid credentials");
        }

        String passwordHash = encoder.encode(newPassword);
        user.setHash(passwordHash);
        repo.save(user);
        return "Password changed successfully";
    }

    @CacheEvict (value = "user", key="#user.id")
    public void deleteUser(Long id) {
        User user = getUser(id);
        repo.deleteById(user.getId());
    }

    public LoginUserServiceRes loginUser(String email, String password, String ipAddress, String deviceInfo) {
        // Find user by email first, if not found, try finding by username
        User user = repo.findByEmail(email.toLowerCase()).orElse(null);
        if (user == null) {
            user = repo.findByUsername(email.toLowerCase()).orElse(null);
        }

        if (user == null || !encoder.matches(password, user.getHash())) {
            throw new LoginFailedException("Invalid username or password");
        }      

        AppSession session = sessionSvc.createNewSession(user.getId(), ipAddress, deviceInfo);
        return new LoginUserServiceRes(user, session);
    }

    public void logoutUser(String token, String refreshToken) {
        User user = tokenSvc.getAuthenticatedUser(token);
        tokenSvc.inalidateToken(token);
        sessionSvc.revokeSession(refreshToken);
    }

    private User getUser(String email) {
        User user = repo.findByEmail(email).orElseThrow(() -> 
        new UserNotFoundException("User with Email: " + email + " does not exist"));

        return user;
    }

    private User getUser(Long id) {
        User user = repo.findById(id).orElseThrow(() -> 
        new UserNotFoundException("User with ID: " + id + " does not exist"));

        return user;
    }

    private User getUser(Long id, String failureMsg) {
        User user = repo.findById(id).orElseThrow(() -> 
        new UserNotFoundException(failureMsg));

        return user;
    }

    public UserResponse userToUserResponse(User u) {
        return new UserResponse(u.getId(), u.getEmail(), u.getUserName(), null);
    }

    @Cacheable (value = "usernames", key = "'allUsernames'")
    public List<String> getAllUserNames() {
        List<String> usernames = repo.findAll().stream().map(user -> user.getUserName()).toList();
        return usernames;
    }

    public LoginUserServiceRes me(String refreshToken) {
        AppSession session = sessionSvc.getBySessionRefreshToken(refreshToken);

        if (session.isExpiredOrRevoked()) {
            throw new AppSessionExpiredException("Session has expired");
        }

        User u = getUserById(session.getUserId());        
        return new LoginUserServiceRes(u, session);
    }
}
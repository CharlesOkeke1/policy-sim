package org.cokeke.policysim.services;

import java.util.List;
import java.time.LocalDateTime;
import java.time.Instant;

import org.cokeke.policysim.components.ServerStatus;
import org.cokeke.policysim.dtos.LoginUserServiceRes;
import org.cokeke.policysim.exceptions.AppSessionExpiredException;
import org.cokeke.policysim.exceptions.InsufficientPermissionsException;
import org.cokeke.policysim.exceptions.InvalidCredentialsException;
import org.cokeke.policysim.exceptions.LoginFailedException;
import org.cokeke.policysim.exceptions.UserAlreadyExistsException;
import org.cokeke.policysim.exceptions.UserNotFoundException;
import org.cokeke.policysim.exceptions.InvalidRequestException;
import org.cokeke.policysim.external.SendEmail;
import org.cokeke.policysim.models.AppSession;
import org.cokeke.policysim.models.Email;
import org.cokeke.policysim.models.Token;
import org.cokeke.policysim.common.EnvConfig;
import org.cokeke.policysim.models.User;
import org.cokeke.policysim.models.Role;
import org.cokeke.policysim.repos.EmailRepository;
import org.cokeke.policysim.repos.UserRepository;
import org.cokeke.policysim.responses.ServerStatusResponse;
import org.cokeke.policysim.responses.UserResponse;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
public class UserService {
    private final UserRepository repo;
    private final PasswordEncoder encoder;
    private final AppSessionService sessionSvc;
    private final TokenService tokenSvc;
    private final EmailRepository emailRepo;
    private final ServerStatus serverStatus;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder,
        AppSessionService sessionSvc, TokenService tokenSvc, EmailRepository emailRepo,
        ServerStatus serverStatus
    ) {
        this.repo = userRepository;
        this.encoder = passwordEncoder;
        this.sessionSvc = sessionSvc;
        this.tokenSvc = tokenSvc;
        this.emailRepo = emailRepo;
        this.serverStatus = serverStatus;
    }

    public User createUser(String name, String email, String password) {
        User existingUser = repo.findByEmail(email).orElse(null);

        if (existingUser != null) {
            throw new UserAlreadyExistsException("User with this username already exists");
        }

        String passwordHash = encoder.encode(password.trim());
        User user = new User(email.trim().toLowerCase(), passwordHash, name);

        if (email.equals("me@charlesokeke.com")) {
            user.setUserRole(Role.SUPER_USER);
        }
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
        user.setLastLogin(Instant.now());
        return new LoginUserServiceRes(user, session);
    }

    public boolean logoutUser(String refreshToken) {
        AppSession session = sessionSvc.getBySessionRefreshToken(refreshToken);
        Token token = tokenSvc.findByUserId(session.getUserId());
        User user = tokenSvc.getAuthenticatedUser(token.getToken());
        if (user == null) return false;
        tokenSvc.inalidateToken(token.getToken());
        sessionSvc.revokeSession(refreshToken);
        return true;
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

    public Email contactSupport(Long userId, String senderEmail, 
        String title, String subject, String content) {
        SendEmail.contactSupoort(senderEmail, title, subject, content);
        Email newEmail = new Email(senderEmail, title, subject, content, "support");

        if (userId != null) {
            newEmail.setUserId(userId);
        }
        return emailRepo.save(newEmail);
    }

    public ServerStatusResponse getServerStatus() {
        return new ServerStatusResponse(serverStatus.isOnline(), serverStatus.getServerReadyTime());
    }

    public boolean isUserSuperUser(Long userId, String secret, boolean status) {
        User user = getUser(userId);

        if (user == null) {
            throw new UserNotFoundException("User with ID: " + userId + " does not exist");
        }

        if (!user.isSuperUser()) throw new InsufficientPermissionsException("You lack the appropriate permissions to use this function");

        String envSecret = EnvConfig.get("SUPER_USER_TOKEN");
        if (!envSecret.equals(secret)) throw new InsufficientPermissionsException("Super user authenticated access denied");

        return true;
    }

    public String setServerStatus(Long userId, String secret, boolean status, LocalDateTime readyTime) {
        if (isUserSuperUser(userId, secret, status)) {
            serverStatus.setOnline(status);

            if (!readyTime.isAfter(LocalDateTime.now())) {
                throw new InvalidRequestException("Expected server uptime is invalid");
            }
            
            serverStatus.setServerReadyTime(readyTime);
            return "Server status successfully updated";
        }
        return "Failed to update server status";
    }
}
package org.cokeke.policysim.services;

import java.util.List;

import org.cokeke.policysim.common.TokenGenerator;
import org.cokeke.policysim.exceptions.InvalidCredentialsException;
import org.cokeke.policysim.exceptions.LoginFailedException;
import org.cokeke.policysim.exceptions.UserAlreadyExistsException;
import org.cokeke.policysim.exceptions.UserNotFoundException;
import org.cokeke.policysim.models.User;
import org.cokeke.policysim.repos.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    private final UserRepository repo;
    private final PasswordEncoder encoder;
    private final TokenGenerator tokenGenerator;

    @Autowired
    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, TokenGenerator tokenGenerator) {
        this.repo = userRepository;
        this.encoder = passwordEncoder;
        this.tokenGenerator = tokenGenerator;
    }

    public User createUser(String email, String password, String name) {
        User existingUser = repo.findByEmail(email).orElse(null);

        if (existingUser != null) {
            throw new UserAlreadyExistsException("User with this email already exists");
        }

        if (email == null) throw new IllegalArgumentException("Email is required");
        if (name == null) throw new IllegalArgumentException("Name is required");
        if (password == null) throw new IllegalArgumentException("Password is required");

        String passwordHash = encoder.encode(password.trim());
        User user = new User(email.trim().toLowerCase(), passwordHash, name);
        return repo.save(user);
    }

    public User getUserById(Long id) {
        User user = this.getUser(id, "User with ID: " + id + " does not exist");
        return user;
    }

    public User getUserByEmail(String email) {
        User user = this.getUser(email);
        return user;
    }

    public List<User> getAllUsers() {
        return repo.findAll();
    }

    public User updateUser(Long id, String email, String name) {
        User user = repo.findById(id).orElse(null);
        if (user == null) {
            throw new UserNotFoundException("User not found");
        }

        if (email != null && !email.isEmpty()) {
            user.setEmail(email);
        }
        if (name != null && !name.isEmpty()) {
            user.setName(name);
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

    public void deleteUser(Long id) {
        User user = getUser(id);
        repo.deleteById(id);
    }

    public User loginUser(String email, String password) {
        User user = repo.findByEmail(email).orElse(null);

        if (!encoder.matches(password, user.getHash()) || user == null) {
            throw new LoginFailedException("Invalid username or password");
        }      

        return user;
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

}
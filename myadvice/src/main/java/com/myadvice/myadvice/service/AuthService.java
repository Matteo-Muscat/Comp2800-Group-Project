package com.myadvice.myadvice.service;

import com.myadvice.myadvice.entity.User;
import com.myadvice.myadvice.entity.SigninRequest;
import com.myadvice.myadvice.repository.UserRepository;
import com.myadvice.myadvice.repository.SigninRequestRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final SigninRequestRepository signinRequestRepository;

    public AuthService(UserRepository userRepository, SigninRequestRepository signinRequestRepository) {
        this.userRepository = userRepository;
        this.signinRequestRepository = signinRequestRepository;
    }

    // Login - check email and password
    public User login(String email, String password) {
        Optional<User> user = userRepository.findByEmailAndPassword(email, password);
        if (user.isPresent() && user.get().getStatus() == User.Status.APPROVED) {
            return user.get();
        }
        return null;
    }

    // Register a new user
    public User register(String name, String email, String password, User.Role role) {
        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setRole(role);
        user.setStatus(User.Status.PENDING);
        User savedUser = userRepository.save(user);

        // Create a signin request for admin to approve
        SigninRequest request = new SigninRequest();
        request.setUserId(savedUser.getUserId());
        request.setDecision(SigninRequest.Decision.PENDING);
        signinRequestRepository.save(request);

        return savedUser;
    }

    // Get user by ID
    public User getUserById(Integer userId) {
        return userRepository.findById(userId).orElse(null);
    }
}

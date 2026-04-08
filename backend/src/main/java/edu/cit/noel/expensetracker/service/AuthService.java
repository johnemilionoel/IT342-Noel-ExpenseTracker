package edu.cit.noel.expensetracker.service;

import edu.cit.noel.expensetracker.dto.LoginRequest;
import edu.cit.noel.expensetracker.dto.RegisterRequest;
import edu.cit.noel.expensetracker.entity.User;
import edu.cit.noel.expensetracker.repository.UserRepository;
import edu.cit.noel.expensetracker.validation.RegisterValidationStrategy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RegisterValidationStrategy registerValidator;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, RegisterValidationStrategy registerValidator) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.registerValidator = registerValidator;
    }

    public String register(RegisterRequest request) {
        registerValidator.validate(request);
        User user = new User();
        user.setFirstname(request.getFirstname());
        user.setLastname(request.getLastname());
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setCreatedAt(LocalDateTime.now());
        userRepository.save(user);
        return "User registered successfully";
    }

    public String login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail()).orElse(null);
        if (user == null) return "Invalid credentials";
        if (passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) return "Login successful";
        return "Invalid credentials";
    }
}

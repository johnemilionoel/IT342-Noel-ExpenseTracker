package edu.cit.noel.expensetracker.controller;

import edu.cit.noel.expensetracker.dto.LoginRequest;
import edu.cit.noel.expensetracker.dto.RegisterRequest;
import edu.cit.noel.expensetracker.entity.User;
import edu.cit.noel.expensetracker.repository.UserRepository;
import edu.cit.noel.expensetracker.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final UserRepository userRepository;

    public AuthController(AuthService authService, UserRepository userRepository) {
        this.authService = authService;
        this.userRepository = userRepository;
    }

    @PostMapping("/register")
    public String register(@RequestBody RegisterRequest request) {
        return authService.register(request);
    }

    @PostMapping("/login")
    public String login(@RequestBody LoginRequest request) {
        return authService.login(request);
    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserByEmail(@RequestParam String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body(Map.of("error", "User not found"));
        }
        Map<String, Object> data = new HashMap<>();
        data.put("id", user.getId());
        data.put("email", user.getEmail());
        data.put("firstname", user.getFirstname());
        data.put("lastname", user.getLastname());
        return ResponseEntity.ok(data);
    }
}

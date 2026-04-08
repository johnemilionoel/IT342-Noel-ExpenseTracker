package edu.cit.noel.expensetracker.controller;

import edu.cit.noel.expensetracker.dto.ApiResponse;
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
    public AuthController(AuthService as, UserRepository ur) { this.authService = as; this.userRepository = ur; }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<String>> register(@RequestBody RegisterRequest request) {
        String result = authService.register(request);
        return ResponseEntity.ok(ApiResponse.success(result, result));
    }
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<String>> login(@RequestBody LoginRequest request) {
        String result = authService.login(request);
        if (result.contains("successful")) return ResponseEntity.ok(ApiResponse.success(result, result));
        return ResponseEntity.badRequest().body(ApiResponse.error(result));
    }
    @GetMapping("/user")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getUserByEmail(@RequestParam String email) {
        User user = userRepository.findByEmail(email).orElse(null);
        if (user == null) return ResponseEntity.badRequest().body(ApiResponse.error("User not found"));
        Map<String, Object> data = new HashMap<>();
        data.put("id", user.getId()); data.put("email", user.getEmail());
        data.put("firstname", user.getFirstname()); data.put("lastname", user.getLastname());
        return ResponseEntity.ok(ApiResponse.success(data, "User found"));
    }
}

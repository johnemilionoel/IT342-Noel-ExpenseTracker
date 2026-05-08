package edu.cit.noel.expensetracker.auth;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private RegisterValidationStrategy registerValidator;
    @InjectMocks private AuthService authService;

    private RegisterRequest registerRequest;
    private LoginRequest loginRequest;
    private User testUser;

    @BeforeEach
    void setUp() {
        registerRequest = new RegisterRequest();
        registerRequest.setFirstname("John");
        registerRequest.setLastname("Noel");
        registerRequest.setEmail("john@test.com");
        registerRequest.setPassword("password123");

        loginRequest = new LoginRequest();
        loginRequest.setEmail("john@test.com");
        loginRequest.setPassword("password123");

        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("john@test.com");
        testUser.setFirstname("John");
        testUser.setLastname("Noel");
        testUser.setPasswordHash("$2a$10$hashedpassword");
    }

    @Test
    @DisplayName("Register with valid data returns success message")
    void register_validData_returnsSuccess() {
        doNothing().when(registerValidator).validate(any());
        when(passwordEncoder.encode("password123")).thenReturn("$2a$10$encoded");
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        String result = authService.register(registerRequest);

        assertEquals("User registered successfully", result);
        verify(userRepository).save(any(User.class));
        verify(registerValidator).validate(registerRequest);
    }

    @Test
    @DisplayName("Login with valid credentials returns success")
    void login_validCredentials_returnsSuccess() {
        when(userRepository.findByEmail("john@test.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", "$2a$10$hashedpassword")).thenReturn(true);

        String result = authService.login(loginRequest);

        assertEquals("Login successful", result);
    }

    @Test
    @DisplayName("Login with wrong password returns invalid")
    void login_wrongPassword_returnsInvalid() {
        when(userRepository.findByEmail("john@test.com")).thenReturn(Optional.of(testUser));
        when(passwordEncoder.matches("password123", "$2a$10$hashedpassword")).thenReturn(false);

        String result = authService.login(loginRequest);

        assertEquals("Invalid credentials", result);
    }

    @Test
    @DisplayName("Login with non-existent email returns invalid")
    void login_nonExistentEmail_returnsInvalid() {
        when(userRepository.findByEmail("john@test.com")).thenReturn(Optional.empty());

        String result = authService.login(loginRequest);

        assertEquals("Invalid credentials", result);
    }

    @Test
    @DisplayName("Register encodes password before saving")
    void register_encodesPassword() {
        doNothing().when(registerValidator).validate(any());
        when(passwordEncoder.encode("password123")).thenReturn("$2a$10$encoded");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

        authService.register(registerRequest);

        verify(passwordEncoder).encode("password123");
    }
}

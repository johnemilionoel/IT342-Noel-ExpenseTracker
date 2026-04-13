package edu.cit.noel.expensetracker.controller;

import edu.cit.noel.expensetracker.entity.User;
import edu.cit.noel.expensetracker.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

@RestController
public class OAuthController {

    private final UserRepository userRepository;

    public OAuthController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/login-success")
    public void loginSuccess(
            @AuthenticationPrincipal OAuth2User oauthUser,
            HttpServletResponse response
    ) throws IOException {

        String email = oauthUser.getAttribute("email");
        String firstname = oauthUser.getAttribute("given_name");
        String lastname = oauthUser.getAttribute("family_name");

        // Create user if not exists (auto-register on first Google login)
        User user;
        if (!userRepository.existsByEmail(email)) {
            user = new User();
            user.setEmail(email);
            user.setFirstname(firstname != null ? firstname : "");
            user.setLastname(lastname != null ? lastname : "");
            user.setPasswordHash("GOOGLE_OAUTH");
            user.setCreatedAt(LocalDateTime.now());
            user = userRepository.save(user);
        } else {
            user = userRepository.findByEmail(email).orElseThrow();
        }

        // Redirect to React frontend with user data as query parameters
        String redirectUrl = String.format(
            "http://localhost:5173/oauth-callback?id=%d&email=%s&firstname=%s&lastname=%s",
            user.getId(),
            URLEncoder.encode(email, StandardCharsets.UTF_8),
            URLEncoder.encode(user.getFirstname(), StandardCharsets.UTF_8),
            URLEncoder.encode(user.getLastname(), StandardCharsets.UTF_8)
        );

        response.sendRedirect(redirectUrl);
    }
}

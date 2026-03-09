package edu.cit.noel.expensetracker.controller;

import edu.cit.noel.expensetracker.entity.User;
import edu.cit.noel.expensetracker.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
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

        if (!userRepository.existsByEmail(email)) {

            User user = new User();
            user.setEmail(email);
            user.setFirstname(firstname);
            user.setLastname(lastname);
            user.setPasswordHash("GOOGLE_AUTH");
            user.setCreatedAt(LocalDateTime.now());

            userRepository.save(user);
        }

        // redirect to React dashboard
        response.sendRedirect("http://localhost:5173/dashboard");
    }
}
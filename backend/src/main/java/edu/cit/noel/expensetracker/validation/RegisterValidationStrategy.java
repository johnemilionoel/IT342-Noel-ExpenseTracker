package edu.cit.noel.expensetracker.validation;

import edu.cit.noel.expensetracker.dto.RegisterRequest;
import edu.cit.noel.expensetracker.exception.ValidationException;
import edu.cit.noel.expensetracker.repository.UserRepository;
import org.springframework.stereotype.Component;

@Component
public class RegisterValidationStrategy implements ValidationStrategy<RegisterRequest> {
    private final UserRepository userRepository;
    public RegisterValidationStrategy(UserRepository userRepository) { this.userRepository = userRepository; }

    @Override
    public void validate(RegisterRequest r) throws ValidationException {
        if (r.getEmail() == null || r.getEmail().isBlank()) throw new ValidationException("Email is required");
        if (r.getPassword() == null || r.getPassword().length() < 6) throw new ValidationException("Password must be at least 6 characters");
        if (r.getFirstname() == null || r.getFirstname().isBlank()) throw new ValidationException("First name is required");
        if (r.getLastname() == null || r.getLastname().isBlank()) throw new ValidationException("Last name is required");
        if (userRepository.existsByEmail(r.getEmail())) throw new ValidationException("Email already registered");
    }
}

package edu.cit.noel.expensetracker.expense;

import edu.cit.noel.expensetracker.common.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class ExpenseValidationStrategyTest {

    private ExpenseValidationStrategy validator;

    @BeforeEach
    void setUp() {
        validator = new ExpenseValidationStrategy();
    }

    @Test
    @DisplayName("Valid expense request passes validation")
    void validate_validRequest_passes() {
        ExpenseRequest request = new ExpenseRequest();
        request.setAmount(new BigDecimal("100.00"));
        request.setCategoryId(1L);
        request.setDate("2026-05-08");

        assertDoesNotThrow(() -> validator.validate(request));
    }

    @Test
    @DisplayName("Zero amount fails validation")
    void validate_zeroAmount_throwsException() {
        ExpenseRequest request = new ExpenseRequest();
        request.setAmount(BigDecimal.ZERO);
        request.setCategoryId(1L);
        request.setDate("2026-05-08");

        assertThrows(ValidationException.class, () -> validator.validate(request));
    }

    @Test
    @DisplayName("Negative amount fails validation")
    void validate_negativeAmount_throwsException() {
        ExpenseRequest request = new ExpenseRequest();
        request.setAmount(new BigDecimal("-50.00"));
        request.setCategoryId(1L);
        request.setDate("2026-05-08");

        assertThrows(ValidationException.class, () -> validator.validate(request));
    }

    @Test
    @DisplayName("Null category fails validation")
    void validate_nullCategory_throwsException() {
        ExpenseRequest request = new ExpenseRequest();
        request.setAmount(new BigDecimal("100.00"));
        request.setCategoryId(null);
        request.setDate("2026-05-08");

        assertThrows(ValidationException.class, () -> validator.validate(request));
    }

    @Test
    @DisplayName("Empty date fails validation")
    void validate_emptyDate_throwsException() {
        ExpenseRequest request = new ExpenseRequest();
        request.setAmount(new BigDecimal("100.00"));
        request.setCategoryId(1L);
        request.setDate("");

        assertThrows(ValidationException.class, () -> validator.validate(request));
    }

    @Test
    @DisplayName("Null amount fails validation")
    void validate_nullAmount_throwsException() {
        ExpenseRequest request = new ExpenseRequest();
        request.setAmount(null);
        request.setCategoryId(1L);
        request.setDate("2026-05-08");

        assertThrows(ValidationException.class, () -> validator.validate(request));
    }
}

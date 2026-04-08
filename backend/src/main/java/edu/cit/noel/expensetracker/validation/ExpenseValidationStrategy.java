package edu.cit.noel.expensetracker.validation;

import edu.cit.noel.expensetracker.dto.ExpenseRequest;
import edu.cit.noel.expensetracker.exception.ValidationException;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;

@Component
public class ExpenseValidationStrategy implements ValidationStrategy<ExpenseRequest> {
    @Override
    public void validate(ExpenseRequest request) throws ValidationException {
        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0)
            throw new ValidationException("Amount must be greater than 0");
        if (request.getCategoryId() == null)
            throw new ValidationException("Category is required");
        if (request.getDate() == null || request.getDate().isBlank())
            throw new ValidationException("Date is required");
    }
}

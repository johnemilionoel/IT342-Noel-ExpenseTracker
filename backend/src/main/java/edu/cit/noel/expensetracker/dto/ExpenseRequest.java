package edu.cit.noel.expensetracker.dto;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ExpenseRequest {
    private BigDecimal amount;
    private Long categoryId;
    private String date;
    private String description;
}

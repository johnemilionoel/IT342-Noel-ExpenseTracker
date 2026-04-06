package edu.cit.noel.expensetracker.dto;

import edu.cit.noel.expensetracker.entity.Expense;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class ExpenseResponse {
    private Long id;
    private BigDecimal amount;
    private String category;
    private Long categoryId;
    private String description;
    private String date;
    private String createdAt;

    public static ExpenseResponse from(Expense e) {
        ExpenseResponse r = new ExpenseResponse();
        r.setId(e.getId());
        r.setAmount(e.getAmount());
        r.setCategory(e.getCategory().getName());
        r.setCategoryId(e.getCategory().getId());
        r.setDescription(e.getDescription());
        r.setDate(e.getDate().toString());
        r.setCreatedAt(e.getCreatedAt() != null ? e.getCreatedAt().toString() : null);
        return r;
    }
}

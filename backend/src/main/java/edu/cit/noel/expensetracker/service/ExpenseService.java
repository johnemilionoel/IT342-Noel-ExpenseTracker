package edu.cit.noel.expensetracker.service;

import edu.cit.noel.expensetracker.dto.ExpenseRequest;
import edu.cit.noel.expensetracker.dto.ExpenseResponse;
import edu.cit.noel.expensetracker.entity.Category;
import edu.cit.noel.expensetracker.entity.Expense;
import edu.cit.noel.expensetracker.entity.User;
import edu.cit.noel.expensetracker.repository.CategoryRepository;
import edu.cit.noel.expensetracker.repository.ExpenseRepository;
import edu.cit.noel.expensetracker.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExpenseService {

    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    public ExpenseService(ExpenseRepository expenseRepository,
                          UserRepository userRepository,
                          CategoryRepository categoryRepository) {
        this.expenseRepository = expenseRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
    }

    public ExpenseResponse addExpense(Long userId, ExpenseRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        if (request.getAmount() == null || request.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("Amount must be greater than 0");
        }

        Expense expense = new Expense();
        expense.setUser(user);
        expense.setCategory(category);
        expense.setAmount(request.getAmount());
        expense.setDescription(request.getDescription());
        expense.setDate(LocalDate.parse(request.getDate()));

        Expense saved = expenseRepository.save(expense);
        return ExpenseResponse.from(saved);
    }

    public List<ExpenseResponse> getExpenses(Long userId) {
        return expenseRepository.findByUserIdOrderByDateDesc(userId)
                .stream()
                .map(ExpenseResponse::from)
                .collect(Collectors.toList());
    }

    public ExpenseResponse updateExpense(Long userId, Long expenseId, ExpenseRequest request) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("Expense not found"));

        if (!expense.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        expense.setAmount(request.getAmount());
        expense.setCategory(category);
        expense.setDescription(request.getDescription());
        expense.setDate(LocalDate.parse(request.getDate()));

        return ExpenseResponse.from(expenseRepository.save(expense));
    }

    public void deleteExpense(Long userId, Long expenseId) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new RuntimeException("Expense not found"));
        if (!expense.getUser().getId().equals(userId)) {
            throw new RuntimeException("Unauthorized");
        }
        expenseRepository.delete(expense);
    }

    public Map<String, Object> getSummary(Long userId) {
        Map<String, Object> summary = new HashMap<>();
        summary.put("totalExpenses", expenseRepository.getTotalByUserId(userId));
        summary.put("totalTransactions", expenseRepository.getCountByUserId(userId));

        List<Object[]> categoryData = expenseRepository.getSummaryByCategory(userId);
        List<Map<String, Object>> categories = new ArrayList<>();
        for (Object[] row : categoryData) {
            Map<String, Object> cat = new HashMap<>();
            cat.put("category", row[0]);
            cat.put("total", row[1]);
            categories.add(cat);
        }
        summary.put("byCategory", categories);
        return summary;
    }
}

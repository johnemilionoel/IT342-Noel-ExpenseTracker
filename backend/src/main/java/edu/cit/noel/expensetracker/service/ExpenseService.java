package edu.cit.noel.expensetracker.service;

import edu.cit.noel.expensetracker.dto.ExpenseRequest;
import edu.cit.noel.expensetracker.dto.ExpenseResponse;
import edu.cit.noel.expensetracker.entity.*;
import edu.cit.noel.expensetracker.repository.*;
import edu.cit.noel.expensetracker.validation.ExpenseValidationStrategy;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ExpenseService {
    private final ExpenseRepository expenseRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final ExpenseValidationStrategy expenseValidator;

    public ExpenseService(ExpenseRepository er, UserRepository ur, CategoryRepository cr, ExpenseValidationStrategy ev) {
        this.expenseRepository = er; this.userRepository = ur; this.categoryRepository = cr; this.expenseValidator = ev;
    }

    public ExpenseResponse addExpense(Long userId, ExpenseRequest request) {
        expenseValidator.validate(request);
        User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User not found"));
        Category cat = categoryRepository.findById(request.getCategoryId()).orElseThrow(() -> new RuntimeException("Category not found"));
        Expense e = new Expense(); e.setUser(user); e.setCategory(cat); e.setAmount(request.getAmount());
        e.setDescription(request.getDescription()); e.setDate(LocalDate.parse(request.getDate()));
        return ExpenseResponse.from(expenseRepository.save(e));
    }

    public List<ExpenseResponse> getExpenses(Long userId) {
        return expenseRepository.findByUserIdOrderByDateDesc(userId).stream().map(ExpenseResponse::from).collect(Collectors.toList());
    }

    public ExpenseResponse updateExpense(Long userId, Long expenseId, ExpenseRequest request) {
        expenseValidator.validate(request);
        Expense e = expenseRepository.findById(expenseId).orElseThrow(() -> new RuntimeException("Expense not found"));
        if (!e.getUser().getId().equals(userId)) throw new RuntimeException("Unauthorized");
        Category cat = categoryRepository.findById(request.getCategoryId()).orElseThrow(() -> new RuntimeException("Category not found"));
        e.setAmount(request.getAmount()); e.setCategory(cat); e.setDescription(request.getDescription()); e.setDate(LocalDate.parse(request.getDate()));
        return ExpenseResponse.from(expenseRepository.save(e));
    }

    public void deleteExpense(Long userId, Long expenseId) {
        Expense e = expenseRepository.findById(expenseId).orElseThrow(() -> new RuntimeException("Expense not found"));
        if (!e.getUser().getId().equals(userId)) throw new RuntimeException("Unauthorized");
        expenseRepository.delete(e);
    }

    public Map<String, Object> getSummary(Long userId) {
        Map<String, Object> s = new HashMap<>();
        s.put("totalExpenses", expenseRepository.getTotalByUserId(userId));
        s.put("totalTransactions", expenseRepository.getCountByUserId(userId));
        List<Object[]> cd = expenseRepository.getSummaryByCategory(userId);
        List<Map<String, Object>> cats = new ArrayList<>();
        for (Object[] r : cd) { Map<String, Object> m = new HashMap<>(); m.put("category", r[0]); m.put("total", r[1]); cats.add(m); }
        s.put("byCategory", cats); return s;
    }
}

package edu.cit.noel.expensetracker.controller;

import edu.cit.noel.expensetracker.dto.ExpenseRequest;
import edu.cit.noel.expensetracker.dto.ExpenseResponse;
import edu.cit.noel.expensetracker.service.ExpenseService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/expenses")
public class ExpenseController {

    private final ExpenseService expenseService;

    public ExpenseController(ExpenseService expenseService) {
        this.expenseService = expenseService;
    }

    @PostMapping
    public ResponseEntity<?> addExpense(@RequestParam Long userId, @RequestBody ExpenseRequest request) {
        try {
            ExpenseResponse response = expenseService.addExpense(userId, request);
            return ResponseEntity.ok(response);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping
    public ResponseEntity<List<ExpenseResponse>> getExpenses(@RequestParam Long userId) {
        return ResponseEntity.ok(expenseService.getExpenses(userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateExpense(@RequestParam Long userId,
                                           @PathVariable Long id,
                                           @RequestBody ExpenseRequest request) {
        try {
            return ResponseEntity.ok(expenseService.updateExpense(userId, id, request));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteExpense(@RequestParam Long userId, @PathVariable Long id) {
        try {
            expenseService.deleteExpense(userId, id);
            return ResponseEntity.ok(Map.of("message", "Expense deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/summary")
    public ResponseEntity<Map<String, Object>> getSummary(@RequestParam Long userId) {
        return ResponseEntity.ok(expenseService.getSummary(userId));
    }
}

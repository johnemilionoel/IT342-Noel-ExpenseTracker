package edu.cit.noel.expensetracker.controller;

import edu.cit.noel.expensetracker.dto.ApiResponse;
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
    public ExpenseController(ExpenseService es) { this.expenseService = es; }

    @PostMapping
    public ResponseEntity<ApiResponse<ExpenseResponse>> addExpense(@RequestParam Long userId, @RequestBody ExpenseRequest request) {
        return ResponseEntity.ok(ApiResponse.success(expenseService.addExpense(userId, request), "Expense added successfully"));
    }
    @GetMapping
    public ResponseEntity<ApiResponse<List<ExpenseResponse>>> getExpenses(@RequestParam Long userId) {
        return ResponseEntity.ok(ApiResponse.success(expenseService.getExpenses(userId)));
    }
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<ExpenseResponse>> updateExpense(@RequestParam Long userId, @PathVariable Long id, @RequestBody ExpenseRequest request) {
        return ResponseEntity.ok(ApiResponse.success(expenseService.updateExpense(userId, id, request), "Expense updated"));
    }
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<?>> deleteExpense(@RequestParam Long userId, @PathVariable Long id) {
        expenseService.deleteExpense(userId, id);
        return ResponseEntity.ok(ApiResponse.success(null, "Expense deleted successfully"));
    }
    @GetMapping("/summary")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getSummary(@RequestParam Long userId) {
        return ResponseEntity.ok(ApiResponse.success(expenseService.getSummary(userId)));
    }
}

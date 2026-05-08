package edu.cit.noel.expensetracker.expense;

import edu.cit.noel.expensetracker.auth.User;
import edu.cit.noel.expensetracker.auth.UserRepository;
import edu.cit.noel.expensetracker.category.Category;
import edu.cit.noel.expensetracker.category.CategoryRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.ArrayList;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseServiceTest {

    @Mock private ExpenseRepository expenseRepository;
    @Mock private UserRepository userRepository;
    @Mock private CategoryRepository categoryRepository;
    @Mock private ExpenseValidationStrategy expenseValidator;
    @InjectMocks private ExpenseService expenseService;

    private User testUser;
    private Category testCategory;
    private Expense testExpense;
    private ExpenseRequest expenseRequest;

    @BeforeEach
    void setUp() {
        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("john@test.com");

        testCategory = new Category();
        testCategory.setId(1L);
        testCategory.setName("Food & Dining");

        testExpense = new Expense();
        testExpense.setId(1L);
        testExpense.setUser(testUser);
        testExpense.setCategory(testCategory);
        testExpense.setAmount(new BigDecimal("150.00"));
        testExpense.setDescription("Lunch");
        testExpense.setDate(LocalDate.now());

        expenseRequest = new ExpenseRequest();
        expenseRequest.setAmount(new BigDecimal("150.00"));
        expenseRequest.setCategoryId(1L);
        expenseRequest.setDate(LocalDate.now().toString());
        expenseRequest.setDescription("Lunch");
    }

    @Test
    @DisplayName("Add expense with valid data returns ExpenseResponse")
    void addExpense_validData_returnsResponse() {
        doNothing().when(expenseValidator).validate(any());
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(expenseRepository.save(any(Expense.class))).thenReturn(testExpense);

        ExpenseResponse result = expenseService.addExpense(1L, expenseRequest);

        assertNotNull(result);
        assertEquals("Food & Dining", result.getCategory());
        verify(expenseRepository).save(any(Expense.class));
    }

    @Test
    @DisplayName("Add expense with invalid user throws exception")
    void addExpense_invalidUser_throwsException() {
        doNothing().when(expenseValidator).validate(any());
        when(userRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> expenseService.addExpense(999L, expenseRequest));
    }

    @Test
    @DisplayName("Add expense with invalid category throws exception")
    void addExpense_invalidCategory_throwsException() {
        doNothing().when(expenseValidator).validate(any());
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(categoryRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> expenseService.addExpense(1L, expenseRequest));
    }

    @Test
    @DisplayName("Get expenses returns list sorted by date")
    void getExpenses_returnsList() {
        Expense expense2 = new Expense();
        expense2.setId(2L);
        expense2.setUser(testUser);
        expense2.setCategory(testCategory);
        expense2.setAmount(new BigDecimal("200.00"));
        expense2.setDescription("Dinner");
        expense2.setDate(LocalDate.now());

        when(expenseRepository.findByUserIdOrderByDateDesc(1L))
            .thenReturn(Arrays.asList(testExpense, expense2));

        List<ExpenseResponse> result = expenseService.getExpenses(1L);

        assertEquals(2, result.size());
        verify(expenseRepository).findByUserIdOrderByDateDesc(1L);
    }

    @Test
    @DisplayName("Delete expense owned by user succeeds")
    void deleteExpense_ownedByUser_succeeds() {
        when(expenseRepository.findById(1L)).thenReturn(Optional.of(testExpense));

        assertDoesNotThrow(() -> expenseService.deleteExpense(1L, 1L));
        verify(expenseRepository).delete(testExpense);
    }

    @Test
    @DisplayName("Delete expense not owned by user throws unauthorized")
    void deleteExpense_notOwnedByUser_throwsUnauthorized() {
        when(expenseRepository.findById(1L)).thenReturn(Optional.of(testExpense));

        assertThrows(RuntimeException.class, () -> expenseService.deleteExpense(999L, 1L));
        verify(expenseRepository, never()).delete(any());
    }

    @Test
    @DisplayName("Delete non-existent expense throws exception")
    void deleteExpense_nonExistent_throwsException() {
        when(expenseRepository.findById(999L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> expenseService.deleteExpense(1L, 999L));
    }

    @Test
    @DisplayName("Update expense with valid data succeeds")
    void updateExpense_validData_returnsUpdated() {
        doNothing().when(expenseValidator).validate(any());
        when(expenseRepository.findById(1L)).thenReturn(Optional.of(testExpense));
        when(categoryRepository.findById(1L)).thenReturn(Optional.of(testCategory));
        when(expenseRepository.save(any(Expense.class))).thenReturn(testExpense);

        ExpenseResponse result = expenseService.updateExpense(1L, 1L, expenseRequest);

        assertNotNull(result);
        verify(expenseRepository).save(any(Expense.class));
    }

    @Test
    @DisplayName("Update expense not owned by user throws unauthorized")
    void updateExpense_notOwnedByUser_throwsUnauthorized() {
        doNothing().when(expenseValidator).validate(any());
        when(expenseRepository.findById(1L)).thenReturn(Optional.of(testExpense));

        assertThrows(RuntimeException.class, () -> expenseService.updateExpense(999L, 1L, expenseRequest));
    }

    @Test
    @DisplayName("Get summary returns correct structure")
    void getSummary_returnsCorrectStructure() {
        when(expenseRepository.getTotalByUserId(1L)).thenReturn(new BigDecimal("500.00"));
        when(expenseRepository.getCountByUserId(1L)).thenReturn(5L);
        List<Object[]> categoryData = new ArrayList<>();
        categoryData.add(new Object[]{"Food & Dining", new BigDecimal("300.00")});
        when(expenseRepository.getSummaryByCategory(1L)).thenReturn(categoryData);
        var result = expenseService.getSummary(1L);

        assertEquals(new BigDecimal("500.00"), result.get("totalExpenses"));
        assertEquals(5L, result.get("totalTransactions"));
        assertNotNull(result.get("byCategory"));
    }
}

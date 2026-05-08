package edu.cit.noel.expensetracker;

import edu.cit.noel.expensetracker.common.ApiResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApiResponseTest {

    @Test
    @DisplayName("Success response has correct fields")
    void success_hasCorrectFields() {
        ApiResponse<String> response = ApiResponse.success("test data", "Operation successful");

        assertTrue(response.isSuccess());
        assertEquals("test data", response.getData());
        assertEquals("Operation successful", response.getMessage());
        assertNotNull(response.getTimestamp());
    }

    @Test
    @DisplayName("Error response has correct fields")
    void error_hasCorrectFields() {
        ApiResponse<String> response = ApiResponse.error("Something went wrong");

        assertFalse(response.isSuccess());
        assertNull(response.getData());
        assertEquals("Something went wrong", response.getMessage());
        assertNotNull(response.getTimestamp());
    }

    @Test
    @DisplayName("Success with default message")
    void success_defaultMessage() {
        ApiResponse<Integer> response = ApiResponse.success(42);

        assertTrue(response.isSuccess());
        assertEquals(42, response.getData());
        assertEquals("Success", response.getMessage());
    }
}

package edu.cit.noel.expensetracker.data.model

import java.math.BigDecimal

// ApiResponse wrapper from backend
data class ApiResponse<T>(
    val success: Boolean,
    val message: String?,
    val data: T?,
    val timestamp: String?
)

data class ExpenseRequest(
    val amount: Double,
    val categoryId: Long,
    val date: String,
    val description: String?
)

data class ExpenseResponse(
    val id: Long,
    val amount: Double,
    val category: String,
    val categoryId: Long,
    val description: String?,
    val date: String,
    val createdAt: String?
)

data class CategoryResponse(
    val id: Long,
    val name: String
)

data class SummaryData(
    val totalExpenses: Double,
    val totalTransactions: Long,
    val byCategory: List<CategorySummary>
)

data class CategorySummary(
    val category: String,
    val total: Double
)

package edu.cit.noel.expensetracker.expense

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import edu.cit.noel.expensetracker.common.RetrofitClient
import edu.cit.noel.expensetracker.auth.*
import edu.cit.noel.expensetracker.expense.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch


data class ExpenseUiState(
    val isLoading: Boolean = false,
    val expenses: List<ExpenseResponse> = emptyList(),
    val categories: List<CategoryResponse> = emptyList(),
    val totalExpenses: Double = 0.0,
    val totalTransactions: Long = 0,
    val byCategory: List<CategorySummary> = emptyList(),
    val errorMessage: String? = null,
    val addSuccess: Boolean = false
)

class ExpenseViewModel(application: Application) : AndroidViewModel(application) {

    private val api = RetrofitClient.expenseApi

    private val _uiState = MutableStateFlow(ExpenseUiState())
    val uiState: StateFlow<ExpenseUiState> = _uiState.asStateFlow()

    fun loadDashboard(userId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val summaryRes = api.getSummary(userId)
                val expensesRes = api.getExpenses(userId)

                val summary = summaryRes.body()?.data
                val expenses = expensesRes.body()?.data ?: emptyList()

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    expenses = expenses,
                    totalExpenses = summary?.totalExpenses ?: 0.0,
                    totalTransactions = summary?.totalTransactions ?: 0,
                    byCategory = summary?.byCategory ?: emptyList()
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Failed to load data: ${e.localizedMessage}"
                )
            }
        }
    }

    fun loadExpenses(userId: Long) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val res = api.getExpenses(userId)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    expenses = res.body()?.data ?: emptyList()
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = e.localizedMessage)
            }
        }
    }

    fun loadCategories() {
        viewModelScope.launch {
            try {
                val res = api.getCategories()
                if (res.isSuccessful) {
                    _uiState.value = _uiState.value.copy(categories = res.body() ?: emptyList())
                }
            } catch (_: Exception) {}
        }
    }

    fun addExpense(userId: Long, request: ExpenseRequest) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, addSuccess = false)
            try {
                val res = api.addExpense(userId, request)
                if (res.isSuccessful) {
                    _uiState.value = _uiState.value.copy(isLoading = false, addSuccess = true)
                } else {
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = "Failed to add expense")
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = e.localizedMessage)
            }
        }
    }

    fun deleteExpense(userId: Long, expenseId: Long) {
        viewModelScope.launch {
            try {
                api.deleteExpense(expenseId, userId)
                loadExpenses(userId)
            } catch (_: Exception) {}
        }
    }

    fun resetAddSuccess() {
        _uiState.value = _uiState.value.copy(addSuccess = false)
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(errorMessage = null)
    }
}

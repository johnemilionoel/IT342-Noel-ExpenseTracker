package edu.cit.noel.expensetracker.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import edu.cit.noel.expensetracker.data.api.RetrofitClient
import edu.cit.noel.expensetracker.data.local.AppDatabase
import edu.cit.noel.expensetracker.data.local.UserEntity
import edu.cit.noel.expensetracker.data.model.LoginRequest
import edu.cit.noel.expensetracker.data.model.RegisterRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val isLoginSuccess: Boolean = false,
    val isRegisterSuccess: Boolean = false,
    val loggedInUserName: String? = null
)

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val authApi = RetrofitClient.authApi
    private val userDao = AppDatabase.getInstance(application).userDao()

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init {
        checkLoggedInUser()
    }

    private fun checkLoggedInUser() {
        viewModelScope.launch {
            val user = userDao.getLoggedInUser()
            if (user != null) {
                _uiState.value = _uiState.value.copy(
                    isLoginSuccess = true,
                    loggedInUserName = "${user.firstname} ${user.lastname}"
                )
            }
        }
    }

    fun login(email: String, password: String) {
        // Input validation
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Please fill in all fields"
            )
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Please enter a valid email address"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                val response = authApi.login(LoginRequest(email, password))

                if (response.isSuccessful) {
                    val body = response.body() ?: ""

                    if (body.contains("Login successful", ignoreCase = true)) {
                        // Save user session locally
                        userDao.logoutAll()
                        userDao.insertUser(
                            UserEntity(
                                email = email,
                                firstname = email.substringBefore("@"),
                                lastname = "",
                                isLoggedIn = true
                            )
                        )

                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isLoginSuccess = true,
                            successMessage = "Login successful!",
                            loggedInUserName = email.substringBefore("@")
                        )
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "Invalid email or password"
                        )
                    }
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Login failed. Please try again."
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Network error: ${e.localizedMessage ?: "Could not connect to server"}"
                )
            }
        }
    }

    fun register(firstname: String, lastname: String, email: String, password: String, confirmPassword: String) {
        // Input validation
        if (firstname.isBlank() || lastname.isBlank() || email.isBlank() || password.isBlank()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Please fill in all fields"
            )
            return
        }

        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Please enter a valid email address"
            )
            return
        }

        if (password.length < 6) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Password must be at least 6 characters"
            )
            return
        }

        if (password != confirmPassword) {
            _uiState.value = _uiState.value.copy(
                errorMessage = "Passwords do not match"
            )
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                val response = authApi.register(
                    RegisterRequest(firstname, lastname, email, password)
                )

                if (response.isSuccessful) {
                    val body = response.body() ?: ""

                    if (body.contains("successfully", ignoreCase = true)) {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isRegisterSuccess = true,
                            successMessage = "Account created successfully!"
                        )
                    } else if (body.contains("already", ignoreCase = true)) {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = "This email is already registered"
                        )
                    } else {
                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            errorMessage = body
                        )
                    }
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Registration failed. Please try again."
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = "Network error: ${e.localizedMessage ?: "Could not connect to server"}"
                )
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            userDao.logoutAll()
            _uiState.value = AuthUiState()
        }
    }

    fun clearMessages() {
        _uiState.value = _uiState.value.copy(
            errorMessage = null,
            successMessage = null
        )
    }

    fun resetRegisterSuccess() {
        _uiState.value = _uiState.value.copy(isRegisterSuccess = false)
    }
}

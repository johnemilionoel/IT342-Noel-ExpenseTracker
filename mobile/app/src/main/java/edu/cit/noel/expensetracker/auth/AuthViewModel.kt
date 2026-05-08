package edu.cit.noel.expensetracker.auth

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import edu.cit.noel.expensetracker.common.RetrofitClient
import edu.cit.noel.expensetracker.common.AppDatabase
import edu.cit.noel.expensetracker.common.UserEntity
import edu.cit.noel.expensetracker.auth.LoginRequest
import edu.cit.noel.expensetracker.auth.RegisterRequest
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject


data class AuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val isLoginSuccess: Boolean = false,
    val isRegisterSuccess: Boolean = false,
    val loggedInUserName: String? = null,
    val loggedInUserId: Long = 0
)

class AuthViewModel(application: Application) : AndroidViewModel(application) {

    private val authApi = RetrofitClient.authApi
    private val userDao = AppDatabase.getInstance(application).userDao()

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    init { checkLoggedInUser() }

    private fun checkLoggedInUser() {
        viewModelScope.launch {
            val user = userDao.getLoggedInUser()
            if (user != null) {
                _uiState.value = _uiState.value.copy(
                    isLoginSuccess = true,
                    loggedInUserName = "${user.firstname} ${user.lastname}".trim(),
                    loggedInUserId = user.id
                )
            }
        }
    }

    fun login(email: String, password: String) {
        if (email.isBlank() || password.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please fill in all fields"); return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter a valid email"); return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val response = authApi.login(LoginRequest(email, password))
                if (response.isSuccessful) {
                    val body = response.body() ?: ""

                    // Handle both plain string and ApiResponse JSON
                    val isSuccess = body.contains("Login successful", ignoreCase = true) ||
                            body.contains("\"success\":true", ignoreCase = true)

                    if (isSuccess) {
                        // Fetch user info to get ID
                        val userRes = RetrofitClient.authApi.let {
                            val url = "api/auth/user?email=$email"
                            val call = okhttp3.Request.Builder()
                                .url("${getBaseUrl()}$url")
                                .build()
                            RetrofitClient.httpClient.newCall(call).execute()
                        }

                        var userId = 0L
                        var firstname = email.substringBefore("@")
                        var lastname = ""

                        if (userRes.isSuccessful) {
                            val json = JSONObject(userRes.body?.string() ?: "{}")
                            // Handle ApiResponse wrapper
                            val data = if (json.has("data") && json.get("data") is JSONObject) json.getJSONObject("data") else json
                            userId = data.optLong("id", 0)
                            firstname = data.optString("firstname", firstname)
                            lastname = data.optString("lastname", "")
                        }

                        userDao.logoutAll()
                        userDao.insertUser(UserEntity(
                            id = userId,
                            email = email,
                            firstname = firstname,
                            lastname = lastname,
                            isLoggedIn = true
                        ))

                        _uiState.value = _uiState.value.copy(
                            isLoading = false,
                            isLoginSuccess = true,
                            loggedInUserName = "$firstname $lastname".trim(),
                            loggedInUserId = userId
                        )
                    } else {
                        _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = "Invalid email or password")
                    }
                } else {
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = "Invalid email or password")
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false,
                    errorMessage = "Network error: ${e.localizedMessage ?: "Could not connect"}")
            }
        }
    }

    fun register(firstname: String, lastname: String, email: String, password: String, confirmPassword: String) {
        if (firstname.isBlank() || lastname.isBlank() || email.isBlank() || password.isBlank()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please fill in all fields"); return
        }
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _uiState.value = _uiState.value.copy(errorMessage = "Please enter a valid email"); return
        }
        if (password.length < 6) {
            _uiState.value = _uiState.value.copy(errorMessage = "Password must be at least 6 characters"); return
        }
        if (password != confirmPassword) {
            _uiState.value = _uiState.value.copy(errorMessage = "Passwords do not match"); return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val response = authApi.register(RegisterRequest(firstname, lastname, email, password))
                if (response.isSuccessful) {
                    val body = response.body() ?: ""
                    if (body.contains("successfully", ignoreCase = true)) {
                        _uiState.value = _uiState.value.copy(isLoading = false, isRegisterSuccess = true, successMessage = "Account created!")
                    } else if (body.contains("already", ignoreCase = true)) {
                        _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = "Email already registered")
                    } else {
                        _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = body)
                    }
                } else {
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = "Registration failed")
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = "Network error: ${e.localizedMessage}")
            }
        }
    }

    fun logout() {
        viewModelScope.launch { userDao.logoutAll(); _uiState.value = AuthUiState() }
    }

    fun clearMessages() { _uiState.value = _uiState.value.copy(errorMessage = null, successMessage = null) }
    fun resetRegisterSuccess() { _uiState.value = _uiState.value.copy(isRegisterSuccess = false) }

    companion object {
        fun getBaseUrl() = "http://10.0.2.2:8080/"
    }
}

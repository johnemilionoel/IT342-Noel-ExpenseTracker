package edu.cit.noel.expensetracker.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import edu.cit.noel.expensetracker.ui.screens.DashboardScreen
import edu.cit.noel.expensetracker.ui.screens.LoginScreen
import edu.cit.noel.expensetracker.ui.screens.RegisterScreen
import edu.cit.noel.expensetracker.viewmodel.AuthViewModel

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val DASHBOARD = "dashboard"
}

@Composable
fun NavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel = viewModel()
) {
    val uiState by authViewModel.uiState.collectAsState()

    NavHost(
        navController = navController,
        startDestination = if (uiState.isLoginSuccess) Routes.DASHBOARD else Routes.LOGIN
    ) {
        // Login Screen
        composable(Routes.LOGIN) {
            LoginScreen(
                uiState = uiState,
                onLogin = { email, password ->
                    authViewModel.login(email, password)
                },
                onNavigateToRegister = {
                    authViewModel.clearMessages()
                    navController.navigate(Routes.REGISTER)
                },
                onGoogleLogin = {
                    // Google OAuth is handled via the web backend
                    // On a real device, this would open a CustomTab or WebView
                },
                onClearMessages = {
                    authViewModel.clearMessages()
                }
            )

            // Navigate to dashboard on successful login
            if (uiState.isLoginSuccess) {
                navController.navigate(Routes.DASHBOARD) {
                    popUpTo(Routes.LOGIN) { inclusive = true }
                }
            }
        }

        // Register Screen
        composable(Routes.REGISTER) {
            RegisterScreen(
                uiState = uiState,
                onRegister = { firstname, lastname, email, password, confirmPassword ->
                    authViewModel.register(firstname, lastname, email, password, confirmPassword)
                },
                onNavigateBack = {
                    authViewModel.resetRegisterSuccess()
                    navController.popBackStack()
                },
                onClearMessages = {
                    authViewModel.clearMessages()
                }
            )
        }

        // Dashboard Screen
        composable(Routes.DASHBOARD) {
            DashboardScreen(
                userName = uiState.loggedInUserName,
                onLogout = {
                    authViewModel.logout()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(Routes.DASHBOARD) { inclusive = true }
                    }
                }
            )
        }
    }
}

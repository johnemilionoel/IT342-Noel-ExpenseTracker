package edu.cit.noel.expensetracker.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import edu.cit.noel.expensetracker.auth.*
import edu.cit.noel.expensetracker.expense.*
import edu.cit.noel.expensetracker.common.theme.*
import edu.cit.noel.expensetracker.auth.AuthViewModel
import edu.cit.noel.expensetracker.expense.ExpenseViewModel
import androidx.compose.ui.unit.dp
import edu.cit.noel.expensetracker.auth.*
import edu.cit.noel.expensetracker.expense.*
import edu.cit.noel.expensetracker.common.theme.*

object Routes {
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val DASHBOARD = "dashboard"
    const val EXPENSES = "expenses"
    const val ADD_EXPENSE = "add_expense"
}

data class BottomNavItem(val route: String, val label: String, val icon: ImageVector)

val bottomNavItems = listOf(
    BottomNavItem(Routes.DASHBOARD, "Home", Icons.Filled.Home),
    BottomNavItem(Routes.EXPENSES, "Expenses", Icons.Filled.List),
    BottomNavItem(Routes.ADD_EXPENSE, "Add", Icons.Filled.Add),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NavGraph(
    navController: NavHostController,
    authViewModel: AuthViewModel = viewModel(),
    expenseViewModel: ExpenseViewModel = viewModel()
) {
    val authState by authViewModel.uiState.collectAsState()
    val expenseState by expenseViewModel.uiState.collectAsState()
    val navBackStack by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStack?.destination?.route

    val isLoggedIn = authState.isLoginSuccess
    val showBottomBar = currentRoute in listOf(Routes.DASHBOARD, Routes.EXPENSES, Routes.ADD_EXPENSE)

    // Get user id from local storage
    val userId = authState.loggedInUserId

    Scaffold(
        topBar = {
            if (showBottomBar) {
                TopAppBar(
                    title = { Text("Expense Tracker", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                    actions = {
                        IconButton(onClick = {
                            authViewModel.logout()
                            navController.navigate(Routes.LOGIN) { popUpTo(0) { inclusive = true } }
                        }) {
                            Icon(Icons.AutoMirrored.Filled.Logout, "Logout", tint = Blue600)
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = White, titleContentColor = Gray800)
                )
            }
        },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(containerColor = White, tonalElevation = 8.dp) {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            selected = currentRoute == item.route,
                            onClick = {
                                navController.navigate(item.route) {
                                    popUpTo(Routes.DASHBOARD) { inclusive = (item.route == Routes.DASHBOARD) }
                                    launchSingleTop = true
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label, fontSize = 11.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Blue600,
                                selectedTextColor = Blue600,
                                unselectedIconColor = Gray400,
                                unselectedTextColor = Gray400,
                                indicatorColor = Blue50
                            )
                        )
                    }
                }
            }
        }
    ) { paddingValues ->

        NavHost(
            navController = navController,
            startDestination = if (isLoggedIn) Routes.DASHBOARD else Routes.LOGIN,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable(Routes.LOGIN) {
                LoginScreen(
                    uiState = authState,
                    onLogin = { email, password -> authViewModel.login(email, password) },
                    onNavigateToRegister = {
                        authViewModel.clearMessages()
                        navController.navigate(Routes.REGISTER)
                    },
                    onGoogleLogin = {},
                    onClearMessages = { authViewModel.clearMessages() }
                )
                if (authState.isLoginSuccess) {
                    LaunchedEffect(Unit) {
                        navController.navigate(Routes.DASHBOARD) { popUpTo(Routes.LOGIN) { inclusive = true } }
                    }
                }
            }

            composable(Routes.REGISTER) {
                RegisterScreen(
                    uiState = authState,
                    onRegister = { fn, ln, e, p, cp -> authViewModel.register(fn, ln, e, p, cp) },
                    onNavigateBack = { authViewModel.resetRegisterSuccess(); navController.popBackStack() },
                    onClearMessages = { authViewModel.clearMessages() }
                )
            }

            composable(Routes.DASHBOARD) {
                DashboardScreen(
                    userName = authState.loggedInUserName,
                    userId = userId,
                    expenseState = expenseState,
                    onLoadDashboard = { expenseViewModel.loadDashboard(it) },
                    onNavigateExpenses = { navController.navigate(Routes.EXPENSES) },
                    onNavigateAddExpense = { navController.navigate(Routes.ADD_EXPENSE) }
                )
            }

            composable(Routes.EXPENSES) {
                ExpensesScreen(
                    userId = userId,
                    expenseState = expenseState,
                    onLoad = { expenseViewModel.loadExpenses(it) },
                    onDelete = { uid, eid -> expenseViewModel.deleteExpense(uid, eid) }
                )
            }

            composable(Routes.ADD_EXPENSE) {
                AddExpenseScreen(
                    userId = userId,
                    expenseState = expenseState,
                    onLoadCategories = { expenseViewModel.loadCategories() },
                    onAdd = { uid, req -> expenseViewModel.addExpense(uid, req) },
                    onSuccess = {
                        expenseViewModel.resetAddSuccess()
                        navController.navigate(Routes.DASHBOARD) { popUpTo(Routes.DASHBOARD) { inclusive = true } }
                    }
                )
            }
        }
    }
}

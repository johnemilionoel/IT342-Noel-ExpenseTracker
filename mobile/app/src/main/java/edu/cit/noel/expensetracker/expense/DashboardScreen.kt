package edu.cit.noel.expensetracker.expense

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.cit.noel.expensetracker.common.theme.*
import edu.cit.noel.expensetracker.expense.ExpenseUiState
import java.text.NumberFormat
import java.util.Locale

private val fmt get() = NumberFormat.getNumberInstance(Locale("en", "PH")).apply { minimumFractionDigits = 2; maximumFractionDigits = 2 }

private val Purple = androidx.compose.ui.graphics.Color(0xFFA78BFA)
private val Pink = androidx.compose.ui.graphics.Color(0xFFF472B6)

private val catColors = mapOf(
    "Food & Dining" to Blue500, "Transportation" to GreenSuccess,
    "Office Supplies" to Purple, "Utilities" to OrangeWarning,
    "Healthcare" to RedError, "Entertainment" to Pink, "Other" to Gray400
)


@Composable
fun DashboardScreen(
    userName: String?,
    userId: Long,
    expenseState: ExpenseUiState,
    onLoadDashboard: (Long) -> Unit,
    onNavigateExpenses: () -> Unit,
    onNavigateAddExpense: () -> Unit
) {
    LaunchedEffect(userId) { onLoadDashboard(userId) }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)
    ) {
        // Greeting
        Text("Good morning, ${userName ?: "User"} 👋", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Gray800)
        Text("Here's your expense overview.", fontSize = 13.sp, color = Gray500)
        Spacer(Modifier.height(20.dp))

        // Summary cards
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SummaryCard("Total Expenses", "₱${fmt.format(expenseState.totalExpenses)}", Modifier.weight(1f))
            SummaryCard("Transactions", "${expenseState.totalTransactions}", Modifier.weight(1f))
        }

        Spacer(Modifier.height(20.dp))

        // By Category
        Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = White),
            elevation = CardDefaults.cardElevation(2.dp)) {
            Column(Modifier.padding(16.dp)) {
                Text("By Category", fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = Gray800)
                Spacer(Modifier.height(12.dp))
                if (expenseState.byCategory.isEmpty()) {
                    Text("No data yet", color = Gray400, fontSize = 13.sp)
                } else {
                    expenseState.byCategory.forEach { cat ->
                        val pct = if (expenseState.totalExpenses > 0) (cat.total / expenseState.totalExpenses * 100) else 0.0
                        Row(Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                            Box(Modifier.size(10.dp).background(catColors[cat.category] ?: Gray400, RoundedCornerShape(3.dp)))
                            Spacer(Modifier.width(8.dp))
                            Text(cat.category, fontSize = 13.sp, color = Gray700, modifier = Modifier.weight(1f))
                            Text("₱${fmt.format(cat.total)}", fontSize = 12.sp, color = Gray500)
                            Spacer(Modifier.width(8.dp))
                            Text("${pct.toInt()}%", fontSize = 12.sp, fontWeight = FontWeight.SemiBold, color = Gray700, modifier = Modifier.width(32.dp), textAlign = TextAlign.End)
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(20.dp))

        // Recent Expenses
        Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = White),
            elevation = CardDefaults.cardElevation(2.dp)) {
            Column(Modifier.padding(16.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text("Recent Expenses", fontWeight = FontWeight.SemiBold, fontSize = 15.sp, color = Gray800)
                    TextButton(onClick = onNavigateExpenses) { Text("View All →", fontSize = 12.sp, color = Blue600) }
                }
                if (expenseState.expenses.isEmpty()) {
                    Spacer(Modifier.height(16.dp))
                    Text("No expenses yet", color = Gray400, fontSize = 13.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                } else {
                    expenseState.expenses.take(5).forEach { exp ->
                        HorizontalDivider(Modifier.padding(vertical = 8.dp), color = Gray100)
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Text(exp.description ?: "—", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = Gray800, maxLines = 1, overflow = TextOverflow.Ellipsis)
                                Row {
                                    Text(exp.category, fontSize = 11.sp, color = Blue600)
                                    Text(" · ${exp.date}", fontSize = 11.sp, color = Gray400)
                                }
                            }
                            Text("₱${fmt.format(exp.amount)}", fontWeight = FontWeight.SemiBold, fontSize = 14.sp, color = Gray800)
                        }
                    }
                }
            }
        }
        Spacer(Modifier.height(80.dp)) // space for bottom nav
    }
}

@Composable
private fun SummaryCard(label: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier, shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(2.dp)) {
        Column(Modifier.padding(16.dp)) {
            Text(label, fontSize = 12.sp, color = Gray500)
            Spacer(Modifier.height(4.dp))
            Text(value, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Gray800)
        }
    }
}

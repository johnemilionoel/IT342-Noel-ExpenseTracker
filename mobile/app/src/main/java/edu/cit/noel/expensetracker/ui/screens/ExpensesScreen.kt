package edu.cit.noel.expensetracker.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.cit.noel.expensetracker.data.model.ExpenseResponse
import edu.cit.noel.expensetracker.ui.theme.*
import edu.cit.noel.expensetracker.viewmodel.ExpenseUiState
import java.text.NumberFormat
import java.util.Locale

@Composable
fun ExpensesScreen(
    userId: Long,
    expenseState: ExpenseUiState,
    onLoad: (Long) -> Unit,
    onDelete: (Long, Long) -> Unit
) {
    val fmt = NumberFormat.getNumberInstance(Locale("en", "PH")).apply { minimumFractionDigits = 2 }
    val total = expenseState.expenses.sumOf { it.amount }

    LaunchedEffect(userId) { onLoad(userId) }

    Column(Modifier.fillMaxSize().padding(20.dp)) {
        Text("Expense List", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Gray800)
        Text("${expenseState.expenses.size} expenses · Total: ₱${fmt.format(total)}", fontSize = 13.sp, color = Gray500)
        Spacer(Modifier.height(16.dp))

        if (expenseState.isLoading) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Blue600)
            }
        } else if (expenseState.expenses.isEmpty()) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No expenses yet", color = Gray400, fontSize = 15.sp)
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(bottom = 80.dp)) {
                itemsIndexed(expenseState.expenses) { _, expense ->
                    ExpenseItem(expense, fmt) {
                        android.util.Log.d("DELETE", "Deleting expense id=${expense.id} for userId=$userId")
                        onDelete(userId, expense.id)
                    }
                }
            }
        }
    }
}

@Composable
private fun ExpenseItem(expense: ExpenseResponse, fmt: NumberFormat, onDelete: () -> Unit) {
    Card(
        Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(1.dp)
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(expense.description ?: "—", fontWeight = FontWeight.Medium, fontSize = 14.sp, color = Gray800, maxLines = 1, overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(2.dp))
                Row {
                    Surface(shape = RoundedCornerShape(8.dp), color = Blue50) {
                        Text(expense.category, fontSize = 11.sp, color = Blue600, fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp))
                    }
                    Spacer(Modifier.width(8.dp))
                    Text(expense.date, fontSize = 12.sp, color = Gray400, modifier = Modifier.align(Alignment.CenterVertically))
                }
            }
            Text("₱${fmt.format(expense.amount)}", fontWeight = FontWeight.Bold, fontSize = 15.sp, color = Gray800)
            Spacer(Modifier.width(8.dp))
            IconButton(onClick = {
                android.util.Log.d("DELETE", "Tapped delete for expense")
                onDelete()
            }) {
                Icon(Icons.Outlined.Delete, contentDescription = "Delete", tint = RedError)
            }
        }
    }
}

package edu.cit.noel.expensetracker.expense

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import edu.cit.noel.expensetracker.expense.CategoryResponse
import edu.cit.noel.expensetracker.expense.ExpenseRequest
import edu.cit.noel.expensetracker.common.theme.*
import edu.cit.noel.expensetracker.expense.ExpenseUiState
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseScreen(
    userId: Long,
    expenseState: ExpenseUiState,
    onLoadCategories: () -> Unit,
    onAdd: (Long, ExpenseRequest) -> Unit,
    onSuccess: () -> Unit
) {
    var amount by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(LocalDate.now().toString()) }
    var selectedCategory by remember { mutableStateOf<CategoryResponse?>(null) }
    var expanded by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf("") }

    LaunchedEffect(Unit) { onLoadCategories() }
    LaunchedEffect(expenseState.addSuccess) {
        if (expenseState.addSuccess) onSuccess()
    }

    Column(
        Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp)
    ) {
        Text("Add New Expense", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = Gray800)
        Text("Fill in the details below.", fontSize = 13.sp, color = Gray500)
        Spacer(Modifier.height(24.dp))

        if (error.isNotBlank()) {
            Card(Modifier.fillMaxWidth(), colors = CardDefaults.cardColors(containerColor = RedError.copy(alpha = 0.1f)), shape = RoundedCornerShape(12.dp)) {
                Text(error, color = RedError, fontSize = 13.sp, modifier = Modifier.padding(12.dp))
            }
            Spacer(Modifier.height(12.dp))
        }

        Card(Modifier.fillMaxWidth(), shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = White),
            elevation = CardDefaults.cardElevation(2.dp)) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {

                // Amount
                Text("Amount (₱)", fontWeight = FontWeight.Medium, fontSize = 14.sp, color = Gray700)
                OutlinedTextField(value = amount, onValueChange = { amount = it }, placeholder = { Text("0.00") },
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Blue500, unfocusedBorderColor = Gray200))

                // Category dropdown
                Text("Category", fontWeight = FontWeight.Medium, fontSize = 14.sp, color = Gray700)
                ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                    OutlinedTextField(
                        value = selectedCategory?.name ?: "", onValueChange = {},
                        readOnly = true, placeholder = { Text("Select category") },
                        modifier = Modifier.fillMaxWidth().menuAnchor(), shape = RoundedCornerShape(12.dp),
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Blue500, unfocusedBorderColor = Gray200))
                    ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                        expenseState.categories.forEach { cat ->
                            DropdownMenuItem(text = { Text(cat.name) },
                                onClick = { selectedCategory = cat; expanded = false })
                        }
                    }
                }

                // Date
                Text("Date", fontWeight = FontWeight.Medium, fontSize = 14.sp, color = Gray700)
                OutlinedTextField(value = date, onValueChange = { date = it }, placeholder = { Text("YYYY-MM-DD") },
                    modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Blue500, unfocusedBorderColor = Gray200))

                // Description
                Text("Description (optional)", fontWeight = FontWeight.Medium, fontSize = 14.sp, color = Gray700)
                OutlinedTextField(value = description, onValueChange = { description = it }, placeholder = { Text("Brief description...") },
                    modifier = Modifier.fillMaxWidth().height(100.dp), shape = RoundedCornerShape(12.dp), maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Blue500, unfocusedBorderColor = Gray200))

                Spacer(Modifier.height(4.dp))

                // Submit
                Button(onClick = {
                    error = ""
                    val amt = amount.toDoubleOrNull()
                    if (amt == null || amt <= 0) { error = "Amount must be greater than 0"; return@Button }
                    if (selectedCategory == null) { error = "Please select a category"; return@Button }
                    if (date.isBlank()) { error = "Please enter a date"; return@Button }
                    onAdd(userId, ExpenseRequest(amt, selectedCategory!!.id, date, description.ifBlank { null }))
                }, modifier = Modifier.fillMaxWidth().height(52.dp), shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Blue600), enabled = !expenseState.isLoading) {
                    if (expenseState.isLoading) CircularProgressIndicator(Modifier.size(24.dp), color = White, strokeWidth = 2.dp)
                    else Text("Save Expense", fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                }
            }
        }
        Spacer(Modifier.height(80.dp))
    }
}

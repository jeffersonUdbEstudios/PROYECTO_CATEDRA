package com.example.proyecto_catedra.ui.screens.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*

enum class TransactionType {
    INCOME,
    EXPENSE
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionScreen(
    availableCategories: List<String> = emptyList(),
    onBack: () -> Unit = {},
    onSave: (
        type: TransactionType,
        amount: Double,
        category: String,
        description: String,
        date: Long,
        paymentMethod: String
    ) -> Unit = { _, _, _, _, _, _ -> }
) {
    var transactionType by remember { mutableStateOf<TransactionType>(TransactionType.EXPENSE) }
    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var paymentMethod by remember { mutableStateOf("Efectivo") }
    var description by remember { mutableStateOf("") }
    var date by remember { mutableStateOf(System.currentTimeMillis()) }
    var isDatePickerVisible by remember { mutableStateOf(false) }
    var selectedDate by remember { mutableStateOf(Calendar.getInstance()) }
    var expandedCategory by remember { mutableStateOf(false) }
    var expandedPaymentMethod by remember { mutableStateOf(false) }
    
    val paymentMethods = listOf(
        "Efectivo",
        "Tarjeta de Débito",
        "Tarjeta de Crédito",
        "Transferencia Bancaria",
        "Billetera Digital"
    )
    
    val blue = Color(0xFF2563EB)
    val lightGray = Color(0xFFF5F5F5)
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Atrás"
                )
            }
            
            Text(
                text = "Agregar Transacción",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Tipo de Transacción",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    onClick = { transactionType = TransactionType.INCOME },
                    modifier = Modifier
                        .weight(1f)
                        .height(70.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = if (transactionType == TransactionType.INCOME) Color(0xFF10B981) else lightGray
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "💰",
                            fontSize = 28.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Ingreso",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (transactionType == TransactionType.INCOME) Color.White else Color.Black
                        )
                    }
                }
                
                Surface(
                    onClick = { transactionType = TransactionType.EXPENSE },
                    modifier = Modifier
                        .weight(1f)
                        .height(70.dp),
                    shape = RoundedCornerShape(12.dp),
                    color = if (transactionType == TransactionType.EXPENSE) Color(0xFFEF4444) else lightGray
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "💸",
                            fontSize = 28.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Gasto",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (transactionType == TransactionType.EXPENSE) Color.White else Color.Black
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            OutlinedTextField(
                value = amount,
                onValueChange = { newValue ->
                    val filtered = newValue.filter { it.isDigit() || it == '.' }
                    if (filtered.count { it == '.' } <= 1) {
                        val parts = filtered.split(".")
                        if (parts.size <= 2) {
                            if (parts.size == 2 && parts[1].length <= 2) {
                                amount = filtered
                            } else if (parts.size == 1) {
                                amount = filtered
                            }
                        }
                    }
                },
                label = { Text("Monto") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                ),
                leadingIcon = {
                    Text("$", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                },
                placeholder = { Text("0.00") }
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = category,
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Categoría") },
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    placeholder = { Text("Selecciona una categoría") }
                )
                
                Button(
                    onClick = { expandedCategory = true },
                    modifier = Modifier.height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = blue
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.List,
                        contentDescription = "Seleccionar categoría",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            if (expandedCategory) {
                CategorySelectionModal(
                    availableCategories = availableCategories,
                    onDismiss = { expandedCategory = false },
                    onCategorySelected = { selectedCategory ->
                        category = selectedCategory
                        expandedCategory = false
                    }
                )
            }
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = paymentMethod,
                    onValueChange = { },
                    readOnly = true,
                    label = { Text("Método de Pago") },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
                
                Button(
                    onClick = { expandedPaymentMethod = true },
                    modifier = Modifier.height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = blue
                    )
                ) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Seleccionar método de pago",
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
            
            if (expandedPaymentMethod) {
                PaymentMethodSelectionModal(
                    paymentMethods = paymentMethods,
                    onDismiss = { expandedPaymentMethod = false },
                    onPaymentMethodSelected = { selectedMethod ->
                        paymentMethod = selectedMethod
                        expandedPaymentMethod = false
                    }
                )
            }
            
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción (opcional)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp),
                maxLines = 4
            )
            
            OutlinedTextField(
                value = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date(date)),
                onValueChange = { },
                label = { Text("Fecha") },
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { isDatePickerVisible = true },
                enabled = false,
                trailingIcon = {
                    IconButton(onClick = { isDatePickerVisible = true }) {
                        Icon(Icons.Default.DateRange, contentDescription = "Seleccionar fecha")
                    }
                },
                placeholder = { Text("Selecciona una fecha") }
            )
            
            if (isDatePickerVisible) {
                DatePickerDialog(
                    onDismiss = { isDatePickerVisible = false },
                    initialDate = selectedDate,
                    onDateSelected = { calendar ->
                        selectedDate = calendar
                        date = calendar.timeInMillis
                        isDatePickerVisible = false
                    }
                )
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            Button(
                onClick = {
                    if (amount.isNotEmpty() && category.isNotEmpty()) {
                        onSave(
                            transactionType,
                            amount.toDoubleOrNull() ?: 0.0,
                            category,
                            description,
                            date,
                            paymentMethod
                        )
                        onBack()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = amount.isNotEmpty() && category.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = blue
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Guardar Transacción",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerDialog(
    onDismiss: () -> Unit,
    initialDate: Calendar,
    onDateSelected: (Calendar) -> Unit
) {
    var selectedYear by remember { mutableIntStateOf(initialDate.get(Calendar.YEAR)) }
    var selectedMonth by remember { mutableIntStateOf(initialDate.get(Calendar.MONTH)) }
    var selectedDay by remember { mutableIntStateOf(initialDate.get(Calendar.DAY_OF_MONTH)) }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Seleccionar Fecha") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column {
                    Text("Año", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TextButton(
                            onClick = { selectedYear = (selectedYear - 1).coerceAtLeast(2020) }
                        ) {
                            Text("<")
                        }
                        Text(
                            text = selectedYear.toString(),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .weight(1f)
                                .padding(8.dp),
                            color = Color(0xFF2563EB)
                        )
                        TextButton(
                            onClick = { selectedYear++ }
                        ) {
                            Text(">")
                        }
                    }
                }
                
                Column {
                    Text("Mes", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TextButton(
                            onClick = {
                                if (selectedMonth == 0) {
                                    selectedMonth = 11
                                    selectedYear--
                                } else {
                                    selectedMonth--
                                }
                            }
                        ) {
                            Text("<")
                        }
                        Text(
                            text = SimpleDateFormat("MMMM", Locale.getDefault())
                                .format(Calendar.getInstance().apply { 
                                    set(Calendar.MONTH, selectedMonth) 
                                }.time),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .weight(1f)
                                .padding(8.dp),
                            color = Color(0xFF2563EB)
                        )
                        TextButton(
                            onClick = {
                                if (selectedMonth == 11) {
                                    selectedMonth = 0
                                    selectedYear++
                                } else {
                                    selectedMonth++
                                }
                            }
                        ) {
                            Text(">")
                        }
                    }
                }
                
                Column {
                    Text("Día", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TextButton(
                            onClick = { 
                                if (selectedDay > 1) selectedDay--
                                else {
                                    if (selectedMonth == 0) {
                                        selectedMonth = 11
                                        selectedYear--
                                    } else {
                                        selectedMonth--
                                    }
                                    selectedDay = getDaysInMonth(selectedYear, selectedMonth)
                                }
                            }
                        ) {
                            Text("<")
                        }
                        Text(
                            text = selectedDay.toString(),
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .weight(1f)
                                .padding(8.dp),
                            color = Color(0xFF2563EB)
                        )
                        TextButton(
                            onClick = {
                                val daysInMonth = getDaysInMonth(selectedYear, selectedMonth)
                                if (selectedDay < daysInMonth) selectedDay++
                                else {
                                    selectedDay = 1
                                    if (selectedMonth == 11) {
                                        selectedMonth = 0
                                        selectedYear++
                                    } else {
                                        selectedMonth++
                                    }
                                }
                            }
                        ) {
                            Text(">")
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val calendar = Calendar.getInstance().apply {
                        set(Calendar.YEAR, selectedYear)
                        set(Calendar.MONTH, selectedMonth)
                        set(Calendar.DAY_OF_MONTH, selectedDay)
                    }
                    onDateSelected(calendar)
                }
            ) {
                Text("Confirmar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}

fun getDaysInMonth(year: Int, month: Int): Int {
    val calendar = Calendar.getInstance()
    calendar.set(year, month, 1)
    return calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
}

@Composable
fun CategorySelectionModal(
    availableCategories: List<String>,
    onDismiss: () -> Unit,
    onCategorySelected: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Seleccionar Categoría",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            if (availableCategories.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Info,
                            contentDescription = "Sin categorías",
                            modifier = Modifier.size(48.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No hay categorías disponibles.\nCrea un presupuesto primero.",
                            fontSize = 12.sp,
                            color = Color.Gray,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                ) {
                    items(availableCategories) { item ->
                        Surface(
                            onClick = {
                                onCategorySelected(item)
                            },
                            color = Color.White,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Text(
                                text = item,
                                fontSize = 16.sp,
                                modifier = Modifier.padding(16.dp),
                                color = Color.Black
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}

@Composable
fun PaymentMethodSelectionModal(
    paymentMethods: List<String>,
    onDismiss: () -> Unit,
    onPaymentMethodSelected: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "Seleccionar Método de Pago",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
            ) {
                items(paymentMethods) { method ->
                    Surface(
                        onClick = {
                            onPaymentMethodSelected(method)
                        },
                        color = Color.White,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            val icon = when (method) {
                                "Efectivo" -> "💵"
                                "Tarjeta de Débito" -> "💳"
                                "Tarjeta de Crédito" -> "💳"
                                "Transferencia Bancaria" -> "🏦"
                                "Billetera Digital" -> "📱"
                                else -> "💰"
                            }
                            Text(
                                text = icon,
                                fontSize = 24.sp,
                                modifier = Modifier.padding(end = 12.dp)
                            )
                            Text(
                                text = method,
                                fontSize = 16.sp,
                                color = Color.Black
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
fun AddTransactionScreenPreview() {
    MaterialTheme {
        AddTransactionScreen()
    }
}


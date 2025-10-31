package com.example.proyecto_catedra.ui.screens.transactions

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.*

data class Transaction(
    val id: String,
    val description: String,
    val category: String,
    val amount: Double,
    val type: String, // "INCOME" or "EXPENSE"
    val date: Long,
    val paymentMethod: String,
    val icon: String = "💰"
)

data class TransactionGroup(
    val dateLabel: String,
    val transactions: List<Transaction>
)

@Composable
fun TransactionsScreen(
    transactions: List<Transaction> = emptyList(),
    isLoading: Boolean = false,
    onBack: () -> Unit = {},
    onNavigateToHome: () -> Unit = {},
    onNavigateToBudgets: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    currentTab: String = "Transacciones"
) {
    var searchText by remember { mutableStateOf("") }
    var showFilterDialog by remember { mutableStateOf(false) }
    var selectedType by remember { mutableStateOf<String?>(null) }
    var selectedCategories by remember { mutableStateOf(setOf<String>()) }
    var selectedPaymentMethods by remember { mutableStateOf(setOf<String>()) }
    var startDate by remember { mutableStateOf<Long?>(null) }
    var endDate by remember { mutableStateOf<Long?>(null) }
    var minAmount by remember { mutableStateOf("") }
    var maxAmount by remember { mutableStateOf("") }

    val availableCategories = remember(transactions) {
        transactions.map { it.category }.distinct().sorted()
    }

    val availablePaymentMethods = remember(transactions) {
        transactions.map { it.paymentMethod }.distinct().sorted()
    }

    val minAmountValue = minAmount.toDoubleOrNull()
    val maxAmountValue = maxAmount.toDoubleOrNull()

    val filteredTransactions = transactions.filter { transaction ->
        val matchesSearch = transaction.description.contains(searchText, ignoreCase = true) ||
            transaction.category.contains(searchText, ignoreCase = true)
        val matchesType = selectedType?.let { transaction.type == it } ?: true
        val matchesCategory = if (selectedCategories.isEmpty()) {
            true
        } else {
            selectedCategories.contains(transaction.category)
        }
        val matchesPayment = if (selectedPaymentMethods.isEmpty()) {
            true
        } else {
            selectedPaymentMethods.contains(transaction.paymentMethod)
        }
        val matchesStartDate = startDate?.let { transaction.date >= it } ?: true
        val matchesEndDate = endDate?.let { transaction.date <= it } ?: true
        val matchesMinAmount = minAmountValue?.let { transaction.amount >= it } ?: true
        val matchesMaxAmount = maxAmountValue?.let { transaction.amount <= it } ?: true

        matchesSearch && matchesType && matchesCategory && matchesPayment &&
            matchesStartDate && matchesEndDate && matchesMinAmount && matchesMaxAmount
    }

    val groupedTransactions = groupTransactionsByDate(filteredTransactions)
    val filteredCount = filteredTransactions.size
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Top Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Atrás",
                    tint = Color.Black,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Text(
                text = "Transacciones",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
        
        // Search Bar
        OutlinedTextField(
            value = searchText,
            onValueChange = { searchText = it },
            placeholder = { Text("Buscar transacciones") },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Buscar")
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            singleLine = true,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF2563EB),
                unfocusedBorderColor = Color.Gray
            ),
            shape = RoundedCornerShape(8.dp)
        )
        
        // Filter Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedButton(
                onClick = { showFilterDialog = true },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    Icons.Default.KeyboardArrowDown,
                    contentDescription = "Filtrar",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Filtrar")
            }

            Spacer(modifier = Modifier.width(12.dp))

            Text(
                text = "Resultados: $filteredCount",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
        
        HorizontalDivider(color = Color.LightGray, thickness = 0.5.dp)
        
        // Content
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (groupedTransactions.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        Icons.Default.List,
                        contentDescription = "Sin transacciones",
                        modifier = Modifier.size(64.dp),
                        tint = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No hay transacciones",
                        fontSize = 16.sp,
                        color = Color.Gray
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp),
                contentPadding = PaddingValues(vertical = 16.dp)
            ) {
                groupedTransactions.forEach { group ->
                    item {
                        Text(
                            text = group.dateLabel,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                    
                    items(group.transactions) { transaction ->
                        TransactionCard(transaction)
                    }
                }
            }
        }
        
        // Bottom Navigation Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.White)
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Inicio
            BottomNavItem(
                label = "Inicio",
                icon = Icons.Default.Home,
                isSelected = false,
                onClick = onNavigateToHome
            )
            
            // Transacciones
            BottomNavItem(
                label = "Transacciones",
                icon = Icons.Default.List,
                isSelected = currentTab == "Transacciones",
                onClick = {}
            )
            
            // Presupuestos
            BottomNavItem(
                label = "Presupuestos",
                icon = Icons.Default.AccountBox,
                isSelected = false,
                onClick = onNavigateToBudgets
            )
            
            // Perfil
            BottomNavItem(
                label = "Perfil",
                icon = Icons.Default.Person,
                isSelected = false,
                onClick = onNavigateToProfile
            )
        }
    }
    
    // Filter Dialog
    if (showFilterDialog) {
        FilterDialog(
            selectedType = selectedType,
            selectedCategories = selectedCategories,
            selectedPaymentMethods = selectedPaymentMethods,
            availableCategories = availableCategories,
            availablePaymentMethods = availablePaymentMethods,
            startDate = startDate,
            endDate = endDate,
            minAmount = minAmount,
            maxAmount = maxAmount,
            onDismiss = { showFilterDialog = false },
            onTypeSelected = { selectedType = it },
            onCategoryToggle = { category ->
                selectedCategories = if (selectedCategories.contains(category)) {
                    selectedCategories - category
                } else {
                    selectedCategories + category
                }
            },
            onPaymentMethodToggle = { method ->
                selectedPaymentMethods = if (selectedPaymentMethods.contains(method)) {
                    selectedPaymentMethods - method
                } else {
                    selectedPaymentMethods + method
                }
            },
            onStartDateChange = { startDate = it },
            onEndDateChange = { endDate = it },
            onMinAmountChange = { minAmount = it },
            onMaxAmountChange = { maxAmount = it },
            onClearFilters = {
                selectedType = null
                selectedCategories = emptySet<String>()
                selectedPaymentMethods = emptySet<String>()
                startDate = null
                endDate = null
                minAmount = ""
                maxAmount = ""
            }
        )
    }
}

@Composable
fun TransactionCard(transaction: Transaction) {
    val amountColor = if (transaction.type == "EXPENSE") Color(0xFFEF4444) else Color(0xFF10B981)
    val sign = if (transaction.type == "EXPENSE") "-" else "+"
    
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 1.dp
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon Circle
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFE3F2FD)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = transaction.icon,
                    fontSize = 24.sp
                )
            }
            
            Spacer(modifier = Modifier.width(12.dp))
            
            // Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = transaction.description,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = transaction.category,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = "Pago: ${transaction.paymentMethod}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            // Amount
            Text(
                text = "$sign$${String.format("%.2f", transaction.amount)}",
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                color = amountColor
            )
        }
    }
}

@Composable
fun BottomNavItem(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val iconColor = if (isSelected) Color(0xFF2563EB) else Color.Gray
    
    Column(
        modifier = Modifier
            .clickable { onClick() }
            .padding(horizontal = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = iconColor,
            modifier = Modifier.size(28.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = iconColor,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
fun FilterDialog(
    selectedType: String?,
    selectedCategories: Set<String>,
    selectedPaymentMethods: Set<String>,
    availableCategories: List<String>,
    availablePaymentMethods: List<String>,
    startDate: Long?,
    endDate: Long?,
    minAmount: String,
    maxAmount: String,
    onDismiss: () -> Unit,
    onTypeSelected: (String?) -> Unit,
    onCategoryToggle: (String) -> Unit,
    onPaymentMethodToggle: (String) -> Unit,
    onStartDateChange: (Long?) -> Unit,
    onEndDateChange: (Long?) -> Unit,
    onMinAmountChange: (String) -> Unit,
    onMaxAmountChange: (String) -> Unit,
    onClearFilters: () -> Unit
) {
    val scrollState = rememberScrollState()
    val dateFormat = remember { SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()) }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showEndDatePicker by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Filtrar Transacciones") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(max = 420.dp)
                    .verticalScroll(scrollState),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Tipo de transacción
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Tipo de transacción", fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            onClick = { onTypeSelected(if (selectedType == "INCOME") null else "INCOME") },
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp),
                            shape = RoundedCornerShape(8.dp),
                            color = if (selectedType == "INCOME") Color(0xFF10B981) else Color(0xFFF5F5F5)
                        ) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                Text(
                                    "Ingreso",
                                    fontSize = 12.sp,
                                    color = if (selectedType == "INCOME") Color.White else Color.Black
                                )
                            }
                        }

                        Surface(
                            onClick = { onTypeSelected(if (selectedType == "EXPENSE") null else "EXPENSE") },
                            modifier = Modifier
                                .weight(1f)
                                .height(40.dp),
                            shape = RoundedCornerShape(8.dp),
                            color = if (selectedType == "EXPENSE") Color(0xFFEF4444) else Color(0xFFF5F5F5)
                        ) {
                            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                                Text(
                                    "Gasto",
                                    fontSize = 12.sp,
                                    color = if (selectedType == "EXPENSE") Color.White else Color.Black
                                )
                            }
                        }
                    }
                }

                // Rango de fechas
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Rango de fechas", fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = { showStartDatePicker = true },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(startDate?.let { dateFormat.format(Date(it)) } ?: "Desde")
                        }

                        OutlinedButton(
                            onClick = { showEndDatePicker = true },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(endDate?.let { dateFormat.format(Date(it)) } ?: "Hasta")
                        }
                    }
                }

                // Rango de monto
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Rango de monto", fontWeight = FontWeight.Bold)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = minAmount,
                            onValueChange = onMinAmountChange,
                            label = { Text("Mínimo") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = maxAmount,
                            onValueChange = onMaxAmountChange,
                            label = { Text("Máximo") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                // Categorías
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Categorías", fontWeight = FontWeight.Bold)
                    if (availableCategories.isEmpty()) {
                        Text("Aún no hay categorías disponibles", fontSize = 12.sp, color = Color.Gray)
                    } else {
                        availableCategories.forEach { category ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = selectedCategories.contains(category),
                                    onCheckedChange = { onCategoryToggle(category) }
                                )
                                Text(category)
                            }
                        }
                    }
                }

                // Métodos de pago
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Métodos de pago", fontWeight = FontWeight.Bold)
                    if (availablePaymentMethods.isEmpty()) {
                        Text("Sin métodos de pago registrados", fontSize = 12.sp, color = Color.Gray)
                    } else {
                        availablePaymentMethods.forEach { method ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Checkbox(
                                    checked = selectedPaymentMethods.contains(method),
                                    onCheckedChange = { onPaymentMethodToggle(method) }
                                )
                                Text(method)
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onClearFilters) {
                Text("Limpiar todo")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cerrar")
            }
        }
    )

    if (showStartDatePicker) {
        val initialCalendar = startDate?.let {
            Calendar.getInstance().apply { timeInMillis = it }
        } ?: Calendar.getInstance()
        DatePickerDialog(
            onDismiss = { showStartDatePicker = false },
            initialDate = initialCalendar,
            onDateSelected = { calendar ->
                onStartDateChange(calendar.timeInMillis)
                showStartDatePicker = false
            }
        )
    }

    if (showEndDatePicker) {
        val initialCalendar = endDate?.let {
            Calendar.getInstance().apply { timeInMillis = it }
        } ?: Calendar.getInstance()
        DatePickerDialog(
            onDismiss = { showEndDatePicker = false },
            initialDate = initialCalendar,
            onDateSelected = { calendar ->
                onEndDateChange(calendar.timeInMillis)
                showEndDatePicker = false
            }
        )
    }
}

fun groupTransactionsByDate(transactions: List<Transaction>): List<TransactionGroup> {
    val groups = mutableListOf<TransactionGroup>()
    val calendar = Calendar.getInstance()
    val today = calendar.timeInMillis
    calendar.add(Calendar.DAY_OF_YEAR, -1)
    val yesterday = calendar.timeInMillis
    
    val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val todayStr = format.format(Date(today))
    val yesterdayStr = format.format(Date(yesterday))
    
    val todayTransactions = transactions.filter { 
        format.format(Date(it.date)) == todayStr 
    }
    
    val yesterdayTransactions = transactions.filter { 
        format.format(Date(it.date)) == yesterdayStr
    }
    
    val olderTransactions = transactions.filter { 
        val dateStr = format.format(Date(it.date))
        dateStr != todayStr && dateStr != yesterdayStr
    }.groupBy { format.format(Date(it.date)) }
    
    if (todayTransactions.isNotEmpty()) {
        groups.add(TransactionGroup("Hoy", todayTransactions))
    }
    
    if (yesterdayTransactions.isNotEmpty()) {
        groups.add(TransactionGroup("Ayer", yesterdayTransactions))
    }
    
    olderTransactions.forEach { (date, trans) ->
        groups.add(TransactionGroup(date, trans))
    }
    
    return groups
}

@Preview(showBackground = true)
@Composable
fun TransactionsScreenPreview() {
    MaterialTheme {
        TransactionsScreen()
    }
}


package com.example.proyecto_catedra.ui.screens.budgets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.verticalScroll
import java.util.*

data class Budget(
    val id: Long,
    val userId: String,
    val category: String,
    val amount: Double,
    val usedAmount: Double = 0.0,
    val icon: String = "💰",
    val description: String = ""
)

@Composable
fun BudgetsScreen(
    budgets: List<Budget> = emptyList(),
    onBack: () -> Unit = {},
    onNavigateToHome: () -> Unit = {},
    onNavigateToTransactions: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onSaveBudget: (category: String, icon: String, description: String, amount: Double) -> Unit = { _, _, _, _ -> },
    currentTab: String = "Presupuestos"
) {
    var showAddBudgetDialog by remember { mutableStateOf(false) }
    var selectedBudgetToEdit by remember { mutableStateOf<Budget?>(null) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Top Bar with Add Button
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
                text = "Presupuestos",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.weight(1f).padding(start = 8.dp)
            )
            
            IconButton(
                onClick = { showAddBudgetDialog = true }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Agregar presupuesto",
                    tint = Color(0xFF2563EB),
                    modifier = Modifier.size(28.dp)
                )
            }
        }
        
        // Content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Section Header
            Text(
                text = "Establecer Presupuestos Mensuales",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            
            // Budget Categories
            if (budgets.isEmpty()) {
                // Show empty state
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Agregar presupuesto",
                            modifier = Modifier.size(64.dp),
                            tint = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No hay presupuestos creados",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Haz clic en el botón + para agregar uno",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                    }
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(budgets) { budget ->
                        BudgetCategoryCard(
                            category = CategoryInfo(
                                budget.category,
                                budget.icon,
                                budget.description
                            ),
                            budgetAmount = budget.amount,
                            onClick = { },
                            onEditClick = { 
                                selectedBudgetToEdit = budget
                                showAddBudgetDialog = true 
                            }
                        )
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
                isSelected = false,
                onClick = onNavigateToTransactions
            )
            
            // Presupuestos
            BottomNavItem(
                label = "Presupuestos",
                icon = Icons.Default.AccountBox,
                isSelected = currentTab == "Presupuestos",
                onClick = {}
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
    
    // Add/Edit Budget Dialog
    if (showAddBudgetDialog) {
        AddEditBudgetDialog(
            budget = selectedBudgetToEdit,
            onDismiss = { 
                showAddBudgetDialog = false
                selectedBudgetToEdit = null
            },
            onSave = { category, icon, description, amount ->
                onSaveBudget(category, icon, description, amount)
                showAddBudgetDialog = false
                selectedBudgetToEdit = null
            }
        )
    }
}

data class CategoryInfo(
    val name: String,
    val icon: String,
    val description: String
)

@Composable
fun BudgetCategoryCard(
    category: CategoryInfo,
    budgetAmount: Double,
    onClick: () -> Unit,
    onEditClick: () -> Unit
) {
    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        color = Color(0xFFF5F5F5),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Text(
                text = category.icon,
                fontSize = 32.sp,
                modifier = Modifier.padding(end = 16.dp)
            )
            
            // Content
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = category.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = category.description,
                    fontSize = 12.sp,
                    color = Color.Gray
                )
            }
            
            // Budget Amount or Edit Icon
            if (budgetAmount > 0) {
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "$${String.format("%.2f", budgetAmount)}",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2563EB)
                    )
                    Text(
                        text = "Presupuesto",
                        fontSize = 10.sp,
                        color = Color.Gray
                    )
                }
            } else {
                IconButton(onClick = onEditClick) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Editar",
                        tint = Color(0xFF2563EB)
                    )
                }
            }
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditBudgetDialog(
    budget: Budget?,
    onDismiss: () -> Unit,
    onSave: (category: String, icon: String, description: String, amount: Double) -> Unit
) {
    var category by remember { mutableStateOf(budget?.category ?: "") }
    var icon by remember { mutableStateOf(budget?.icon ?: "💰") }
    var description by remember { mutableStateOf(budget?.description ?: "") }
    var amountText by remember { mutableStateOf(budget?.amount?.toString() ?: "") }
    var expandedIcon by remember { mutableStateOf(false) }
    
    val icons = listOf("💰", "🍕", "🚗", "🎬", "🛍️", "📄", "🏠", "💳", "📱", "🎮", "✈️", "🎓")
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = if (budget == null) "Agregar Presupuesto" else "Editar Presupuesto",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            
            // Icon Selection
            Column {
                Text(
                    text = "Icono",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(
                        onClick = { expandedIcon = true }
                    ) {
                        Text(text = icon, fontSize = 32.sp)
                        Icon(
                            Icons.Default.ArrowDropDown,
                            contentDescription = "Seleccionar",
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                
                if (expandedIcon) {
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 8.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = Color(0xFFF5F5F5)
                    ) {
                        LazyColumn(
                            modifier = Modifier
                                .height(150.dp)
                        ) {
                            items(icons) { selectedIcon ->
                                Surface(
                                    onClick = {
                                        icon = selectedIcon
                                        expandedIcon = false
                                    },
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(
                                        text = selectedIcon,
                                        fontSize = 24.sp,
                                        modifier = Modifier.padding(12.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            // Category Field
            OutlinedTextField(
                value = category,
                onValueChange = { category = it },
                label = { Text("Título/Categoría") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            // Description Field
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 2
            )
            
            // Amount Field
            OutlinedTextField(
                value = amountText,
                onValueChange = { amountText = it },
                label = { Text("Presupuesto Total") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                leadingIcon = { Text("$", fontSize = 18.sp, fontWeight = FontWeight.Bold) }
            )
            
            // Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = onDismiss,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Cancelar")
                }
                
                Button(
                    onClick = {
                        val amount = amountText.toDoubleOrNull() ?: 0.0
                        if (category.isNotEmpty() && amount > 0) {
                            onSave(category, icon, description, amount)
                        }
                    },
                    modifier = Modifier.weight(1f),
                    enabled = category.isNotEmpty() && amountText.isNotEmpty() && amountText.toDoubleOrNull() ?: 0.0 > 0
                ) {
                    Text(if (budget == null) "Agregar" else "Actualizar")
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun BudgetsScreenPreview() {
    MaterialTheme {
        BudgetsScreen()
    }
}

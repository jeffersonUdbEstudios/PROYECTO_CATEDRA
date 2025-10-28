package com.example.proyecto_catedra.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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

data class FinancialSummary(
    val availableBalance: Double = 0.0,
    val totalIncome: Double = 0.0,
    val totalExpenses: Double = 0.0
)

data class Alert(
    val id: String,
    val icon: String,
    val title: String,
    val message: String
)

@Composable
fun HomeScreen(
    userName: String = "Usuario",
    summary: FinancialSummary = FinancialSummary(
        availableBalance = 2345.0,
        totalIncome = 3500.0,
        totalExpenses = 1155.0
    ),
    alerts: List<Alert> = listOf(
        Alert("1", "🔔", "Alerta de Presupuesto de Comidas", "Has superado tu presupuesto de comidas por $50."),
        Alert("2", "📅 12", "Pago Próximo", "Pago de alquiler pendiente en 3 días.")
    ),
    onNavigateToProfile: () -> Unit = {},
    onNavigateToTransactions: () -> Unit = {},
    onNavigateToBudgets: () -> Unit = {},
    onAddTransaction: () -> Unit = {},
    onViewReports: () -> Unit = {},
    currentTab: String = "Inicio"
) {
    val lightBlue = Color(0xFFE3F2FD)
    val blue = Color(0xFF2563EB)
    val lightGray = Color(0xFFF5F5F5)
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Top Bar - "Pantalla inicio"
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Pantalla inicio",
                fontSize = 16.sp,
                fontWeight = FontWeight.Normal,
                color = Color.Gray
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // Header with Profile and Title
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile Picture
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(lightGray),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Perfil",
                    tint = Color.Gray,
                    modifier = Modifier.size(24.dp)
                )
            }
            
            Text(
                text = "Resumen",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            
            Spacer(modifier = Modifier.width(40.dp)) // Balance layout
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Summary Cards
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // First Row - Available Balance and Income
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Available Balance Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(100.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = lightGray
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 0.dp
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Saldo Disponible",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = "$${String.format("%.2f", summary.availableBalance)}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }
                
                // Income Card
                Card(
                    modifier = Modifier
                        .weight(1f)
                        .height(100.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = lightGray
                    ),
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 0.dp
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(12.dp),
                        verticalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Ingresos",
                            fontSize = 12.sp,
                            color = Color.Gray
                        )
                        Text(
                            text = "$${String.format("%.2f", summary.totalIncome)}",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }
                }
            }
            
            // Expense Card
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(100.dp),
                colors = CardDefaults.cardColors(
                    containerColor = lightGray
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 0.dp
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Gastos",
                        fontSize = 12.sp,
                        color = Color.Gray
                    )
                    Text(
                        text = "$${String.format("%.2f", summary.totalExpenses)}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Alerts Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Text(
                text = "Alertas",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            alerts.forEach { alert ->
                // Check if this is a budget alert (contains "Alerta de Presupuesto")
                val isBudgetAlert = alert.title.contains("Alerta de Presupuesto")
                val backgroundColor = if (isBudgetAlert) Color(0xFFFFEBEE) else Color.White
                val titleColor = if (isBudgetAlert) Color(0xFFC62828) else Color.Black
                val messageColor = if (isBudgetAlert) Color(0xFFD32F2F) else Color.Gray
                
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = backgroundColor
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
                        Text(
                            text = alert.icon,
                            fontSize = 24.sp,
                            modifier = Modifier.padding(end = 12.dp)
                        )
                        
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = alert.title,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = titleColor
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = alert.message,
                                fontSize = 12.sp,
                                color = messageColor,
                                fontWeight = if (isBudgetAlert) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Quick Actions Section
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
        ) {
            Text(
                text = "Acciones Rápidas",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.padding(bottom = 12.dp)
            )
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Add Transaction Button
                Button(
                    onClick = onAddTransaction,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = blue
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "Agregar Transacción",
                        fontWeight = FontWeight.Bold
                    )
                }
                
                // View Reports Button
                OutlinedButton(
                    onClick = onViewReports,
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color.Black
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        "Ver Informes",
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.weight(1f))
        
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
                isSelected = currentTab == "Inicio",
                onClick = {}
            )
            
            // Transacciones
            BottomNavItem(
                label = "Transacciones",
                icon = Icons.Default.List,
                isSelected = currentTab == "Transacciones",
                onClick = onNavigateToTransactions
            )
            
            // Presupuestos
            BottomNavItem(
                label = "Presupuestos",
                icon = Icons.Default.AccountBox,
                isSelected = currentTab == "Presupuestos",
                onClick = onNavigateToBudgets
            )
            
            // Perfil
            BottomNavItem(
                label = "Perfil",
                icon = Icons.Default.Person,
                isSelected = currentTab == "Perfil",
                onClick = onNavigateToProfile
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

@Preview(showBackground = true)
@Composable
fun HomeScreenPreview() {
    MaterialTheme {
        HomeScreen()
    }
}


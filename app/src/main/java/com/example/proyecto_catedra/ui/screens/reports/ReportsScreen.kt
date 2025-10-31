package com.example.proyecto_catedra.ui.screens.reports

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.TrendingDown
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.proyecto_catedra.ui.screens.transactions.Transaction
import kotlin.math.roundToInt

data class CategoryData(
    val category: String,
    val amount: Double,
    val percentage: Double
)

data class BudgetComparison(
    val category: String,
    val budgetAmount: Double,
    val actualAmount: Double,
    val usagePercentage: Double
)

@Composable
fun ReportsScreen(
    totalIncome: Double,
    totalExpenses: Double,
    categoryBreakdown: List<CategoryData>,
    topExpenses: List<Transaction>,
    budgetComparison: List<BudgetComparison>,
    onBack: () -> Unit
) {
    val scrollState = rememberScrollState()
    val compliancePercentage = remember(budgetComparison) {
        if (budgetComparison.isEmpty()) 0.0
        else {
            val withinBudget = budgetComparison.count { it.actualAmount <= it.budgetAmount }
            (withinBudget.toDouble() / budgetComparison.size.toDouble()) * 100
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "Informes",
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Regresar"
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = Color.White,
                titleContentColor = Color.Black,
                navigationIconContentColor = Color.Black
            )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 20.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            SummarySection(totalIncome = totalIncome, totalExpenses = totalExpenses)

            CategoryBreakdownSection(categoryBreakdown)

            TopExpensesSection(topExpenses)

            BudgetComparisonSection(budgetComparison)

            ComplianceSection(compliancePercentage = compliancePercentage)
        }
    }
}

@Composable
private fun SummarySection(totalIncome: Double, totalExpenses: Double) {
    val balance = totalIncome - totalExpenses
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Resumen mensual",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                SummaryChip(
                    title = "Ingresos",
                    amount = totalIncome,
                    background = Color(0xFFE3F2FD),
                    foreground = Color(0xFF2563EB)
                )

                SummaryChip(
                    title = "Gastos",
                    amount = totalExpenses,
                    background = Color(0xFFFFEBEE),
                    foreground = Color(0xFFD32F2F)
                )
            }

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                shape = RoundedCornerShape(12.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                ) {
                    Text(
                        text = "Balance",
                        fontSize = 14.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "$${String.format("%.2f", balance)}",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (balance >= 0) Color(0xFF10B981) else Color(0xFFEF4444)
                    )
                }
            }
        }
    }
}

@Composable
private fun SummaryChip(
    title: String,
    amount: Double,
    background: Color,
    foreground: Color
) {
    Card(
        modifier = Modifier
            .weight(1f)
            .height(110.dp),
        colors = CardDefaults.cardColors(containerColor = background),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = title, fontSize = 14.sp, color = foreground)
            Text(
                text = "$${String.format("%.2f", amount)}",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        }
    }
}

@Composable
private fun CategoryBreakdownSection(categoryBreakdown: List<CategoryData>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.List,
                    contentDescription = null,
                    tint = Color(0xFF2563EB)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Distribución por categorías",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            if (categoryBreakdown.isEmpty()) {
                EmptyState(text = "No hay gastos registrados este mes")
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    categoryBreakdown.forEach { categoryData ->
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = categoryData.category,
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "$${String.format("%.2f", categoryData.amount)}",
                                    fontSize = 14.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            LinearProgressIndicator(
                                progress = (categoryData.percentage / 100f).coerceIn(0f, 1f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp),
                                color = Color(0xFF2563EB)
                            )
                            Text(
                                text = "${categoryData.percentage.roundToInt()}% del total de gastos",
                                fontSize = 12.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun TopExpensesSection(topExpenses: List<Transaction>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.TrendingDown,
                    contentDescription = null,
                    tint = Color(0xFFEF4444)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Top 5 gastos",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            if (topExpenses.isEmpty()) {
                EmptyState(text = "Aún no hay gastos que mostrar")
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    topExpenses.take(5).forEachIndexed { index, transaction ->
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(Color(0xFFFFEBEE))
                                        .padding(horizontal = 12.dp, vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${index + 1}",
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFD32F2F)
                                    )
                                }
                                Spacer(modifier = Modifier.width(12.dp))
                                Column {
                                    Text(
                                        text = transaction.description,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = transaction.category,
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }
                            }
                            Text(
                                text = "-$${String.format("%.2f", transaction.amount)}",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFD32F2F)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun BudgetComparisonSection(budgetComparison: List<BudgetComparison>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.BarChart,
                    contentDescription = null,
                    tint = Color(0xFF2563EB)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Presupuesto vs gastado",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            if (budgetComparison.isEmpty()) {
                EmptyState(text = "No hay presupuestos configurados este mes")
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    budgetComparison.forEach { item ->
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = item.category,
                                        fontSize = 14.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "Presupuesto: $${String.format("%.2f", item.budgetAmount)}",
                                        fontSize = 12.sp,
                                        color = Color.Gray
                                    )
                                }
                                Text(
                                    text = "Gastado: $${String.format("%.2f", item.actualAmount)}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (item.actualAmount <= item.budgetAmount) Color(0xFF2563EB) else Color(0xFFD32F2F)
                                )
                            }
                            LinearProgressIndicator(
                                progress = (item.usagePercentage / 100f).coerceAtLeast(0f),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(6.dp),
                                color = if (item.actualAmount <= item.budgetAmount) Color(0xFF2563EB) else Color(0xFFD32F2F)
                            )
                            Text(
                                text = "${item.usagePercentage.roundToInt()}% del presupuesto",
                                fontSize = 12.sp,
                                color = if (item.actualAmount <= item.budgetAmount) Color.Gray else Color(0xFFD32F2F)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ComplianceSection(compliancePercentage: Double) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Cumplimiento de presupuestos",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = "${compliancePercentage.roundToInt()}%",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF2563EB)
            )

            LinearProgressIndicator(
                progress = (compliancePercentage / 100f).coerceIn(0f, 1f),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp),
                color = Color(0xFF2563EB)
            )

            Text(
                text = "Porcentaje de categorías dentro de su presupuesto",
                fontSize = 12.sp,
                color = Color.Gray
            )
        }
    }
}

@Composable
private fun EmptyState(text: String) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = text,
            fontSize = 14.sp,
            color = Color.Gray
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ReportsScreenPreview() {
    val sampleCategories = listOf(
        CategoryData("Comida", 320.5, 32.0),
        CategoryData("Transporte", 150.0, 15.0),
        CategoryData("Entretenimiento", 90.0, 9.0)
    )
    val sampleTransactions = listOf(
        Transaction(id = "1", description = "Cena", category = "Comida", amount = 45.0, type = "EXPENSE", date = System.currentTimeMillis(), paymentMethod = "Tarjeta"),
        Transaction(id = "2", description = "Gasolina", category = "Transporte", amount = 40.0, type = "EXPENSE", date = System.currentTimeMillis(), paymentMethod = "Efectivo"),
        Transaction(id = "3", description = "Cine", category = "Entretenimiento", amount = 30.0, type = "EXPENSE", date = System.currentTimeMillis(), paymentMethod = "Crédito")
    )
    val sampleBudgetComparison = listOf(
        BudgetComparison("Comida", 400.0, 320.0, 80.0),
        BudgetComparison("Transporte", 200.0, 150.0, 75.0),
        BudgetComparison("Entretenimiento", 120.0, 90.0, 75.0)
    )

    MaterialTheme {
        ReportsScreen(
            totalIncome = 2500.0,
            totalExpenses = 1200.0,
            categoryBreakdown = sampleCategories,
            topExpenses = sampleTransactions,
            budgetComparison = sampleBudgetComparison,
            onBack = {}
        )
    }
}



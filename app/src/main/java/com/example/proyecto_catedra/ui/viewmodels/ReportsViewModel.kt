package com.example.proyecto_catedra.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto_catedra.data.local.dao.BudgetDao
import com.example.proyecto_catedra.data.local.dao.TransactionDao
import com.example.proyecto_catedra.data.local.entities.TransactionEntity
import com.example.proyecto_catedra.ui.screens.reports.BudgetComparison
import com.example.proyecto_catedra.ui.screens.reports.CategoryData
import com.example.proyecto_catedra.ui.screens.transactions.Transaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar

data class ReportsUiState(
    val totalIncome: Double = 0.0,
    val totalExpenses: Double = 0.0,
    val categoryBreakdown: List<CategoryData> = emptyList(),
    val topExpenses: List<Transaction> = emptyList(),
    val budgetComparison: List<BudgetComparison> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

class ReportsViewModel(
    private val transactionDao: TransactionDao,
    private val budgetDao: BudgetDao,
    private val currentUserId: String
) : ViewModel() {

    private val _uiState = MutableStateFlow(ReportsUiState())
    val uiState: StateFlow<ReportsUiState> = _uiState.asStateFlow()

    init {
        loadReports()
    }

    private fun loadReports() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }

            val (month, year) = currentMonthYear()

            combine(
                transactionDao.getTransactionsByUser(currentUserId),
                budgetDao.getBudgetsForMonth(currentUserId, month, year)
            ) { transactions, budgets ->
                buildReportsState(transactions, budgets)
            }
                .catch { throwable ->
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = throwable.message ?: "Error al cargar los informes"
                        )
                    }
                }
                .collect { state ->
                    _uiState.value = state
                }
        }
    }

    private fun buildReportsState(
        transactions: List<TransactionEntity>,
        budgets: List<com.example.proyecto_catedra.data.local.entities.BudgetEntity>
    ): ReportsUiState {
        val (month, year) = currentMonthYear()

        val currentMonthTransactions = transactions.filter { entity ->
            isFromMonth(entity.date, month, year)
        }

        val monthlyIncome = currentMonthTransactions
            .filter { it.type.equals("INCOME", ignoreCase = true) }
            .sumOf { it.amount }

        val monthlyExpensesEntities = currentMonthTransactions
            .filter { it.type.equals("EXPENSE", ignoreCase = true) }

        val monthlyExpenses = monthlyExpensesEntities.sumOf { it.amount }

        val categoryBreakdown = buildCategoryBreakdown(monthlyExpensesEntities)
        val topExpenses = monthlyExpensesEntities
            .sortedByDescending { it.amount }
            .take(5)
            .map { entity ->
                Transaction(
                    id = entity.id.toString(),
                    description = entity.description,
                    category = entity.category,
                    amount = entity.amount,
                    type = entity.type,
                    date = entity.date,
                    paymentMethod = entity.paymentMethod,
                    icon = getIconForCategory(entity.category)
                )
            }

        val budgetComparison = budgets.map { budget ->
            val spent = monthlyExpensesEntities
                .filter { it.category.equals(budget.category, ignoreCase = true) }
                .sumOf { it.amount }

            val usagePercentage = if (budget.amount > 0) {
                (spent / budget.amount) * 100
            } else 0.0

            BudgetComparison(
                category = budget.category,
                budgetAmount = budget.amount,
                actualAmount = spent,
                usagePercentage = if (usagePercentage.isFinite()) usagePercentage else 0.0
            )
        }.sortedByDescending { it.usagePercentage }

        return ReportsUiState(
            totalIncome = monthlyIncome,
            totalExpenses = monthlyExpenses,
            categoryBreakdown = categoryBreakdown,
            topExpenses = topExpenses,
            budgetComparison = budgetComparison,
            isLoading = false,
            error = null
        )
    }

    private fun buildCategoryBreakdown(expenses: List<TransactionEntity>): List<CategoryData> {
        if (expenses.isEmpty()) return emptyList()

        val total = expenses.sumOf { it.amount }

        return expenses
            .groupBy { it.category }
            .map { (category, transactions) ->
                val amount = transactions.sumOf { it.amount }
                val percentage = if (total > 0) (amount / total) * 100 else 0.0

                CategoryData(
                    category = category,
                    amount = amount,
                    percentage = percentage
                )
            }
            .sortedByDescending { it.amount }
    }

    private fun currentMonthYear(): Pair<Int, Int> {
        val calendar = Calendar.getInstance()
        val month = calendar.get(Calendar.MONTH) + 1 // Calendar.MONTH es 0-based
        val year = calendar.get(Calendar.YEAR)
        return month to year
    }

    private fun isFromMonth(timestamp: Long, month: Int, year: Int): Boolean {
        val calendar = Calendar.getInstance().apply { timeInMillis = timestamp }
        val entityMonth = calendar.get(Calendar.MONTH) + 1
        val entityYear = calendar.get(Calendar.YEAR)
        return entityMonth == month && entityYear == year
    }

    private fun getIconForCategory(category: String): String {
        return when (category.lowercase()) {
            "supermercado", "comida", "alimentación" -> "🍽️"
            "renta", "vivienda", "alquiler" -> "🏠"
            "salario", "trabajo", "ingreso" -> "💼"
            "entretenimiento", "cine", "ocio" -> "🎬"
            "transporte", "movilidad" -> "🚗"
            "salud", "medicina" -> "🏥"
            "educación", "estudios" -> "📚"
            "servicios", "facturas" -> "💡"
            "compras", "shopping" -> "🛍️"
            else -> "💳"
        }
    }
}



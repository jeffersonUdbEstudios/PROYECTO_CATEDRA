package com.example.proyecto_catedra.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto_catedra.data.local.dao.BudgetDao
import com.example.proyecto_catedra.data.local.dao.TransactionDao
import com.example.proyecto_catedra.ui.screens.home.Alert
import com.example.proyecto_catedra.ui.screens.home.FinancialSummary
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.*

data class HomeUiState(
    val summary: FinancialSummary = FinancialSummary(),
    val alerts: List<Alert> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class HomeViewModel(
    private val transactionDao: TransactionDao,
    private val budgetDao: BudgetDao,
    private val currentUserId: String
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    
    init {
        loadSummary()
    }
    
    private fun loadSummary() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            
            try {
                // Get income and expenses from transactions
                val income = transactionDao.getTotalIncome(currentUserId).first() ?: 0.0
                val expenses = transactionDao.getTotalExpenses(currentUserId).first() ?: 0.0
                val availableBalance = income - expenses
                
                // Get all transactions
                val allTransactions = transactionDao.getTransactionsByUser(currentUserId).first()
                
                // Get all budgets
                val allBudgets = budgetDao.getAllBudgets(currentUserId).first()
                
                // Calculate expenses per category
                val expensesByCategory = allTransactions
                    .filter { it.type == "EXPENSE" }
                    .groupBy { it.category }
                    .mapValues { (_, transactions) -> transactions.sumOf { it.amount } }
                
                // Generate alerts for budgets at 50% or more
                val budgetAlerts = allBudgets.mapNotNull { budget ->
                    val spent = expensesByCategory[budget.category] ?: 0.0
                    val percentage = (spent / budget.amount) * 100
                    
                    if (percentage >= 50) {
                        val remaining = budget.amount - spent
                        Alert(
                            id = budget.id.toString(),
                            icon = "⚠️",
                            title = "Alerta de Presupuesto - ${budget.category}",
                            message = if (remaining > 0) {
                                "Te queda $${String.format("%.2f", remaining)} de presupuesto (${percentage.toInt()}% gastado)"
                            } else {
                                "Has agotado tu presupuesto de ${budget.category} ($${String.format("%.2f", spent)} gastado)"
                            }
                        )
                    } else null
                }
                
                _uiState.update { state ->
                    state.copy(
                        summary = FinancialSummary(
                            availableBalance = availableBalance,
                            totalIncome = income,
                            totalExpenses = expenses
                        ),
                        alerts = budgetAlerts,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        error = "Error al cargar datos: ${e.message}"
                    )
                }
            }
        }
    }
    
    fun refreshData() {
        loadSummary()
    }
}


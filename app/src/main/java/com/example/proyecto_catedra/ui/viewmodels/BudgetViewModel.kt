package com.example.proyecto_catedra.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto_catedra.data.local.dao.BudgetDao
import com.example.proyecto_catedra.data.local.entities.BudgetEntity
import com.example.proyecto_catedra.ui.screens.budgets.Budget
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*

data class BudgetUiState(
    val budgets: List<Budget> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

class BudgetViewModel(
    private val budgetDao: BudgetDao,
    private val currentUserId: String
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(BudgetUiState())
    val uiState: StateFlow<BudgetUiState> = _uiState.asStateFlow()
    
    init {
        loadBudgets()
    }
    
    private fun loadBudgets() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                budgetDao.getAllBudgets(currentUserId).collect { entities ->
                    val budgets = entities.map { entity ->
                        Budget(
                            id = entity.id,
                            userId = entity.userId,
                            category = entity.category,
                            amount = entity.amount,
                            usedAmount = 0.0, // TODO: Calculate from transactions
                            icon = entity.icon,
                            description = entity.description
                        )
                    }
                    
                    _uiState.update {
                        it.copy(
                            budgets = budgets,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
            }
        }
    }
    
    suspend fun setBudget(category: String, icon: String, description: String, amount: Double) {
        val calendar = Calendar.getInstance()
        val month = calendar.get(Calendar.MONTH) + 1
        val year = calendar.get(Calendar.YEAR)
        
        val budget = BudgetEntity(
            userId = currentUserId,
            category = category,
            amount = amount,
            month = month,
            year = year,
            icon = icon,
            description = description
        )
        
        budgetDao.insertBudget(budget)
        
        // Reload budgets after inserting
        loadBudgets()
    }
    
    suspend fun getCategories(): List<String> {
        val categories = mutableListOf<String>()
        try {
            budgetDao.getAllBudgets(currentUserId).collect { entities ->
                entities.forEach { categories.add(it.category) }
            }
        } catch (e: Exception) {
            // Return empty list
        }
        return categories
    }
}


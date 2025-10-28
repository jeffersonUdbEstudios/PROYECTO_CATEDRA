package com.example.proyecto_catedra.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto_catedra.data.local.dao.TransactionDao
import com.example.proyecto_catedra.data.local.entities.TransactionEntity
import com.example.proyecto_catedra.ui.screens.transactions.Transaction
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class TransactionsUiState(
    val transactions: List<Transaction> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)

class TransactionsViewModel(
    private val transactionDao: TransactionDao,
    private val currentUserId: String
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(TransactionsUiState())
    val uiState: StateFlow<TransactionsUiState> = _uiState.asStateFlow()
    
    init {
        loadTransactions()
    }
    
    private fun loadTransactions() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                transactionDao.getTransactionsByUser(currentUserId).collect { entities ->
                    val transactions = entities.map { entity ->
                        Transaction(
                            id = entity.id.toString(),
                            description = entity.description,
                            category = entity.category,
                            amount = entity.amount,
                            type = entity.type,
                            date = entity.date,
                            icon = getIconForCategory(entity.category)
                        )
                    }
                    
                    _uiState.update {
                        it.copy(
                            transactions = transactions,
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
    
    private fun getIconForCategory(category: String): String {
        return when (category.lowercase()) {
            "supermercado", "comida" -> "🛒"
            "renta", "vivienda" -> "🏠"
            "salario", "trabajo" -> "💼"
            "entretenimiento", "cine" -> "🎬"
            "transporte" -> "🚗"
            "salud" -> "🏥"
            "educación" -> "📚"
            "otros" -> "💰"
            else -> "💳"
        }
    }
}


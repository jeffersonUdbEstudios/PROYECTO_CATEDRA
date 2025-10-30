package com.example.proyecto_catedra.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto_catedra.data.local.dao.TransactionDao
import com.example.proyecto_catedra.data.local.entities.TransactionEntity
import com.example.proyecto_catedra.ui.screens.transactions.TransactionType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AddTransactionUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val isSaved: Boolean = false
)

class AddTransactionViewModel(
    private val transactionDao: TransactionDao,
    private val currentUserId: String
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AddTransactionUiState())
    val uiState: StateFlow<AddTransactionUiState> = _uiState.asStateFlow()
    
    fun saveTransaction(
        type: TransactionType,
        amount: Double,
        category: String,
        description: String,
        date: Long,
        paymentMethod: String = "Efectivo"
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                val transaction = TransactionEntity(
                    userId = currentUserId,
                    amount = amount,
                    description = description,
                    type = type.name,
                    category = category,
                    paymentMethod = paymentMethod,
                    date = date
                )
                
                transactionDao.insertTransaction(transaction)
                
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isSaved = true
                    )
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
}


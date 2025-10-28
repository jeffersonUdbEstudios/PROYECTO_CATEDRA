package com.example.proyecto_catedra.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto_catedra.data.local.dao.TransactionDao
import com.example.proyecto_catedra.data.local.dao.UserDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class ProfileUiState(
    val userName: String = "",
    val userEmail: String = "",
    val income: Double = 0.0,
    val expenses: Double = 0.0,
    val isLoading: Boolean = false,
    val error: String? = null
)

class ProfileViewModel(
    private val userDao: UserDao,
    private val transactionDao: TransactionDao,
    private val currentUserId: String
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(ProfileUiState())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()
    
    init {
        loadProfileData()
    }
    
    fun loadProfileData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            
            try {
                // Load user info
                val user = userDao.getUserById(currentUserId).first()
                
                // Load income and expenses
                val income = transactionDao.getTotalIncome(currentUserId).first() ?: 0.0
                val expenses = transactionDao.getTotalExpenses(currentUserId).first() ?: 0.0
                
                _uiState.value = ProfileUiState(
                    userName = user?.name ?: "",
                    userEmail = user?.email ?: "",
                    income = income,
                    expenses = expenses,
                    isLoading = false,
                    error = null
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error al cargar datos: ${e.message}"
                )
            }
        }
    }
    
    fun refreshProfileData() {
        loadProfileData()
    }
}

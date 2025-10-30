package com.example.proyecto_catedra

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.example.proyecto_catedra.data.local.AppDatabase
import com.example.proyecto_catedra.ui.screens.auth.LoginScreen
import com.example.proyecto_catedra.ui.screens.auth.RegisterScreen
import com.example.proyecto_catedra.ui.screens.home.HomeScreen
import com.example.proyecto_catedra.ui.screens.profile.ProfileScreen
import com.example.proyecto_catedra.ui.screens.transactions.AddTransactionScreen
import com.example.proyecto_catedra.ui.screens.transactions.TransactionsScreen
import com.example.proyecto_catedra.ui.screens.budgets.BudgetsScreen
import com.example.proyecto_catedra.ui.screens.news.NewsScreen
import com.example.proyecto_catedra.ui.theme.PROYECTO_CATEDRATheme
import com.example.proyecto_catedra.ui.viewmodels.AuthViewModel
import com.example.proyecto_catedra.ui.viewmodels.ProfileViewModel
import com.example.proyecto_catedra.ui.viewmodels.HomeViewModel
import com.example.proyecto_catedra.ui.viewmodels.NewsViewModel
import com.example.proyecto_catedra.ui.viewmodels.TransactionsViewModel
import com.example.proyecto_catedra.ui.viewmodels.BudgetViewModel
import com.example.proyecto_catedra.ui.viewmodels.AddTransactionViewModel
import com.example.proyecto_catedra.ui.screens.transactions.TransactionType

class MainActivity : ComponentActivity() {
    
    private lateinit var database: AppDatabase
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        
        // Initialize Room Database
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "proyecto_catedra_db"
        )
        .addMigrations(
            AppDatabase.MIGRATION_1_2,
            AppDatabase.MIGRATION_2_3,
            AppDatabase.MIGRATION_3_4,
            AppDatabase.MIGRATION_4_5
        )
        .fallbackToDestructiveMigration()
        .build()
        
        setContent {
            PROYECTO_CATEDRATheme {
                AppNavigation(database)
            }
        }
    }
}

data class AuthCredentials(
    val email: String = "",
    val password: String = ""
)

@Composable
fun AppNavigation(
    database: AppDatabase,
    authViewModel: AuthViewModel = viewModel { 
        AuthViewModel(database.userDao()) 
    }
) {
    val authState by authViewModel.authState.collectAsState()
    
    // Create a single BudgetViewModel instance that persists across screens
    val currentUserId = authState.currentUserId
    val budgetViewModel = remember(currentUserId) {
        if (currentUserId != null) {
            BudgetViewModel(database.budgetDao(), currentUserId)
        } else null
    }
    
    var showRegister by remember { mutableStateOf(false) }
    var currentScreen by remember { mutableStateOf("Home") }
    var credentials by remember { mutableStateOf(AuthCredentials()) }
    var showAddTransaction by remember { mutableStateOf(false) }
    var showNews by remember { mutableStateOf(false) }
    var showTransactions by remember { mutableStateOf(false) }
    var showBudgets by remember { mutableStateOf(false) }
    
    val isAuthenticated = authState.isAuthenticated
    
    // Listen for authentication changes and navigate accordingly
    LaunchedEffect(authState.isAuthenticated) {
        if (authState.isAuthenticated) {
            currentScreen = "Home"
            showRegister = false
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        if (!isAuthenticated) {
            if (showRegister) {
                RegisterScreen(
                    onNavigateToLogin = {
                        showRegister = false
                    },
                    onRegisterClick = { fullName, email, password ->
                        credentials = AuthCredentials(email, password)
                        authViewModel.register(fullName, email, password)
                        // Will navigate to home automatically when authState.isAuthenticated becomes true
                    },
                    isLoading = authState.isLoading,
                    errorMessage = authState.error ?: ""
                )
            } else {
                LoginScreen(
                    email = credentials.email,
                    onNavigateToRegister = {
                        showRegister = true
                    },
                    onLoginClick = { email, password ->
                        authViewModel.login(email, password)
                    },
                    errorMessage = authState.error ?: "",
                    isLoading = authState.isLoading
                )
            }
        } else {
            // User is authenticated - show main screens
            if (showNews) {
                val newsViewModel = remember { NewsViewModel() }
                val newsState by newsViewModel.uiState.collectAsState()
                
                NewsScreen(
                    articles = newsState.articles,
                    isLoading = newsState.isLoading,
                    onBack = { showNews = false },
                    onNavigateToHome = { 
                        showNews = false
                        currentScreen = "Home"
                    },
                    onNavigateToTransactions = { 
                        showNews = false
                        currentScreen = "Transactions" 
                    },
                    onNavigateToBudgets = { 
                        showNews = false
                        currentScreen = "Budgets" 
                    },
                    onNavigateToProfile = { 
                        showNews = false
                        currentScreen = "Profile" 
                    },
                    currentTab = "News"
                )
            } else if (showTransactions) {
                val transactionsViewModel = remember {
                    TransactionsViewModel(
                        database.transactionDao(),
                        authState.currentUserId ?: ""
                    )
                }
                val transactionsState by transactionsViewModel.uiState.collectAsState()
                
                TransactionsScreen(
                    transactions = transactionsState.transactions,
                    isLoading = transactionsState.isLoading,
                    onBack = { 
                        showTransactions = false
                        currentScreen = "Home"
                    },
                    onNavigateToHome = { 
                        showTransactions = false
                        currentScreen = "Home"
                    },
                    onNavigateToBudgets = { 
                        showTransactions = false
                        showBudgets = true
                    },
                    onNavigateToProfile = { 
                        showTransactions = false
                        currentScreen = "Profile" 
                    },
                    currentTab = "Transacciones"
                )
            } else if (showBudgets) {
                if (budgetViewModel == null) {
                    // Show loading or empty state
                    Box(
                        modifier = Modifier.fillMaxSize(), 
                        contentAlignment = androidx.compose.ui.Alignment.Center
                    ) {
                        androidx.compose.material3.Text("Cargando...")
                    }
                } else {
                val budgetState by budgetViewModel.uiState.collectAsState()
                val budgetScope = rememberCoroutineScope()
                
                BudgetsScreen(
                    budgets = budgetState.budgets,
                    onBack = { 
                        showBudgets = false
                        currentScreen = "Home"
                    },
                    onNavigateToHome = { 
                        showBudgets = false
                        currentScreen = "Home"
                    },
                    onNavigateToTransactions = { 
                        showBudgets = false
                        showTransactions = true
                    },
                    onNavigateToProfile = { 
                        showBudgets = false
                        currentScreen = "Profile" 
                    },
                    onSaveBudget = { category, icon, description, amount ->
                        budgetScope.launch {
                            budgetViewModel.setBudget(category, icon, description, amount)
                        }
                    },
                    currentTab = "Presupuestos"
                )
                }
            } else if (showAddTransaction) {
                val addTransactionViewModel = remember {
                    AddTransactionViewModel(
                        database.transactionDao(),
                        authState.currentUserId ?: ""
                    )
                }
                val addTransactionState by addTransactionViewModel.uiState.collectAsState()
                
                // Get available categories from the shared budgetViewModel
                val availableCategories = if (budgetViewModel != null) {
                    val budgetState by budgetViewModel.uiState.collectAsState()
                    budgetState.budgets.map { it.category }
                } else {
                    emptyList()
                }
                
                AddTransactionScreen(
                    availableCategories = availableCategories,
                    onBack = { showAddTransaction = false },
                    onSave = { type, amount, category, description, date, paymentMethod ->
                        addTransactionViewModel.saveTransaction(
                            type = type,
                            amount = amount,
                            category = category,
                            description = description,
                            date = date,
                            paymentMethod = paymentMethod
                        )
                        
                        // Wait a bit for the save to complete, then close
                        if (addTransactionState.isSaved) {
                            showAddTransaction = false
                        }
                    }
                )
                
                // Close when saved
                LaunchedEffect(addTransactionState.isSaved) {
                    if (addTransactionState.isSaved) {
                        showAddTransaction = false
                    }
                }
            } else when (currentScreen) {
                "Home" -> {
                    // Create HomeViewModel for the current user
                    val homeViewModel = remember {
                    HomeViewModel(
                        database.transactionDao(),
                        database.budgetDao(),
                        authState.currentUserId ?: ""
                    )
                    }
                    val homeState by homeViewModel.uiState.collectAsState()
                    
                    HomeScreen(
                    summary = homeState.summary,
                    alerts = homeState.alerts,
                    onNavigateToProfile = { currentScreen = "Profile" },
                    onNavigateToTransactions = { 
                        currentScreen = "Transactions"
                        showTransactions = true
                    },
                    onNavigateToBudgets = { 
                        currentScreen = "Budgets"
                        showBudgets = true
                    },
                    onAddTransaction = { showAddTransaction = true },
                    onViewReports = { showNews = true },
                    currentTab = "Inicio"
                )
                }
                "Profile" -> {
                    // Get profile data for current user
                    val profileViewModel = remember {
                        ProfileViewModel(
                            database.userDao(),
                            database.transactionDao(),
                            authState.currentUserId ?: ""
                        )
                    }
                    val profileState by profileViewModel.uiState.collectAsState()
                    
                    ProfileScreen(
                        userName = profileState.userName.ifEmpty { authState.name ?: "Usuario" },
                        userEmail = profileState.userEmail.ifEmpty { authState.email ?: "usuario@email.com" },
                        income = profileState.income,
                        expenses = profileState.expenses,
                        isLoading = profileState.isLoading,
                        onLogout = {
                            authViewModel.logout()
                            currentScreen = "Home"
                            credentials = AuthCredentials()
                        },
                        onNavigateToHome = { currentScreen = "Home" },
                        onNavigateToTransactions = { 
                            currentScreen = "Transactions"
                            showTransactions = true
                        },
                        onNavigateToBudgets = { 
                            currentScreen = "Budgets"
                            showBudgets = true
                        },
                        currentTab = "Perfil"
                    )
                }
                else -> HomeScreen(
                    summary = com.example.proyecto_catedra.ui.screens.home.FinancialSummary(
                        availableBalance = 2345.0,
                        totalIncome = 3500.0,
                        totalExpenses = 1155.0
                    ),
                    alerts = listOf(
                        com.example.proyecto_catedra.ui.screens.home.Alert("1", "🔔", "Alerta de Presupuesto de Comidas", "Has superado tu presupuesto de comidas por $50."),
                        com.example.proyecto_catedra.ui.screens.home.Alert("2", "📅 12", "Pago Próximo", "Pago de alquiler pendiente en 3 días.")
                    ),
                    onNavigateToProfile = { currentScreen = "Profile" },
                    onNavigateToTransactions = { 
                        currentScreen = "Transactions"
                        showTransactions = true
                    },
                    onNavigateToBudgets = { 
                        currentScreen = "Budgets"
                        showBudgets = true
                    },
                    onAddTransaction = { /* TODO */ },
                    onViewReports = { /* TODO */ },
                    currentTab = "Inicio"
                )
            }
        }
    }
}
package com.example.proyecto_catedra.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto_catedra.data.local.dao.UserDao
import com.example.proyecto_catedra.data.local.entities.UserEntity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

data class AuthState(
    val isAuthenticated: Boolean = false,
    val currentUserId: String? = null,
    val email: String? = null,
    val name: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

class AuthViewModel(
    private val userDao: UserDao,
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {
    
    private val _authState = MutableStateFlow(AuthState())
    val authState: StateFlow<AuthState> = _authState.asStateFlow()
    
    init {
        // Listen for auth state changes
        auth.addAuthStateListener { firebaseAuth ->
            val user = firebaseAuth.currentUser
            _authState.update { state ->
                state.copy(
                    isAuthenticated = user != null,
                    currentUserId = user?.uid,
                    email = user?.email,
                    name = user?.displayName
                )
            }
        }
        
        // Check if user is already logged in
        val currentUser = auth.currentUser
        if (currentUser != null) {
            _authState.update { state ->
                state.copy(
                    isAuthenticated = true,
                    currentUserId = currentUser.uid,
                    email = currentUser.email,
                    name = currentUser.displayName
                )
            }
        }
    }
    
    // Login with Firebase
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _authState.update { it.copy(isLoading = true, error = null) }
            
            try {
                val result = auth.signInWithEmailAndPassword(email, password).await()
                val firebaseUser = result.user
                
                firebaseUser?.let { user ->
                    // Get or create user in local database
                    val localUser = userDao.getUserByIdSync(user.uid)
                    
                    if (localUser == null) {
                        // Create user in local database
                        val newUser = UserEntity(
                            uid = user.uid,
                            name = user.displayName ?: email.substringBefore("@"),
                            email = user.email ?: email,
                            photoUrl = user.photoUrl?.toString()
                        )
                        userDao.insertUser(newUser)
                    }
                    
                    _authState.update { state ->
                        state.copy(
                            isAuthenticated = true,
                            currentUserId = user.uid,
                            email = user.email,
                            name = localUser?.name ?: user.displayName,
                            isLoading = false,
                            error = null
                        )
                    }
                }
            } catch (e: Exception) {
                _authState.update { state ->
                    state.copy(
                        isLoading = false,
                        error = "Error al iniciar sesión: ${getErrorMessage(e)}"
                    )
                }
            }
        }
    }
    
    // Register with Firebase
    fun register(fullName: String, email: String, password: String) {
        viewModelScope.launch {
            _authState.update { it.copy(isLoading = true, error = null) }
            
            try {
                val result = auth.createUserWithEmailAndPassword(email, password).await()
                val firebaseUser = result.user
                
                firebaseUser?.let { user ->
                    // Update display name
                    val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                        .setDisplayName(fullName)
                        .build()
                    
                    user.updateProfile(profileUpdates).await()
                    
                    // Create user in local database
                    val localUser = UserEntity(
                        uid = user.uid,
                        name = fullName,
                        email = email,
                        photoUrl = null
                    )
                    userDao.insertUser(localUser)
                    
                    _authState.update { state ->
                        state.copy(
                            isAuthenticated = true,
                            currentUserId = user.uid,
                            email = email,
                            name = fullName,
                            isLoading = false,
                            error = null
                        )
                    }
                }
            } catch (e: Exception) {
                _authState.update { state ->
                    state.copy(
                        isLoading = false,
                        error = "Error al registrar: ${getErrorMessage(e)}"
                    )
                }
            }
        }
    }
    
    // Logout from Firebase
    fun logout() {
        auth.signOut()
        _authState.update { state ->
            state.copy(
                isAuthenticated = false,
                currentUserId = null,
                email = null,
                name = null,
                isLoading = false,
                error = null
            )
        }
    }
    
    /**
     * Actualiza el perfil del usuario con datos académicos
     * 
     * Este método debe ser usado por PERSONA 2 en EditProfileScreen.kt
     * 
     * @param name Nombre completo del estudiante
     * @param universidad Nombre de la universidad
     * @param carrera Carrera universitaria
     * @param semestre Semestre o año actual (ejemplo: "1er Semestre", "3er Año")
     * 
     * Ejemplo de uso:
     * authViewModel.updateUserProfile(
     *     name = "Juan Pérez",
     *     universidad = "Universidad de El Salvador",
     *     carrera = "Ingeniería en Sistemas",
     *     semestre = "5to Semestre"
     * )
     */
    fun updateUserProfile(
        name: String,
        universidad: String?,
        carrera: String?,
        semestre: String?
    ) {
        viewModelScope.launch {
            _authState.update { it.copy(isLoading = true, error = null) }
            
            try {
                val userId = _authState.value.currentUserId
                if (userId == null) {
                    _authState.update { it.copy(
                        isLoading = false,
                        error = "Usuario no autenticado"
                    )}
                    return@launch
                }
                
                // Actualizar en base de datos local
                userDao.updateUserProfile(
                    userId = userId,
                    name = name,
                    universidad = universidad,
                    carrera = carrera,
                    semestre = semestre
                )
                
                // Actualizar displayName en Firebase si cambió el nombre
                val currentUser = auth.currentUser
                if (currentUser != null && currentUser.displayName != name) {
                    val profileUpdates = com.google.firebase.auth.UserProfileChangeRequest.Builder()
                        .setDisplayName(name)
                        .build()
                    currentUser.updateProfile(profileUpdates).await()
                }
                
                // Actualizar el estado local
                _authState.update { state ->
                    state.copy(
                        name = name,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _authState.update { state ->
                    state.copy(
                        isLoading = false,
                        error = "Error al actualizar perfil: ${e.message}"
                    )
                }
            }
        }
    }
    
    // Get user-friendly error message
    private fun getErrorMessage(exception: Exception): String {
        return when (exception.message) {
            "The email address is badly formatted." -> "El correo electrónico no es válido"
            "The password is too weak." -> "La contraseña es muy débil"
            "An account already exists with the same email address but different sign-in credentials." -> "Ya existe una cuenta con este correo"
            "A network error (such as timeout, interrupted connection or unreachable host) has occurred." -> "Error de conexión. Verifica tu internet"
            "The user account has been disabled by an administrator." -> "Tu cuenta ha sido deshabilitada"
            "There is no user record corresponding to this identifier. The user may have been deleted." -> "No existe una cuenta con este correo"
            "The password is invalid or the user does not have a password." -> "Contraseña incorrecta"
            else -> exception.message ?: "Error desconocido"
        }
    }
}


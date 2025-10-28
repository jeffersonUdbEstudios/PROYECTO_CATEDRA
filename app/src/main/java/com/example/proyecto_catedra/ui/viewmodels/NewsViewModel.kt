package com.example.proyecto_catedra.ui.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.proyecto_catedra.data.remote.models.NewsArticle
import com.example.proyecto_catedra.data.remote.RetrofitClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class NewsUiState(
    val articles: List<NewsArticle> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class NewsViewModel(
    private val newsService: com.example.proyecto_catedra.data.remote.NewsService = RetrofitClient.newsService
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(NewsUiState())
    val uiState: StateFlow<NewsUiState> = _uiState.asStateFlow()
    
    init {
        loadNews()
    }
    
    fun loadNews() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, error = null) }
            
            try {
                val response = newsService.getNews(limit = 10, offset = 0)
                if (response.isSuccessful && response.body() != null) {
                    _uiState.update { state ->
                        state.copy(
                            articles = response.body()!!.news,
                            isLoading = false,
                            error = null
                        )
                    }
                } else {
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            error = "Error al cargar noticias: ${response.message()}"
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        error = "Error al cargar noticias: ${e.message}"
                    )
                }
            }
        }
    }
    
    fun refreshNews() {
        loadNews()
    }
}


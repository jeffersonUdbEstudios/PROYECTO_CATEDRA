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
import kotlinx.coroutines.tasks.await
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions

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
                    val original = response.body()!!.news
                    // Traducir titular y resumen al español en dispositivo
                    val options = TranslatorOptions.Builder()
                        .setSourceLanguage(TranslateLanguage.ENGLISH)
                        .setTargetLanguage(TranslateLanguage.SPANISH)
                        .build()
                    val translator = Translation.getClient(options)
                    try {
                        // Asegura que el modelo esté disponible (descarga si hace falta)
                        translator.downloadModelIfNeeded().await()
                        val translated = original.map { art ->
                            val h = translator.translate(art.headline).await()
                            val a = translator.translate(art.abstract).await()
                            art.copy(headline = h, abstract = a)
                        }
                        _uiState.update { state ->
                            state.copy(
                                articles = translated,
                                isLoading = false,
                                error = null
                            )
                        }
                    } finally {
                        translator.close()
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


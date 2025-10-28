package com.example.proyecto_catedra.data.remote

import com.example.proyecto_catedra.data.remote.models.NewsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsService {
    @GET("news/sport")
    suspend fun getNews(
        @Query("limit") limit: Int = 10,
        @Query("offset") offset: Int = 0
    ): Response<NewsResponse>
}


package com.example.proyecto_catedra.data.remote.models

import com.google.gson.annotations.SerializedName

data class NewsResponse(
    @SerializedName("news")
    val news: List<NewsArticle>,
    @SerializedName("count")
    val count: Int,
    @SerializedName("next")
    val next: Int?,
    @SerializedName("eof")
    val eof: Boolean
)

data class NewsArticle(
    @SerializedName("id")
    val id: Int,
    @SerializedName("headline")
    val headline: String,
    @SerializedName("abstract")
    val abstract: String,
    @SerializedName("body")
    val body: String,
    @SerializedName("author")
    val author: String,
    @SerializedName("section")
    val section: String,
    @SerializedName("date")
    val date: String,
    @SerializedName("article_uri")
    val articleUri: String,
    @SerializedName("pfd_uri")
    val pdfUri: String
)


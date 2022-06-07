package com.example.news.data_class

data class NewsResponce(
    val articles: MutableList<Article>,
    val status: String,
    val totalResults: Int
)
package com.example.news

import com.example.news.api.RetrofitInstance
import com.example.news.data_class.Article
import com.example.news.database.NewsDatabase

class NewsRepository(val database: NewsDatabase) {
    suspend fun getBreakingNews(country: String, page: Int) =
        RetrofitInstance.api.getBreakingNews(country, page)

    suspend fun searchBreakingNews(country: String, page: Int) =
        RetrofitInstance.api.getSearchedNews(country, page)

    suspend fun insertArticle(article: Article) =
        database.getArticlesDAO().insertArticles(article)

    suspend fun deleteArticle(article: Article) =
        database.getArticlesDAO().deleteArticles(article)

    fun getSavedArticles() = database.getArticlesDAO().getAllArticles()

}
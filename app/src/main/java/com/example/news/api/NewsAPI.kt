package com.example.news.api

import com.example.news.data_class.NewsResponce
import com.example.news.Constance.Companion.API_KEY
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NewsAPI {

    @GET("v2/top-headlines")
    suspend fun getBreakingNews(
        @Query("country") myCountry: String = "in",
        @Query("page") myPage: Int = 1,
        @Query("apiKey") myApiKey: String = API_KEY
    ) : Response<NewsResponce>

    @GET("v2/everything")
    suspend fun getSearchedNews(
        @Query("q") searchQuery: String,
        @Query("page") myPage: Int = 1,
        @Query("apiKey") myApiKey: String = API_KEY
    ) : Response<NewsResponce>
}
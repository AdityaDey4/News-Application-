package com.example.news.viewmodel

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.ConnectivityManager.*
import android.net.NetworkCapabilities.*
import android.os.Build
import androidx.annotation.BoolRes
import androidx.core.graphics.component1
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.news.MyApplication
import com.example.news.NewsActivity
import com.example.news.NewsRepository
import com.example.news.Resource
import com.example.news.data_class.Article
import com.example.news.data_class.NewsResponce
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import retrofit2.Response
import java.io.IOException

class NewsViewModel(
    val app: Application,
    val newsRepository: NewsRepository
): AndroidViewModel(app) {

    val breakingNews: MutableLiveData<Resource<NewsResponce>> = MutableLiveData()
    var breakingNewsPage = 1
    var breakingNewsResponse: NewsResponce? = null

    val searchNews: MutableLiveData<Resource<NewsResponce>> = MutableLiveData()
    var searchNewsPage = 1
    var searchNewsResponce: NewsResponce? = null
    init {
        getBreakingNews("in")
    }


    fun getBreakingNews(country: String) = viewModelScope.launch {
        safeBreakingNewsCall(country)
    }
    private suspend fun safeBreakingNewsCall(country: String){

        breakingNews.postValue(Resource.Loading())
        try{
            if(hasInternetConnection()){
                val responce: Response<NewsResponce> = newsRepository.getBreakingNews(country, breakingNewsPage)
                breakingNews.postValue(handleBreakingNewsResponce(responce))
            }else{
                breakingNews.postValue(Resource.Error("No Internet Connection"))
            }
        }catch (t : Throwable){
            when(t){
                is IOException -> breakingNews.postValue(Resource.Error("Network Failure"))
                else -> breakingNews.postValue(Resource.Error("Conversion Error"))
            }
        }
    }
    private fun handleBreakingNewsResponce(responce: Response<NewsResponce>): Resource<NewsResponce>? {
        if(responce.isSuccessful){
            responce.body()?.let { newsResponce->
                breakingNewsPage++
                if(breakingNewsResponse == null){
                   breakingNewsResponse = newsResponce
                }else{
                    val oldArticle = breakingNewsResponse?.articles
                    val newArticle = newsResponce.articles

                    oldArticle?.addAll(newArticle)
                }
                return Resource.Success(breakingNewsResponse ?: newsResponce)
            }
        }
        return Resource.Error(responce.message())
    }

    fun getSearchNews(q: String) = viewModelScope.launch {
        safeSearchNewsCall(q)
    }
    suspend fun safeSearchNewsCall(q: String){
        searchNews.postValue(Resource.Loading())
        try{
            if(hasInternetConnection()){
                val response = newsRepository.searchBreakingNews(q, searchNewsPage)
                searchNews.postValue(handleSearchNewsResponce(response))
            }else{
                searchNews.postValue(Resource.Error("No Internet Connection"))
            }
        }catch(t: Throwable){
            when(t){
                is IOException -> searchNews.postValue(Resource.Error("Network Failure"))
                else -> searchNews.postValue(Resource.Error("Conversion Problem"))
            }
        }
    }
    fun handleSearchNewsResponce(responce: Response<NewsResponce>): Resource<NewsResponce>{
        if(responce.isSuccessful){
            responce.body()?.let { newsResponce->
                searchNewsPage++
                if(searchNewsResponce == null){
                    searchNewsResponce = newsResponce
                }else{
                    val oldArticle = searchNewsResponce?.articles
                    val newArticle = newsResponce.articles

                    oldArticle?.addAll(newArticle)
                }
                return Resource.Success(searchNewsResponce ?: newsResponce)
            }
        }
        return Resource.Error(responce.message())
    }

    private fun hasInternetConnection(): Boolean{
        val connectivityManager = getApplication<MyApplication>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            val activeNetwotk = connectivityManager.activeNetwork ?: return false
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwotk) ?: return false

            return when{
                capabilities.hasTransport(TRANSPORT_WIFI) -> true
                capabilities.hasTransport(TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(TRANSPORT_ETHERNET) -> true
                else -> false
            }
        }else{
            connectivityManager.activeNetworkInfo?.run {
                return when(type){
                    TYPE_WIFI -> true
                    TYPE_ETHERNET -> true
                    TYPE_MOBILE -> true
                    else -> false
                }
            }
        }

        return false
    }
    fun insertArticle(article: Article) = viewModelScope.launch {
        newsRepository.insertArticle(article)
    }

    fun deleteArticle(article: Article) = viewModelScope.launch {
       newsRepository.deleteArticle(article)
    }

    fun getSavedNews() = newsRepository.getSavedArticles()
}
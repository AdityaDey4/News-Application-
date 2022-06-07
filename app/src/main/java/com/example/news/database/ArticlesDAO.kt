package com.example.news.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.news.data_class.Article

@Dao
interface ArticlesDAO {

    @Insert(onConflict = OnConflictStrategy.REPLACE)//used to update
    suspend fun insertArticles(article: Article): Long

    @Delete
    suspend fun deleteArticles(article: Article)

    @Query("SELECT * FROM ARTICLES")
    fun getAllArticles(): LiveData<List<Article>>

}
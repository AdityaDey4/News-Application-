package com.example.news.database

import android.content.Context
import androidx.room.*
import com.example.news.api.Type_Converter
import com.example.news.data_class.Article

@TypeConverters(Type_Converter::class)
@Database(
    entities = [Article::class],
    version = 1
)
abstract class NewsDatabase: RoomDatabase() {
    abstract fun getArticlesDAO() : ArticlesDAO

    companion object{
        @Volatile // give updated instance to all the threads
        private var instance: NewsDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK){
            instance?:createDataBase(context).also {
                instance = it
            }
        }

        private fun createDataBase(context: Context) = Room.databaseBuilder(
            context.applicationContext, NewsDatabase::class.java, "news_database.db"
        ).build()
    }
}
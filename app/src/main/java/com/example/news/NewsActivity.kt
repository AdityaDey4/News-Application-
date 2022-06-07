package com.example.news

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.news.database.NewsDatabase
import com.example.news.viewmodel.NewsViewModel
import com.example.news.viewmodel.NewsViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView

class NewsActivity : AppCompatActivity() {

    lateinit var bottomNavigationView: BottomNavigationView
    lateinit var navHostFragment: Fragment
    lateinit var newsViewModel: NewsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_news)

        val newsRepository = NewsRepository(NewsDatabase(this))
        newsViewModel = ViewModelProvider(this, NewsViewModelFactory(application, newsRepository)).get(NewsViewModel::class.java)

        navHostFragment = supportFragmentManager.findFragmentById(R.id.navHostFragment) as NavHostFragment
        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        bottomNavigationView.setupWithNavController(navHostFragment.findNavController())

        // API -> https://newsapi.org/v2/everything?q=tesla&from=2022-04-16&sortBy=publishedAt&apiKey=0d22f62dbf2647ee87d8b501f97a9b15
    }
}
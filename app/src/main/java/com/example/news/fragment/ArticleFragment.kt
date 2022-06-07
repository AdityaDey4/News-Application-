package com.example.news.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ImageView
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import com.example.news.NewsActivity
import com.example.news.R
import com.example.news.data_class.Article
import com.example.news.viewmodel.NewsViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.snackbar.Snackbar
import java.lang.Exception

class ArticleFragment : Fragment() {
    lateinit var newsViewModel: NewsViewModel
    lateinit var webView: WebView
    lateinit var fab: FloatingActionButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_article, container, false)

        newsViewModel = (activity as NewsActivity).newsViewModel

        webView = view.findViewById(R.id.webView)
        fab = view.findViewById(R.id.fab)

        val article = ArticleFragmentArgs.fromBundle(arguments!!).article
        webView.apply {
            webViewClient = WebViewClient()
            loadUrl(article.url!!)
            Log.d("check", article.id.toString())
        }

        fab.setOnClickListener {
            newsViewModel.insertArticle(article)
            Snackbar.make(view, "Saved Successfully", Snackbar.LENGTH_SHORT).show()
        }

        return view
    }
}
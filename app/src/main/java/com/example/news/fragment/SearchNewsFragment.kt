package com.example.news.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.news.*
import com.example.news.viewmodel.NewsViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SearchNewsFragment : Fragment() {
    lateinit var newsViewModel: NewsViewModel
    lateinit var etSearch: EditText
    lateinit var rvSearchNews: RecyclerView
    lateinit var paginationProgressBar: ProgressBar
    lateinit var newsAdapter: NewsAdapter
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_search_news, container, false)

        newsViewModel = (activity as NewsActivity).newsViewModel
        etSearch = view.findViewById(R.id.etSearch)
        rvSearchNews = view.findViewById(R.id.rvSearchNews)
        paginationProgressBar = view.findViewById(R.id.paginationProgressBar)
        setUpRecyclerView()

        newsAdapter.setOnItemClickListener {

            val action = SearchNewsFragmentDirections.actionSearchNewsFragmentToArticleFragment(it)
            findNavController().navigate(action)
        }

        var job: Job? = null
        etSearch.addTextChangedListener{ editable ->
            job?.cancel()
            job = MainScope().launch {
                delay(2000)
                editable?.let {
                    if(it.toString().isNotEmpty()){
                        newsViewModel.searchNewsPage = 1
                        newsViewModel.searchNewsResponce = null
                        newsViewModel.getSearchNews(it.toString())
                    }
                }
            }
        }

        newsViewModel.searchNews.observe(viewLifecycleOwner, Observer { resource->
            when(resource){
                is Resource.Success -> {
                     hideProgressBar()
                    resource.data?.let { newsResponce ->
                        newsAdapter.differ.submitList(newsResponce.articles.toList())
                        Log.d("check", "API Called SuccessFully")
                        val totalPage = newsResponce.totalResults / Constance.LIST_SIZE +2
                        isLastPage = totalPage == newsViewModel.searchNewsPage
                        if(isLastPage){
                            rvSearchNews.setPadding(0, 0, 0, 0)
                        }
                    }
                }
                is Resource.Error -> {
                    hideProgressBar()
                    resource.msg.let {
                        Log.d("check", resource.msg.toString())
                        Toast.makeText(activity,resource.msg, Toast.LENGTH_SHORT).show()
                    }
                }
                is Resource.Loading -> {
                    showProgressBar()
                    Log.d("check", "Loading..........")
                }
            }
        })

        return view
    }

    fun hideProgressBar(){
        paginationProgressBar.visibility = View.INVISIBLE
        isLoading = false
    }
    fun showProgressBar(){
        paginationProgressBar.visibility = View.VISIBLE
        isLoading = true
    }
    fun setUpRecyclerView(){
        newsAdapter = NewsAdapter()
        rvSearchNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@SearchNewsFragment.onScrollListener)
        }
    }

    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    var onScrollListener = object : RecyclerView.OnScrollListener() {

        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItem = layoutManager.itemCount

            val isNotLoadingAndNotAtLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition+visibleItemCount >= totalItem
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItem >= Constance.LIST_SIZE

            val shouldPaginate = isNotLoadingAndNotAtLastPage && isAtLastItem && isNotAtBeginning
                    && isTotalMoreThanVisible && isScrolling

            if(shouldPaginate){
                newsViewModel.getSearchNews(etSearch.text.toString())
                isScrolling = false
            }
        }
        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
            super.onScrollStateChanged(recyclerView, newState)
            if(newState == AbsListView.OnScrollListener.SCROLL_STATE_TOUCH_SCROLL){
                isScrolling = true
            }
        }
    }
}
package com.example.news.fragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AbsListView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.navigation.NavDirections
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.news.*
import com.example.news.viewmodel.NewsViewModel
import com.google.android.material.snackbar.Snackbar

class BreakingNewsFragment : Fragment() {
    lateinit var newsViewModel: NewsViewModel
    lateinit var newsAdapter: NewsAdapter
    lateinit var rvBreakingNews: RecyclerView
    lateinit var paginationProgressBar: ProgressBar
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_breaking_news, container, false)

        paginationProgressBar = view?.findViewById(R.id.paginationProgressBarBN)!!
        newsViewModel = (activity as NewsActivity).newsViewModel
        rvBreakingNews = view.findViewById(R.id.rvBreakingNews)!!
        setupRecyclerView()

        newsAdapter.setOnItemClickListener {

            val action = BreakingNewsFragmentDirections.actionBreakingNewsFragmentToArticleFragment(it)
            findNavController().navigate(action)
        }

        newsViewModel.breakingNews.observe(viewLifecycleOwner, Observer { resource ->
            when(resource){
                is Resource.Success ->{
                    hideProgressBar()
                    resource.data?.let {  newsResponce ->
                        newsAdapter.differ.submitList(newsResponce.articles.toList())
                        Log.d("check", "API Called SuccessFully")
                        val totalPage = newsResponce.totalResults/Constance.LIST_SIZE+2
                        isLastPage = newsViewModel.breakingNewsPage == totalPage
                        if(isLastPage){
                            rvBreakingNews.setPadding(0, 0, 0, 0)
                        }
                    }
                }
                is Resource.Error ->{
                    hideProgressBar()
                    resource.msg.let {
                        Log.d("check", resource.msg.toString())
                        //Toast.makeText(activity,resource.msg, Toast.LENGTH_SHORT).show()
                        Snackbar.make(view, resource.msg.toString(), Snackbar.LENGTH_INDEFINITE)
                            .setAction("Refresh"){
                                newsViewModel.getBreakingNews("in")
                            }.show()
                    }
                }
                is Resource.Loading ->{
                    showProgressBar()
                    Log.d("check", "Loading..........")
                }
            }
        })

        return view
    }

    private fun hideProgressBar(){
        paginationProgressBar.visibility = View.INVISIBLE
        isLoading = false
    }
    private fun showProgressBar(){
        paginationProgressBar.visibility =View.VISIBLE
        isLoading = true
    }
    private fun setupRecyclerView() {
        newsAdapter = NewsAdapter()
        rvBreakingNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
            addOnScrollListener(this@BreakingNewsFragment.scrollListener)
        }
    }

    var isLoading = false
    var isLastPage = false
    var isScrolling = false

    val scrollListener = object : RecyclerView.OnScrollListener() {
        override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
            super.onScrolled(recyclerView, dx, dy)

            val layoutManager = recyclerView.layoutManager as LinearLayoutManager
            val firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition()
            val visibleItemCount = layoutManager.childCount
            val totalItemCount = layoutManager.itemCount

            val isNotLoadingAndNotLastPage = !isLoading && !isLastPage
            val isAtLastItem = firstVisibleItemPosition+visibleItemCount >= totalItemCount
            val isNotAtBeginning = firstVisibleItemPosition >= 0
            val isTotalMoreThanVisible = totalItemCount >= Constance.LIST_SIZE

            val shouldPaginate = isNotLoadingAndNotLastPage && isAtLastItem && isNotAtBeginning
                    && isTotalMoreThanVisible && isScrolling

            if(shouldPaginate){
                newsViewModel.getBreakingNews("in")
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
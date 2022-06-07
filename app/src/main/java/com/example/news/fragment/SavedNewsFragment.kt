package com.example.news.fragment

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.news.NewsActivity
import com.example.news.NewsAdapter
import com.example.news.R
import com.example.news.viewmodel.NewsViewModel
import com.google.android.material.snackbar.Snackbar

class SavedNewsFragment : Fragment() {
    lateinit var newsViewModel: NewsViewModel
    lateinit var rvSavedNews: RecyclerView
    lateinit var newsAdapter: NewsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_saved_news, container, false)

        newsViewModel = (activity as NewsActivity).newsViewModel
        rvSavedNews = view.findViewById(R.id.rvSavedNews)
        setUpRecyclerView()

        newsAdapter.setOnItemClickListener {

            val action = SavedNewsFragmentDirections.actionSavedNewsFragmentToArticleFragment(it)
            findNavController().navigate(action)
        }

        val itemTouchHelper = object : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or ItemTouchHelper.DOWN
        , ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return true
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val article = newsAdapter.differ.currentList[position]

                newsViewModel.deleteArticle(article)

                Snackbar.make(view, "Delete Successfully", Snackbar.LENGTH_LONG)
                    .setAction("UNDO"){
                        newsViewModel.insertArticle(article)
                    }
                    .show()
            }

        }

        ItemTouchHelper(itemTouchHelper).attachToRecyclerView(rvSavedNews)

        newsViewModel.getSavedNews().observe(viewLifecycleOwner, Observer { listOfArticles->
            newsAdapter.differ.submitList(listOfArticles)
        })
        return view
    }
    fun setUpRecyclerView(){
        newsAdapter = NewsAdapter()
        rvSavedNews.apply {
            adapter = newsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }
}
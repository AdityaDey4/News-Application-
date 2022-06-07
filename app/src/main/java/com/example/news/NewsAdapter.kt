package com.example.news

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.news.data_class.Article
import com.example.news.data_class.Source

class NewsAdapter: RecyclerView.Adapter<NewsAdapter.ViewHolder>() {

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val url: ImageView = itemView.findViewById(R.id.articleImage)
        val title: TextView = itemView.findViewById(R.id.articleTitle)
        val description: TextView = itemView.findViewById(R.id.articleDescription)
        val source: TextView = itemView.findViewById(R.id.articleSource)
        val publishedAt: TextView = itemView.findViewById(R.id.articlePublishAt)

    }

    private val differCallback = object : DiffUtil.ItemCallback<Article>() {
        override fun areItemsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem.url == newItem.url
        }

        override fun areContentsTheSame(oldItem: Article, newItem: Article): Boolean {
            return oldItem==newItem
        }
    }

    val differ = AsyncListDiffer(this, differCallback)
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.article_item, parent, false)
        )
    }
    var myListener:((Article)->Unit)? = null

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val article = differ.currentList[position]
        holder.apply {
            Glide.with(this.itemView).load(article.urlToImage).into(url)
            title.text = article.title
            description.text = article.description
            source.text = article.source?.name
            publishedAt.text = article.publishedAt
        }
        holder.itemView.setOnClickListener{
            myListener?.let {
                it(article)
            }
        }
    }

    fun setOnItemClickListener(listener:(Article)->Unit){
            myListener = listener
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }


}
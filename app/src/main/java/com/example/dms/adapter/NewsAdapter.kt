package com.example.dms.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dms.R
import com.example.dms.models.News
import com.example.dms.ui.NewsDetailActivity

class NewsAdapter(
    private val role: String?, // Добавляем роль пользователя
    private val onEditClick: (News) -> Unit,
    private val onDeleteClick: (News) -> Unit
) : RecyclerView.Adapter<NewsAdapter.NewsViewHolder>() {

    private val items = mutableListOf<News>()

    fun submitList(list: List<News>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    class NewsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.newsTitle)
        val description: TextView = itemView.findViewById(R.id.newsDescription)
        val date: TextView = itemView.findViewById(R.id.newsDate)
        val btnEdit: Button = itemView.findViewById(R.id.btnEditNews)
        val btnDelete: Button = itemView.findViewById(R.id.btnDeleteNews)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NewsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_news, parent, false)
        return NewsViewHolder(view)
    }

    override fun onBindViewHolder(holder: NewsViewHolder, position: Int) {
        val news = items[position]
        holder.title.text = news.title
        holder.description.text = news.description
        holder.date.text = news.date

        // Клик по карточке
        holder.itemView.setOnClickListener {
            val context = holder.itemView.context
            val intent = Intent(context, NewsDetailActivity::class.java)
            intent.putExtra("title", news.title)
            intent.putExtra("description", news.description)
            intent.putExtra("date", news.date)
            context.startActivity(intent)
        }

        val canModify = role?.equals("admin", true) == true || role?.equals("manager", true) == true
        if (canModify && news.id != null) {
            holder.btnEdit.visibility = View.VISIBLE
            holder.btnDelete.visibility = View.VISIBLE
            holder.btnEdit.setOnClickListener { onEditClick(news) }
            holder.btnDelete.setOnClickListener { onDeleteClick(news) }
        } else {
            holder.btnEdit.visibility = View.GONE
            holder.btnDelete.visibility = View.GONE
        }
    }

    override fun getItemCount() = items.size
}

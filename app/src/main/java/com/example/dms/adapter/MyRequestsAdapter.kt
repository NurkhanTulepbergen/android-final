package com.example.dms.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dms.R
import com.example.dms.models.MyRequest

class MyRequestsAdapter(
    private val list: List<MyRequest>
) : RecyclerView.Adapter<MyRequestsAdapter.RequestViewHolder>() {

    inner class RequestViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNumber: TextView = view.findViewById(R.id.tvNumber)
        val tvTitle: TextView = view.findViewById(R.id.tvTitle)
        val tvStatus: TextView = view.findViewById(R.id.tvStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RequestViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_document, parent, false)
        return RequestViewHolder(view)
    }

    override fun getItemCount(): Int = list.size

    override fun onBindViewHolder(holder: RequestViewHolder, position: Int) {
        val item = list[position]

        holder.tvNumber.text = (position + 1).toString()
        holder.tvTitle.text = item.title
        holder.tvStatus.text = getStatusText(item.status)

        when (item.status.lowercase()) {
            "accepted", "принят" -> holder.tvStatus.setTextColor(Color.parseColor("#3E9E00"))
            "rejected", "отклонен" -> holder.tvStatus.setTextColor(Color.parseColor("#E50000"))
            "pending", "на рассмотрении" -> holder.tvStatus.setTextColor(Color.parseColor("#E57C04"))
            else -> holder.tvStatus.setTextColor(Color.BLACK)
        }
    }

    private fun getStatusText(status: String): String {
        return when (status.lowercase()) {
            "accepted" -> "Принят"
            "rejected" -> "Отклонен"
            "pending" -> "На рассмотрении"
            else -> status
        }
    }
}


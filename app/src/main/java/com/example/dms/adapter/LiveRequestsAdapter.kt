package com.example.dms.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dms.R
import com.example.dms.models.LocalLiveRequest
import com.google.android.material.button.MaterialButton

class LiveRequestsAdapter(
    private val onAccept: (LocalLiveRequest) -> Unit,
    private val onReject: (LocalLiveRequest) -> Unit
) : RecyclerView.Adapter<LiveRequestsAdapter.LiveRequestViewHolder>() {

    private val items = mutableListOf<LocalLiveRequest>()

    fun submitList(list: List<LocalLiveRequest>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    inner class LiveRequestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tvRequestTitle)
        val floor: TextView = itemView.findViewById(R.id.tvRequestFloor)
        val status: TextView = itemView.findViewById(R.id.tvRequestStatus)
        val accept: MaterialButton = itemView.findViewById(R.id.btnAcceptRequest)
        val reject: MaterialButton = itemView.findViewById(R.id.btnRejectRequest)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LiveRequestViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_live_request, parent, false)
        return LiveRequestViewHolder(view)
    }

    override fun onBindViewHolder(holder: LiveRequestViewHolder, position: Int) {
        val request = items[position]
        holder.title.text = "Комната ${request.room}"
        holder.floor.text = "Этаж: ${request.floor}"
        holder.status.text = "Статус: ${formatStatus(request.status)}"

        setButtonStates(holder, request)

        holder.accept.setOnClickListener { onAccept(request) }
        holder.reject.setOnClickListener { onReject(request) }
    }

    override fun getItemCount(): Int = items.size

    private fun formatStatus(status: String): String {
        return when (status.lowercase()) {
            "accepted", "принят" -> "Принят"
            "rejected", "отклонен" -> "Отклонён"
            "pending", "на рассмотрении" -> "На рассмотрении"
            else -> status
        }
    }

    private fun setButtonStates(holder: LiveRequestViewHolder, request: LocalLiveRequest) {
        when (request.status.lowercase()) {
            "accepted", "принят" -> {
                holder.accept.isEnabled = false
                holder.reject.isEnabled = true
            }
            "rejected", "отклонен" -> {
                holder.accept.isEnabled = true
                holder.reject.isEnabled = false
            }
            else -> {
                holder.accept.isEnabled = true
                holder.reject.isEnabled = true
            }
        }
    }
}

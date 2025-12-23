package com.example.dms.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dms.R
import com.example.dms.models.LocalRepairRequest

class RepairRequestsAdapter : RecyclerView.Adapter<RepairRequestsAdapter.RepairViewHolder>() {

    private val items = mutableListOf<LocalRepairRequest>()

    fun submitList(list: List<LocalRepairRequest>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    inner class RepairViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val location: TextView = itemView.findViewById(R.id.tvRepairRoom)
        val detail: TextView = itemView.findViewById(R.id.tvRepairDetail)
        val status: TextView = itemView.findViewById(R.id.tvRepairStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RepairViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_repair_request, parent, false)
        return RepairViewHolder(view)
    }

    override fun onBindViewHolder(holder: RepairViewHolder, position: Int) {
        val request = items[position]
        holder.location.text = request.location
        holder.detail.text = request.issue
        holder.status.text = "Статус: ${request.status}"
    }

    override fun getItemCount(): Int = items.size
}

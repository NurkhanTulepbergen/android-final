package com.example.dms.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dms.R
import com.example.dms.models.FinanceEntry
import com.google.android.material.button.MaterialButton

class FinanceAdapter(
    private val onMarkPaid: (FinanceEntry) -> Unit
) : RecyclerView.Adapter<FinanceAdapter.FinanceViewHolder>() {

    private val items = mutableListOf<FinanceEntry>()

    fun submitList(list: List<FinanceEntry>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    inner class FinanceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tvFinanceTitle)
        val date: TextView = itemView.findViewById(R.id.tvFinanceDate)
        val amount: TextView = itemView.findViewById(R.id.tvFinanceAmount)
        val status: TextView = itemView.findViewById(R.id.tvFinanceStatus)
        val markPaid: MaterialButton = itemView.findViewById(R.id.btnMarkPaid)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FinanceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_finance_entry, parent, false)
        return FinanceViewHolder(view)
    }

    override fun onBindViewHolder(holder: FinanceViewHolder, position: Int) {
        val entry = items[position]
        holder.title.text = entry.title
        holder.date.text = "До ${entry.dueDate}"
        holder.amount.text = "Сумма: ${entry.amount}"
        holder.status.text = "Статус: ${entry.status}"

        val isPending = entry.status.lowercase().contains("pending") ||
                entry.status.lowercase().contains("на рассмотрении")

        holder.markPaid.visibility = if (isPending) View.VISIBLE else View.GONE
        holder.markPaid.setOnClickListener { onMarkPaid(entry) }
    }

    override fun getItemCount(): Int = items.size
}

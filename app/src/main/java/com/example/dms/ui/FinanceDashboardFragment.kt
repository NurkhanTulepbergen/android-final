package com.example.dms.ui

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dms.R
import com.example.dms.adapter.FinanceAdapter
import com.example.dms.models.FinanceEntry

class FinanceDashboardFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var totalDueView: TextView
    private lateinit var dueDateSummary: TextView
    private val adapter = FinanceAdapter(::markPaid)
    private val baseEntries = listOf(
        FinanceEntry("1", "Коммунальные платежи", "25 октября", "₽4 800", "pending"),
        FinanceEntry("2", "Проживание в общежитии", "1 ноября", "₽6 200", "pending"),
        FinanceEntry("3", "Штраф за пропуск тренировки", "15 октября", "₽450", "paid")
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_finance_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.rvFinanceEntries)
        totalDueView = view.findViewById(R.id.tvTotalDue)
        dueDateSummary = view.findViewById(R.id.tvDueDateSummary)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        loadEntries()
    }

    private fun markPaid(entry: FinanceEntry) {
        val prefs = requireContext().getSharedPreferences("finance_prefs", Context.MODE_PRIVATE)
        prefs.edit().putString(entry.id, "paid").apply()
        Toast.makeText(requireContext(), "Счет отмечен как оплаченный", Toast.LENGTH_SHORT).show()
        loadEntries()
    }

    private fun loadEntries() {
        val prefs = requireContext().getSharedPreferences("finance_prefs", Context.MODE_PRIVATE)
        val updated = baseEntries.map { entry ->
            val status = prefs.getString(entry.id, entry.status) ?: entry.status
            entry.copy(status = status)
        }

        adapter.submitList(updated)
        val dueEntries = updated.filter { it.status.lowercase() != "paid" }
        val totalDue = dueEntries.joinToString(" + ") { it.amount.removePrefix("₽") }
            .let { parseAmounts(it) }
        totalDueView.text = "Сумма к оплате: ₽${totalDue}"
        val next = dueEntries.minByOrNull { it.dueDate }?.dueDate ?: "—"
        dueDateSummary.text = "Ближайший счет: $next"
    }

    private fun parseAmounts(raw: String): String {
        val total = raw.split("+").mapNotNull {
            it.trim().replace(" ", "").toIntOrNull()
        }.sum()
        return total.toString()
    }
}

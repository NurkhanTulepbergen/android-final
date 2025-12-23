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
import com.example.dms.adapter.RepairRequestsAdapter
import com.example.dms.models.LocalRepairRequest
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class RepairRequestFragment : Fragment() {

    private lateinit var locationInput: TextInputEditText
    private lateinit var descriptionInput: TextInputEditText
    private lateinit var sendButton: MaterialButton
    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyState: TextView
    private val adapter = RepairRequestsAdapter()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_repair_request, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        locationInput = view.findViewById(R.id.etRepairLocation)
        descriptionInput = view.findViewById(R.id.etRepairDescription)
        sendButton = view.findViewById(R.id.btnSendRepair)
        recyclerView = view.findViewById(R.id.rvRepairHistory)
        emptyState = view.findViewById(R.id.tvRepairEmpty)

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter

        sendButton.setOnClickListener { submitRequest() }

        loadRequests()
    }

    private fun submitRequest() {
        val location = locationInput.text.toString().trim()
        val description = descriptionInput.text.toString().trim()
        if (location.isEmpty() || description.isEmpty()) {
            Toast.makeText(requireContext(), "Заполните все поля", Toast.LENGTH_SHORT).show()
            return
        }

        val id = System.currentTimeMillis().toString()
        val prefs = requireContext().getSharedPreferences("repair_requests", Context.MODE_PRIVATE)
        prefs.edit().putString(id, "$location|$description|pending").apply()

        Toast.makeText(requireContext(), "Запрос отправлен", Toast.LENGTH_SHORT).show()
        locationInput.text?.clear()
        descriptionInput.text?.clear()
        loadRequests()
    }

    private fun loadRequests() {
        val prefs = requireContext().getSharedPreferences("repair_requests", Context.MODE_PRIVATE)
        val list = prefs.all.mapNotNull { entry ->
            val parts = entry.value.toString().split("|")
            if (parts.size >= 3) {
                LocalRepairRequest(entry.key, parts[0], parts[1], parts[2])
            } else {
                null
            }
        }.sortedByDescending { it.id }

        adapter.submitList(list)
        emptyState.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
    }
}

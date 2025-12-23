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
import com.example.dms.adapter.LiveRequestsAdapter
import com.example.dms.models.LocalLiveRequest

class LiveRequestsApprovalFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyState: TextView
    private lateinit var adapter: LiveRequestsAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_live_requests_approval, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.rvLiveRequests)
        emptyState = view.findViewById(R.id.tvEmptyState)
        adapter = LiveRequestsAdapter(::acceptRequest, ::rejectRequest)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        recyclerView.adapter = adapter
        loadRequests()
    }

    private fun acceptRequest(request: LocalLiveRequest) {
        updateRequestStatus(request, "accepted")
        Toast.makeText(requireContext(), "Запрос принят", Toast.LENGTH_SHORT).show()
    }

    private fun rejectRequest(request: LocalLiveRequest) {
        updateRequestStatus(request, "rejected")
        Toast.makeText(requireContext(), "Запрос отклонён", Toast.LENGTH_SHORT).show()
    }

    private fun updateRequestStatus(request: LocalLiveRequest, status: String) {
        val prefs = requireContext().getSharedPreferences("live_requests", Context.MODE_PRIVATE)
        prefs.edit().putString(request.id, "${request.room}|${request.floor}|$status").apply()
        loadRequests()
    }

    private fun loadRequests() {
        val prefs = requireContext().getSharedPreferences("live_requests", Context.MODE_PRIVATE)
        val list = prefs.all.mapNotNull { entry ->
            val parts = entry.value.toString().split("|")
            if (parts.size >= 3) {
                LocalLiveRequest(entry.key, parts[0], parts[1], parts[2])
            } else {
                null
            }
        }
        adapter.submitList(list)
        emptyState.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
    }
}

package com.example.dms.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.widget.Toast
import com.example.dms.R
import com.example.dms.adapter.MyRequestsAdapter
import com.example.dms.models.*
import com.example.dms.utils.SessionManager

class MyRequestsFragment : Fragment() {

    private lateinit var rvRequests: RecyclerView
    private val requests = mutableListOf<MyRequest>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_my_requests, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rvRequests = view.findViewById(R.id.rvRequests)
        rvRequests.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onResume() {
        super.onResume()
        loadLocalRequests()
    }

    // 🔹 Загружаем все локальные записи: спорт + проживание
    private fun loadLocalRequests() {
        requests.clear()

        // 1️⃣ Локальные спортивные записи
        val sportsPref = requireContext().getSharedPreferences("sports_requests", 0)
        sportsPref.all.forEach { entry ->
            val parts = entry.value.toString().split("|")
            if (parts.size >= 4) {
                val sport = parts[0]
                val teacher = parts[1]
                val time = parts[2]
                val status = parts[3]

                requests.add(
                    MyRequest(
                        id = entry.key.toIntOrNull() ?: 0,
                        type = RequestType.SPORTS,
                        title = "Запись на физкультуру",
                        description = "$sport, $teacher, $time",
                        status = status,
                        createdAt = null
                    )
                )
            }
        }

        // 2️⃣ Локальные записи на проживание
        val livePref = requireContext().getSharedPreferences("live_requests", 0)
        livePref.all.forEach { entry ->
            val parts = entry.value.toString().split("|")
            if (parts.size >= 3) {
                val room = parts[0]
                val floor = parts[1]
                val status = parts[2]

                requests.add(
                    MyRequest(
                        id = entry.key.toIntOrNull() ?: 0,
                        type = RequestType.LIVE,
                        title = "Запись на проживание",
                        description = "$floor, комната $room",
                        status = status,
                        createdAt = null
                    )
                )
            }
        }

        updateRecyclerView()
    }

    private fun updateRecyclerView() {
        // Можно сортировать по дате, если нужно
        val sortedRequests = requests.sortedByDescending { it.createdAt ?: "" }
        rvRequests.adapter = MyRequestsAdapter(sortedRequests)
    }
}

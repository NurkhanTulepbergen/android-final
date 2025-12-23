package com.example.dms.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.dms.R
import com.example.dms.adapter.NewsAdapter
import com.example.dms.models.News
import com.example.dms.utils.SessionManager

class NewsFragment : Fragment() {

    private lateinit var sessionManager: SessionManager
    private lateinit var recyclerView: RecyclerView
    private lateinit var addButton: Button
    private lateinit var adapter: NewsAdapter
    private val viewModel: NewsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_news, container, false)
        sessionManager = SessionManager(requireContext())

        recyclerView = view.findViewById(R.id.newsRecycler)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        addButton = view.findViewById(R.id.btnAddNews)

        updateAddButtonVisibility() // показываем/скрываем кнопку по роли

        // Передаем роль пользователя в адаптер
        adapter = NewsAdapter(sessionManager.getUserRole(), ::onEditClick, ::onDeleteClick)
        recyclerView.adapter = adapter

        viewModel.news.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
        }

        viewModel.message.observe(viewLifecycleOwner) { message ->
            message?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
                viewModel.consumeMessage()
            }
        }

        viewModel.loadNews()
        return view
    }

    override fun onResume() {
        super.onResume()
        updateAddButtonVisibility() // обновляем видимость кнопки при возврате на экран
        if (sessionManager.getToken() != null) viewModel.loadNews()
    }

    private fun updateAddButtonVisibility() {
        val role = sessionManager.getUserRole()
        Log.d("NewsFragment", "Role from SessionManager: '$role'")

        if (role.equals("admin", true) || role.equals("manager", true)) {
            addButton.visibility = View.VISIBLE
            addButton.setOnClickListener { openAddNews() }
        } else {
            addButton.visibility = View.GONE
        }
    }

    private fun onEditClick(news: News) {
        if (news.id == null) return
        val intent = Intent(requireContext(), AddEditNewsActivity::class.java)
        intent.putExtra("news_id", news.id)
        intent.putExtra("news_title", news.title)
        intent.putExtra("news_description", news.description)
        startActivity(intent)
    }

    private fun onDeleteClick(news: News) {
        news.id?.let { viewModel.deleteNews(it) }
    }

    private fun openAddNews() {
        val intent = Intent(requireContext(), AddEditNewsActivity::class.java)
        startActivity(intent)
    }
}

package com.example.dms.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.dms.data.local.AppDatabase
import com.example.dms.network.RetrofitClient
import com.example.dms.repository.NewsRepository
import com.example.dms.utils.SessionManager
import kotlinx.coroutines.launch
import com.example.dms.models.News

class NewsViewModel(application: Application) : AndroidViewModel(application) {

    private val sessionManager = SessionManager(application)
    private val repository = NewsRepository(
        RetrofitClient.getInstance(sessionManager.getToken()),
        AppDatabase.getInstance(application).newsDao()
    )

    private val _news = MutableLiveData<List<News>>(emptyList())
    val news: LiveData<List<News>> = _news

    private val _message = MutableLiveData<String?>()
    val message: LiveData<String?> = _message

    private val _isLoading = MutableLiveData(false)
    val isLoading: LiveData<Boolean> = _isLoading

    init {
        loadNews()
    }

    fun loadNews() {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.fetchNews()
            if (result.isSuccess) {
                _news.value = result.getOrNull()
            } else {
                val cached = repository.getCachedNews()
                if (cached.isNotEmpty()) {
                    _news.value = cached
                    _message.value = "Работа в офлайн-режиме, показываются локальные данные"
                } else {
                    _message.value = result.exceptionOrNull()?.localizedMessage ?: "Не удалось загрузить новости"
                }
            }
            _isLoading.value = false
        }
    }

    fun deleteNews(newsId: Int) {
        viewModelScope.launch {
            _isLoading.value = true
            val result = repository.deleteNews(newsId)
            if (result.isSuccess) {
                _message.value = "Новость удалена"
                loadNews()
            } else {
                _message.value = result.exceptionOrNull()?.localizedMessage ?: "Не удалось удалить новость"
                _isLoading.value = false
            }
        }
    }

    fun consumeMessage() {
        _message.value = null
    }
}

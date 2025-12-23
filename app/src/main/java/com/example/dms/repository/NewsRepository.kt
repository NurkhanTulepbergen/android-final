package com.example.dms.repository

import com.example.dms.data.local.NewsDao
import com.example.dms.data.local.toNews
import com.example.dms.data.local.toEntity
import com.example.dms.models.News
import com.example.dms.network.ApiService

class NewsRepository(
    private val api: ApiService,
    private val newsDao: NewsDao
) {

    suspend fun fetchNews(): Result<List<News>> {
        return try {
            val response = api.getAllNews()
            if (response.isSuccessful) {
                val body = response.body() ?: emptyList()
                val entities = body.mapNotNull { it.toEntity() }
                newsDao.clearAll()
                if (entities.isNotEmpty()) {
                    newsDao.insertAll(entities)
                }
                Result.success(body)
            } else {
                Result.failure(Exception("Сервер ответил: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCachedNews(): List<News> {
        return newsDao.getAll().map { it.toNews() }
    }

    suspend fun deleteNews(newsId: Int): Result<Unit> {
        return try {
            val response = api.deleteNews(newsId)
            if (response.isSuccessful) {
                newsDao.deleteById(newsId)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Ошибка удаления: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

package com.example.dms.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.dms.models.News

@Entity(tableName = "news")
data class NewsEntity(
    @PrimaryKey val id: Int,
    val title: String,
    val description: String,
    val date: String? = null
)

fun NewsEntity.toNews(): News = News(id = id, title = title, description = description, date = date)

fun News.toEntity(): NewsEntity? {
    val newsId = id ?: return null
    return NewsEntity(id = newsId, title = title, description = description, date = date)
}

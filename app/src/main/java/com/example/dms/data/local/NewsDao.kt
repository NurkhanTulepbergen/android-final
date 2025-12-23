package com.example.dms.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface NewsDao {

    @Query("SELECT * FROM news ORDER BY id DESC")
    suspend fun getAll(): List<NewsEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(news: List<NewsEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(news: NewsEntity)

    @Query("DELETE FROM news WHERE id = :newsId")
    suspend fun deleteById(newsId: Int)

    @Query("DELETE FROM news")
    suspend fun clearAll()
}

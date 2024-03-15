package com.example.cineflix.Model.local.searchHistory

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface SearchHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(searchHistory: SearchHistory)

    @Delete
    suspend fun delete(searchHistory: SearchHistory)

    @Query("DELETE FROM search_history WHERE `query`=:query")
    fun deleteRecord(query: String)

    @Query("SELECT COUNT(*) FROM search_history")
    fun getLastID() : Int

    @Query("SELECT * FROM search_history ORDER BY id DESC")
    fun getSearchHistory(): List<SearchHistory>

    @Query("DELETE FROM search_history")
    fun deleteAll()
}
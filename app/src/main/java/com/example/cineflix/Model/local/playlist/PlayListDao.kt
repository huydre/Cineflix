package com.example.cineflix.Model.local.playlist

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query


@Dao
interface PlayListDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(playList: PlayList)

    @Delete
    suspend fun delete(playList: PlayList)

    @Query("DELETE FROM playlist WHERE `tmdbID`=:id")
    fun deleteRecord(id: Int)

    @Query("SELECT * FROM playlist ORDER BY timeAdd DESC")
    fun getPlayListAll(): LiveData<List<PlayList>>

    @Query("DELETE FROM playlist")
    fun deleteAll()

    @Query("SELECT * FROM playlist WHERE `tmdbID`=:id")
    fun getPlayList(id:Int) : LiveData<PlayList>

}
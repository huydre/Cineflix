package com.example.cineflix.Model.local.playlist

import androidx.lifecycle.LiveData

class PlayListRepository(private val playListDao: PlayListDao) {

    suspend fun insert(playList: PlayList) {
        playListDao.insert(playList)
    }

    suspend fun delete(playList: PlayList) {
        playListDao.delete(playList)
    }

    fun deleteRecord(id: Int) {
        playListDao.deleteRecord(id)
    }

    fun getPlayListAll(): LiveData<List<PlayList>> {
        return playListDao.getPlayListAll()
    }

    fun deleteAll() {
        playListDao.deleteAll()
    }

    fun getPlayList(id:Int) : LiveData<PlayList> {
        return playListDao.getPlayList(id)
    }

}
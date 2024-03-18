package com.example.cineflix.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.cineflix.Model.local.playlist.PlayList
import com.example.cineflix.Model.local.playlist.PlayListDao
import com.example.cineflix.Model.local.playlist.PlayListRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class PlaylistViewModelFactory(private val playListRepository: PlayListRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(PlayListViewModel::class.java)){
            return PlayListViewModel(playListRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }

}
class PlayListViewModel(private val playListRepository: PlayListRepository) : ViewModel() {
    suspend fun insert(playList: PlayList) {
        viewModelScope.launch(Dispatchers.IO) {
            playListRepository.insert(playList)
        }

    }

    suspend fun delete(playList: PlayList) {
        viewModelScope.launch(Dispatchers.IO) {
            playListRepository.delete(playList)
        }
    }
    fun deleteRecord(id: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            playListRepository.deleteRecord(id)
        }
    }
    fun getPlayListAll(): LiveData<List<PlayList>> {
        return playListRepository.getPlayListAll()
    }

    fun deleteAll() {
        viewModelScope.launch(Dispatchers.IO) {
            playListRepository.deleteAll()
        }
    }

    fun getPlayList(id:Int) : LiveData<PlayList> {
        return playListRepository.getPlayList(id)
    }

}
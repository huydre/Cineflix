package com.example.cineflix.ViewModel

import android.content.ContentValues
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.cineflix.Model.MovieDetails
import com.example.cineflix.Model.local.watching.ContinueWatching
import com.example.cineflix.Model.local.watching.ContinueWatchingDAO
import com.example.cineflix.Model.local.watching.ContinueWatchingRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class ContinueWatchingViewModelFactory(private val watchingDao: ContinueWatchingDAO) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ContinueWatchingViewModel::class.java)){
            return ContinueWatchingViewModel(watchingDao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}

class ContinueWatchingViewModel(private val watchingDao: ContinueWatchingDAO) : ViewModel(){

    private val _progress = MutableLiveData<ContinueWatching>()
    val progress: LiveData<ContinueWatching> get() = _progress

    val allWatchHistory : LiveData<List<ContinueWatching>> = watchingDao.getContinueWatchingTest()

    val searchOpen = MutableLiveData(true)

}

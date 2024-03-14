package com.example.cineflix.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
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

class ContinueWatchingViewModel(watchingDao: ContinueWatchingDAO) : ViewModel(){
    val allWatchHistory : LiveData<List<ContinueWatching>> = watchingDao.getContinueWatchingTest()

//    val currentFragment = MutableLiveData(R.id.moviesFragment)
    val searchOpen = MutableLiveData(true)

}

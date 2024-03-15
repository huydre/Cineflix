package com.example.cineflix.ViewModel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.cineflix.Model.local.searchHistory.QueryRepository
import com.example.cineflix.Model.local.searchHistory.SearchHistory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SearchVMFactory(private val queryRepository: QueryRepository) : ViewModelProvider.Factory{
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SearchHistoryViewModel::class.java)){
            return SearchHistoryViewModel(queryRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel Class")
    }
}

class SearchHistoryViewModel(private val queryRepository: QueryRepository): ViewModel(){
    val queries = MutableLiveData<List<SearchHistory>>()
    val noMatches = MutableLiveData(false)
    val choice = MutableLiveData(1)
    val queryText = MutableLiveData("")
    init {
        viewModelScope.launch(Dispatchers.IO) {
            queries.postValue(queryRepository.loadData())
        }
    }

    fun addToHistory(item: SearchHistory){
        viewModelScope.launch (Dispatchers.IO){
            queryRepository.insert(item)
            queries.postValue(queryRepository.loadData())
        }
    }

    fun deleteRecord(item:SearchHistory){
        viewModelScope.launch (Dispatchers.IO){
            queryRepository.deleteRecord(item)
            queries.postValue(queryRepository.loadData())
        }
    }

    fun deleteAll(){
        viewModelScope.launch(Dispatchers.IO) {
            queryRepository.deleteAll()
            queries.postValue(queryRepository.loadData())
        }
    }


    val searchClicked = MutableLiveData(false)
}
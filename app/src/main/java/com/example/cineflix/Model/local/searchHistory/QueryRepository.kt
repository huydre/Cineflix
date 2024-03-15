package com.example.cineflix.Model.local.searchHistory

import androidx.annotation.WorkerThread
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData


class QueryRepository(private val queryDao: SearchHistoryDao) {

    private val _allQueries = MutableLiveData<List<SearchHistory>>()
    val allQueries : LiveData<List<SearchHistory>> = _allQueries

    fun loadData(): List<SearchHistory>{
        return queryDao.getSearchHistory()
    }

    @WorkerThread
    fun deleteRecord(query: SearchHistory){
        queryDao.deleteRecord(query.query)
    }

    @WorkerThread
    suspend fun insert(query: SearchHistory) {
        queryDao.insert(query)
    }

    @WorkerThread
    suspend fun delete(query: SearchHistory) {
        queryDao.delete(query)
    }

    @WorkerThread
    fun deleteAll(){
        queryDao.deleteAll()
    }
}
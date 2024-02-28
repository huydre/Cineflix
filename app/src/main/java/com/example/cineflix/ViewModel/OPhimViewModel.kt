package com.example.cineflix.ViewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.cineflix.Model.OPhimRespone
import com.example.cineflix.OPhimRepository
import kotlinx.coroutines.launch

class OPhimViewModelFactory(private val repository: OPhimRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(OPhimViewModel::class.java)) {
            return OPhimViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class OPhimViewModel(private val repository: OPhimRepository): ViewModel() {
    private val _oPhimDetails = MutableLiveData<List<OPhimRespone>>()
    val oPhimDetails : LiveData<List<OPhimRespone>> get() = _oPhimDetails

    fun getOPhimDetails(slug: String) {
        viewModelScope.launch {
            try {
                val respone = repository.getOPhimDetails(slug)
                val oPhimRespone = respone.body()
                oPhimRespone?.let {
                    _oPhimDetails.value = listOf(it)
                }
            } catch (e: Exception) {

            }
        }
    }
}
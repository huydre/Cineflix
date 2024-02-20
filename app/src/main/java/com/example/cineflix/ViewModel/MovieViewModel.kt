package com.example.cineflix.ViewModel


import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cineflix.Model.Movie
import com.example.cineflix.MovieRepository
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.cineflix.MyApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject


class MovieViewModelFactory(private val repository: MovieRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MovieViewModel::class.java)) {
            return MovieViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}

class MovieViewModel(private val repository: MovieRepository): ViewModel() {
    private val _popularMovies = MutableLiveData<List<Movie>>()
    val popularMovies: LiveData<List<Movie>> get() = _popularMovies

    private val _topratedMovies = MutableLiveData<List<Movie>>()
    val topRatedMovies: LiveData<List<Movie>> get() = _topratedMovies
    

    fun getPopularMovies(page: Int) {
        viewModelScope.launch {
            try {
                val response = repository.getPopularMovies(page)
                val movieResponse = response.body()
                movieResponse?.let {
                    _popularMovies.value = it.results
                }
            } catch (e: Exception) {
                Log.d(TAG, "getPopularMovies Error: " + e.message)
            }

        }
    }

    fun getTopRatedMovies(page: Int) {
        viewModelScope.launch {
            try {
                val response = repository.getTopRatedMovies(page)
                val movieResponse = response.body()
                movieResponse?.let {
                    _topratedMovies.value = it.results
                }
            } catch (e: Exception) {
                Log.d(TAG, "Error: " + e.message)
            }

        }
    }

}

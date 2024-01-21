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
    

    fun getPopularMovies(apiKey: String, page: Int) {
        viewModelScope.launch {
            try {
                val response = repository.getPopularMovies(apiKey, page)
                val movieResponse = response.body()
                Log.d(TAG, "getPopularMovies: " + response.body())
                movieResponse?.let {
                    _popularMovies.value = it.results
                }
            } catch (e: Exception) {
                Log.d(TAG, "getPopularMovies Error: " + e.message)
            }

        }
    }

}

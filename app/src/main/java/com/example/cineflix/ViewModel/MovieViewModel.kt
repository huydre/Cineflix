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
import com.example.cineflix.Model.Credit
import com.example.cineflix.Model.SearchMulti
import com.example.cineflix.Model.Video
import com.example.cineflix.MyApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import retrofit2.http.Query
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

    private val _nowplayingMovies = MutableLiveData<List<Movie>>()
    val nowPlayingMovies: LiveData<List<Movie>> get() = _nowplayingMovies

    private val _movieVideos = MutableLiveData<Video>()
    val movieVideos: LiveData<Video> get() = _movieVideos

    private val _movieCredits = MutableLiveData<Credit>()
    val movieCredits: LiveData<Credit> get() = _movieCredits

    private val _movieSimilar = MutableLiveData<List<Movie>>()
    val movieSimilar: LiveData<List<Movie>> get() = _movieSimilar

    private val _searchMulti = MutableLiveData<List<SearchMulti>>()
    val searchMulti: LiveData<List<SearchMulti>> get() = _searchMulti
    

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

    fun getNowPlayingMovies(page: Int) {
        viewModelScope.launch {
            try {
                val response = repository.getNowPlayingMovies(page)
                val movieResponse = response.body()
                movieResponse?.let {
                    _nowplayingMovies.value  = it.results
                }
            } catch (e: Exception) {
                Log.d(TAG, "Error: " + e.message)
            }
        }
    }

    fun getMovieVideos(movieId: String) {
        viewModelScope.launch {
            try {
                val response = repository.getMovieVideos(movieId)
                val videoRespone = response.body()
                videoRespone?.let {
                    _movieVideos.value = it
                }

            } catch (e: Exception) {
                Log.d(TAG, "Error: " + e.message)
            }
        }
    }

    fun getMovieCredits(movieId: String) {
        viewModelScope.launch {
            try {
                val response = repository.getMovieCredits(movieId)
                val videoRespone = response.body()
                videoRespone?.let {
                    _movieCredits.value = it
                }

            } catch (e: Exception) {
                Log.d(TAG, "Error: " + e.message)
            }
        }
    }

    fun getMovieSimilar(movieId: String) {
        viewModelScope.launch {
            try {
                val response = repository.getMovieSimilar(movieId)
                val movieResponse = response.body()
                movieResponse?.let {
                    _movieSimilar.value  = it.results
                }
            } catch (e: Exception) {
                Log.d(TAG, "Error: " + e.message)
            }
        }
    }

    fun getSearchMulti(query: String) {
        viewModelScope.launch {
            try {
                val response = repository.getSearchMulti(query)
                val searchResponse = response.body()
                searchResponse?.let {
                    _searchMulti.value = it.results
                }
            } catch (e: Exception) {
                Log.d(TAG, "Error: " + e.message)
            }
        }
    }
}

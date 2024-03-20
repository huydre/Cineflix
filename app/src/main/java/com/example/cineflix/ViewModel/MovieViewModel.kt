package com.example.cineflix.ViewModel


import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.cineflix.Model.network.Movie
import com.example.cineflix.MovieRepository
import androidx.lifecycle.viewModelScope
import com.example.cineflix.Model.network.Credit
import com.example.cineflix.Model.network.Ids
import com.example.cineflix.Model.network.MovieDetails
import com.example.cineflix.Model.network.SearchMulti
import com.example.cineflix.Model.network.SeasonDetails
import com.example.cineflix.Model.network.TV
import com.example.cineflix.Model.network.TvDetails
import com.example.cineflix.Model.network.Video
import kotlinx.coroutines.launch


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

    private val _movieDetails = MutableLiveData<MovieDetails>()
    val movieDetails: LiveData<MovieDetails> get() = _movieDetails

    private val _movieVideos = MutableLiveData<Video>()
    val movieVideos: LiveData<Video> get() = _movieVideos

    private val _movieCredits = MutableLiveData<Credit>()
    val movieCredits: LiveData<Credit> get() = _movieCredits

    private val _movieSimilar = MutableLiveData<List<Movie>>()
    val movieSimilar: LiveData<List<Movie>> get() = _movieSimilar

    private val _searchMulti = MutableLiveData<List<SearchMulti>>()
    val searchMulti: LiveData<List<SearchMulti>> get() = _searchMulti

    private val _tvPopular = MutableLiveData<List<TV>>()
    val tvPopular: LiveData<List<TV>> get() = _tvPopular

    private val _tvTopRated = MutableLiveData<List<TV>>()
    val tvTopRated: LiveData<List<TV>> get() = _tvTopRated

    private val _tvAiringToday = MutableLiveData<List<TV>>()
    val tvAiringToday: LiveData<List<TV>> get() = _tvAiringToday

    private val _tvVideos = MutableLiveData<Video>()
    val tvVideos: LiveData<Video> get() = _tvVideos

    private val _tvCredits = MutableLiveData<Credit>()
    val tvCredit: LiveData<Credit> get() = _tvCredits

    private val _tvSimilar = MutableLiveData<List<TV>>()
    val tvSimilar : LiveData<List<TV>> get() = _tvSimilar

    private val _tvDetails = MutableLiveData<TvDetails>()
    val tvDetails : LiveData<TvDetails> get() = _tvDetails

    private val _tvSeasonDetails = MutableLiveData<SeasonDetails>()
    val tvSeasonDetails : LiveData<SeasonDetails> get() = _tvSeasonDetails

    private val _imdbID = MutableLiveData<Ids>()
    val imdbID : LiveData<Ids> get() = _imdbID

    private val _networkTV = MutableLiveData<List<TV>>()
    val networkTV : LiveData<List<TV>> get() = _networkTV

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

    fun getMovieDetails(movieId: String) {
        viewModelScope.launch {
            try {
                val response = repository.getMovieDetails(movieId)
                val movieResponse = response.body()
                movieResponse?.let {
                    _movieDetails.value = it
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

    fun getTVPopular(page: Int) {
        viewModelScope.launch {
            try {
                val response = repository.getTVPopular(page)
                val tvResponse = response.body()
                tvResponse?.let {
                    _tvPopular.value = it.results
                }
            } catch (e: Exception) {
                Log.d(TAG, "Error: " + e.message)
            }
        }
    }

    fun getTVTopRated(page: Int) {
        viewModelScope.launch {
            try {
                val response = repository.getTVTopRated(page)
                val tvResponse = response.body()
                tvResponse?.let {
                    _tvTopRated.value = it.results
                }
            } catch (e: Exception) {
                Log.d(TAG, "Error: " + e.message)
            }
        }
    }

    fun getTVAiringToday(page: Int) {
        viewModelScope.launch {
            try {
                val response = repository.getTVAiringToday(page)
                val tvResponse = response.body()
                tvResponse?.let {
                    _tvAiringToday.value = it.results
                }
            } catch (e: Exception) {
                Log.d(TAG, "Error: " + e.message)
            }
        }
    }

    fun getTvVideos(TvId: String) {
        viewModelScope.launch {
            try {
                val response = repository.getTVVideos(TvId)
                val videoRespone = response.body()
                videoRespone?.let {
                    _tvVideos.value = it
                }

            } catch (e: Exception) {
                Log.d(TAG, "Error: " + e.message)
            }
        }
    }

    fun getTvCredits(TvId: String) {
        viewModelScope.launch {
            try {
                val response = repository.getTVCredits(TvId)
                val creditResponse = response.body()
                creditResponse?.let {
                    _tvCredits.value = it
                }
            } catch (e: Exception) {
                Log.d(TAG, "Error: " + e.message)
            }
        }
    }

    fun getTVSimilar(TvId: String) {
        viewModelScope.launch {
            try {
                val response = repository.getTVSimilar(TvId)
                val tvResponse = response.body()
                tvResponse?.let {
                    _tvSimilar.value = it.results
                }
            } catch (e: Exception) {
                Log.d(TAG, "Error: " + e.message)
            }
        }
    }

    fun getTVDetails(TvId: String) {
        viewModelScope.launch {
            try {
                val response = repository.getTVDetails(TvId)
                val tvResponse = response.body()
                tvResponse?.let {
                    _tvDetails.value = it
                }
            } catch (e: Exception) {
                Log.d(TAG, "Error: " + e.message)
            }
        }
    }

    fun getTVSeasonDetails(TvId: String, SeasonId: String) {
        viewModelScope.launch {
            try {
                val response = repository.getTVSeasonDetails(TvId, SeasonId)
                val tvResponse = response.body()
                tvResponse?.let {
                    _tvSeasonDetails.value = it
                }
            } catch (e: Exception) {
                Log.d(TAG, "Error: " + e.message)
            }
        }
    }

    fun getImdbID(tmdbID: String, mediaType: String) {
        viewModelScope.launch {
            try {
                if (mediaType == "movie") {
                    val response = repository.getMovieImdbId(tmdbID)
                    val responseBody = response.body()
                    responseBody?.let {
                        _imdbID.value = it
                    }
                }
                else {
                    val response = repository.getTvImdbId(tmdbID)
                    val responseBody = response.body()
                    responseBody?.let {
                        _imdbID.value = it
                    }
                }
            } catch (e: Exception) {
                Log.d(TAG, "Error: " + e.message)
            }
        }
    }

    fun getNetworkTV(network: Int, page: Int) {
        viewModelScope.launch {
            try {
                val response = repository.getNetworkTV(network, page)
                val tvResponse = response.body()
                tvResponse?.let {
                    _networkTV.value = it.results
                }
            } catch (e: Exception) {
                Log.d(TAG, "Error: " + e.message)
            }
        }
    }
}

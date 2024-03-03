package com.example.cineflix

import com.example.cineflix.Model.Credit
import com.example.cineflix.Model.Movie
import com.example.cineflix.Model.MovieResponse
import com.example.cineflix.Model.SearchMulti
import com.example.cineflix.Model.SearchMultiResponse
import com.example.cineflix.Model.TVResponse
import com.example.cineflix.Model.Video
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import javax.inject.Inject


const val API_KEY = "8012e4149af0c58d8ecbd982582fcbf0"
const val LANGUAGE = "vi-VN"

private val retrofit: Retrofit by lazy {
    Retrofit.Builder()
        .baseUrl("https://api.themoviedb.org/3/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}


val movieApiService : ApiService by lazy {
    retrofit.create(ApiService::class.java)
}

interface ApiService {
    @GET("movie/popular")
    suspend fun getPopularMovie(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int,
        @Query("language") language: String
    ):Response<MovieResponse>

    @GET("movie/top_rated")
    suspend fun getTopRatedMovie(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int,
        @Query("language") language: String
    ):Response<MovieResponse>

    @GET("movie/now_playing")
    suspend fun getNowPlayingMovie(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int,
        @Query("language") language: String
    ):Response<MovieResponse>

    @GET("movie/{movie_id}/videos")
    suspend fun getMovieVideos(
        @Path("movie_id") movieId: String,
        @Query("api_key") apiKey: String
    ):Response<Video>

    @GET("movie/{movie_id}/credits")
    suspend fun getMovieCredits(
        @Path("movie_id") movieId: String,
        @Query("api_key") apiKey: String
    ): Response<Credit>

    @GET("movie/{movie_id}/similar")
    suspend fun getMovieSimilar(
        @Path("movie_id") movieId: String,
        @Query("api_key") apiKey: String
    ): Response<MovieResponse>

     @GET("search/multi")
    suspend fun getSearchMulti(
        @Query("query") query: String,
        @Query("api_key") apiKey: String,
        @Query("language") language: String
    ): Response<SearchMultiResponse>

    @GET("tv/popular")
    suspend fun getTVPopular(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int,
        @Query("language") language: String
    ) : Response<TVResponse>

    @GET("tv/top_rated")
    suspend fun getTVTopRated(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int,
        @Query("language") language: String
    ) : Response<TVResponse>

    @GET("tv/airing_today")
    suspend fun getTVAiringToday(
        @Query("api_key") apiKey: String,
        @Query("page") page: Int,
        @Query("language") language: String
    ) : Response<TVResponse>

    @GET("movie/{tv_id}/videos")
    suspend fun getTVVideos(
        @Path("tv_id") TVId: String,
        @Query("api_key") apiKey: String
    ):Response<Video>

    @GET("tv/{tv_id}/credits")
    suspend fun getTVCredits(
        @Path("tv_id") TVId: String,
        @Query("api_key") apiKey: String
    ): Response<Credit>

    @GET("tv/{series_id}/similar")
    suspend fun getTVSimilar(
        @Path("series_id") TvId: String,
        @Query("api_key") apiKey: String
    ) : Response<TVResponse>
}

class MovieRepository {
    suspend fun getPopularMovies(page: Int): Response<MovieResponse> {
        return movieApiService.getPopularMovie(API_KEY, page, LANGUAGE)
    }
    suspend fun getTopRatedMovies(page: Int): Response<MovieResponse> {
        return movieApiService.getTopRatedMovie(API_KEY, page, LANGUAGE)
    }
    suspend fun getNowPlayingMovies(page: Int): Response<MovieResponse> {
        return movieApiService.getNowPlayingMovie(API_KEY, page, LANGUAGE)
    }
    suspend fun getMovieVideos(movieId: String): Response<Video> {
        return movieApiService.getMovieVideos(movieId, API_KEY)
    }
    suspend fun getMovieCredits(movieId: String): Response<Credit>{
        return movieApiService.getMovieCredits(movieId, API_KEY)
    }
    suspend fun getMovieSimilar(movieId: String): Response<MovieResponse>{
        return movieApiService.getMovieSimilar(movieId, API_KEY)
    }
    suspend fun getSearchMulti(query: String): Response<SearchMultiResponse> {
        return movieApiService.getSearchMulti(query, API_KEY, LANGUAGE)
    }
    suspend fun getTVPopular(page: Int) : Response<TVResponse> {
        return movieApiService.getTVPopular(API_KEY, page, LANGUAGE)
    }
    suspend fun getTVTopRated(page: Int) : Response<TVResponse> {
        return movieApiService.getTVTopRated(API_KEY, page, LANGUAGE)
    }
    suspend fun getTVAiringToday(page: Int) : Response<TVResponse> {
        return movieApiService.getTVAiringToday(API_KEY, page, LANGUAGE)
    }
    suspend fun getTVVideos(TVId: String) : Response<Video> {
        return movieApiService.getTVVideos(TVId, API_KEY)
    }
    suspend fun getTVCredits(TVId: String) : Response<Credit> {
        return movieApiService.getTVCredits(TVId, API_KEY)
    }
    suspend fun getTVSimilar(TVId: String) : Response<TVResponse> {
        return movieApiService.getTVSimilar(TVId, API_KEY)
    }
}


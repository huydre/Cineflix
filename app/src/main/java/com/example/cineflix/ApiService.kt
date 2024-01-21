package com.example.cineflix

import com.example.cineflix.Model.Movie
import com.example.cineflix.Model.MovieResponse
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Query
import javax.inject.Inject

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
        @Query("page") page: Int
    ):Response<MovieResponse>
}

class MovieRepository {
    suspend fun getPopularMovies(apiKey: String, page: Int): Response<MovieResponse> {
        return movieApiService.getPopularMovie(apiKey, page)
    }
}


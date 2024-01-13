package com.example.cineflix.Utils.movieList.remote

import com.example.cineflix.Utils.movieList.remote.respond.MovieListDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface MovieAPI {
    @GET("movie/{category}")
    suspend fun getMovieList(
        @Path("category") category: String,
        @Query("page") page: Int,
        @Query("api_key") api_key: String = API_KEY
    ) : MovieListDto

    companion object{
        const val BASE_URL = "https://api.themoviedb.org/3/"
        const val IMAGE_BASE_URL = "https://image.tmdb.org/t/p/w500"
        const val API_KEY = "eyJhbGciOiJIUzI1NiJ9.eyJhdWQiOiI4MDEyZTQxNDlhZjBjNThkOGVjYmQ5ODI1ODJmY2JmMCIsInN1YiI6IjY0YzdlNmM5NDFhYWM0MGZiNzE0NzExNyIsInNjb3BlcyI6WyJhcGlfcmVhZCJdLCJ2ZXJzaW9uIjoxfQ.wQV34USREj73_anxRXZS4vimk4QvKEZ1_9D3JOxzNYo"
    }
}
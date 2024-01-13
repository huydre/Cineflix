package com.example.cineflix.Utils.movieList.domain.repository

import com.example.cineflix.Utils.Resource
import com.example.cineflix.Utils.movieList.domain.model.Movie
import kotlinx.coroutines.flow.Flow


interface MovieListRepository {
    suspend fun getMovieList(
        forceFetchFromRemote: Boolean,
        category: String,
        page: Int
    ): Flow<Resource<List<Movie>>>

    suspend fun getMovie(id: Int): Flow<Resource<Movie>>
}
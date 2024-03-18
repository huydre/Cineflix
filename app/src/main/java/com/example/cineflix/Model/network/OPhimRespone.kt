package com.example.cineflix.Model.network

data class OPhimRespone(
    val episodes: List<Episode>,
    val movie: MovieX,
    val msg: String,
    val status: Boolean
)
package com.example.cineflix.Model

data class SeasonDetails(
    val _id: String,
    val air_date: String,
    val episodes: List<EpisodeX>,
    val id: Int,
    val name: String,
    val overview: String,
    val poster_path: String,
    val season_number: Int,
    val vote_average: Double
)
package com.example.cineflix.Model

import java.io.Serializable


class MovieModel(
//    val id : String,
//    val original_language:String,
//    val production_countries:List<Prod>,
    val title: String,
    val poster_path : String,
//    val release_date : String
) : Serializable {

}

data class Prod(
    val iso_3166_1 : String,
    val name: String
)

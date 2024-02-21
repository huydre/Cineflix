package com.example.cineflix.View.Activities

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import com.example.cineflix.Adapters.NowPlayingListAdapter
import com.example.cineflix.Model.Movie
import com.example.cineflix.R
import kotlinx.serialization.json.Json

class MovieDetailsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_details)

        val movieId = intent.getIntExtra("movie_id",-1)
        val movieTitle = intent.getStringExtra("movie_title")
        val movieYear = intent.getStringExtra("movie_year")
        val movieOverview = intent.getStringExtra("movie_overview")

        val title = findViewById<TextView>(R.id.movieTitle)
        val year = findViewById<TextView>(R.id.movieYear)
        val overview = findViewById<TextView>(R.id.movieOverview)

        title.text = movieTitle
        year.text = movieYear.toString().substring(0,4)
        overview.text = movieOverview

    }
}
package com.example.cineflix.View.Activities

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.MediaController
import android.widget.TextView
import android.widget.VideoView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.cineflix.MovieRepository
import com.example.cineflix.R
import com.example.cineflix.ViewModel.MovieViewModel
import com.example.cineflix.ViewModel.MovieViewModelFactory
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView


class MovieDetailsActivity : AppCompatActivity() {

    private lateinit var movieViewModel: MovieViewModel

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {

        val repository = MovieRepository()
        val movieViewModelFactory = MovieViewModelFactory(repository)
        movieViewModel = ViewModelProvider(this, movieViewModelFactory).get(MovieViewModel::class.java)

        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        setContentView(R.layout.activity_movie_details)

        val movieId = intent.getIntExtra("movie_id",0)
        val movieTitle = intent.getStringExtra("movie_title")
        val movieYear = intent.getStringExtra("movie_year")
        val movieOverview = intent.getStringExtra("movie_overview")

        val title = findViewById<TextView>(R.id.movieTitle)
        val year = findViewById<TextView>(R.id.movieYear)
        val overview = findViewById<TextView>(R.id.movieOverview)
        val actor = findViewById<TextView>(R.id.movieActor)

        title.text = movieTitle
        year.text = movieYear.toString().substring(0,4)
        overview.text = movieOverview

        movieViewModel.getMovieVideos(movieId.toString())

        val youTubePlayerView = findViewById<YouTubePlayerView>(R.id.youtube_player_view)
        lifecycle.addObserver(youTubePlayerView)

        youTubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                movieViewModel.movieVideos.observe(this@MovieDetailsActivity) { videos ->
                    videos?.let {
                        val videoId = it.results.firstOrNull()?.key ?: ""
                        youTubePlayer.loadVideo(videoId, 0f)
                    }
                }
            }
        })

        // Get movie credits
        movieViewModel.getMovieCredits(movieId.toString())

        movieViewModel.movieCredits.observe(this@MovieDetailsActivity) { credit ->
            credit?.let {
                val credits = credit.cast.sortedByDescending { it.popularity }.subList(0,5)
                val names = credits.map { it.name }
//                val actorList = names.joinToString {  }
                actor.text = "Diễn viên: " + names.joinToString(separator = ", ") + "..."
            }
        }

    }
}
package com.example.cineflix.View.Activities

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.annotation.DimenRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.example.cineflix.Adapters.SimilarListAdapter
import com.example.cineflix.MovieRepository
import com.example.cineflix.R
import com.example.cineflix.ViewModel.MovieViewModel
import com.example.cineflix.ViewModel.MovieViewModelFactory
import com.google.android.material.button.MaterialButton
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView


class MovieDetailsActivity : AppCompatActivity() {

    private lateinit var movieViewModel: MovieViewModel
    private lateinit var similarListAdapter: SimilarListAdapter

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

        // Similar View
        val similarView: RecyclerView = findViewById(R.id.similarView)

        val spacing = resources.getDimensionPixelSize(R.dimen.grid_spacing) // Khoảng cách giữa các item
        val includeEdge = true // Bao gồm cả mép của RecyclerView

        val layoutManager = GridLayoutManager(this, 3)
        similarView.layoutManager = layoutManager
        similarListAdapter = SimilarListAdapter(emptyList())
        similarView.adapter = similarListAdapter

        similarView.addItemDecoration(GridSpacingItemDecoration(3,spacing, includeEdge))

//        similarView.layoutManager = GridLayoutManager(this, 3)
        similarListAdapter = SimilarListAdapter(emptyList())
        similarView.adapter = similarListAdapter
        movieViewModel.getMovieSimilar(movieId.toString())

        movieViewModel.movieSimilar.observe(this, Observer { movies ->
            movies?.let {
                similarListAdapter.setMovies(it.subList(0,9))
                Log.d(TAG, "onCreate: " + it)
            }
        })

        //Play button
        val playbtn = findViewById<MaterialButton>(R.id.playBtn)
        playbtn.setOnClickListener {
            val intent = Intent(this, MoviePlayerActivity::class.java)
            intent.putExtra("movie_id", movieId.toString())
            startActivity(intent)
        }

    }
}


class GridSpacingItemDecoration(private val spanCount: Int, private val spacing: Int, private val includeEdge: Boolean) : RecyclerView.ItemDecoration() {

    override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
        val position = parent.getChildAdapterPosition(view) // item position
        val column = position % spanCount // item column

        if (includeEdge) {
            outRect.left = spacing - column * spacing / spanCount // spacing - column * ((1f / spanCount) * spacing)
            outRect.right = (column + 1) * spacing / spanCount // (column + 1) * ((1f / spanCount) * spacing)

            if (position < spanCount) { // top edge
                outRect.top = spacing
            }
            outRect.bottom = spacing + 15 // item bottom
        } else {
            outRect.left = column * spacing / spanCount // column * ((1f / spanCount) * spacing)
            outRect.right = spacing - (column + 1) * spacing / spanCount // spacing - (column + 1) * ((1f / spanCount) * spacing)

            if (position >= spanCount) {
                outRect.top = spacing // item top
            }
        }
    }
}
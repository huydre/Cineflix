package com.example.cineflix.View.Activities

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.cineflix.Adapters.SimilarListAdapter
import com.example.cineflix.Model.local.watching.ContinueWatchingDatabase
import com.example.cineflix.Model.local.watching.ContinueWatchingRepository
import com.example.cineflix.MovieRepository
import com.example.cineflix.OPhimRepository
import com.example.cineflix.R
import com.example.cineflix.ViewModel.ContinueWatchingViewModel
import com.example.cineflix.ViewModel.ContinueWatchingViewModelFactory
import com.example.cineflix.ViewModel.MovieViewModel
import com.example.cineflix.ViewModel.MovieViewModelFactory
import com.example.cineflix.ViewModel.OPhimViewModel
import com.example.cineflix.ViewModel.OPhimViewModelFactory
import com.google.android.material.button.MaterialButton
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.math.log


class MovieDetailsActivity : AppCompatActivity() {

    private lateinit var movieViewModel: MovieViewModel
    private lateinit var similarListAdapter: SimilarListAdapter
    private lateinit var oPhimViewModel: OPhimViewModel
    private lateinit var posterPath: String
    private var playbackPosition: Long = 0
    private var isWatching: Boolean = false

    private lateinit var continueWatchingRepository: ContinueWatchingRepository
    private val database by lazy { ContinueWatchingDatabase.getInstance(this) }
    private val watchHistoryDao by lazy { database.watchDAO() }
    private lateinit var viewModelFactory: ContinueWatchingViewModelFactory
    private val viewModel: ContinueWatchingViewModel by viewModels(
        factoryProducer = {
            viewModelFactory
        }
    )


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
        val movieBackdrop = intent.getStringExtra("movie_backdropPath")
        posterPath = intent.getStringExtra("poster_path").toString()
//        val slug = ConvertNameToSlug(movieTitle.toString())
        val backBtn = findViewById<MaterialButton>(R.id.backBtn)

        val title = findViewById<TextView>(R.id.movieTitle)
        val year = findViewById<TextView>(R.id.movieYear)
        val overview = findViewById<TextView>(R.id.movieOverview)
        val actor = findViewById<TextView>(R.id.movieActor)
        val backdrop = findViewById<ImageView>(R.id.MovieBackdrop)

        backBtn.setOnClickListener {
            finish()
        }

        title.text = movieTitle
        year.text = movieYear.toString().substring(0,4)
        overview.text = movieOverview
        backdrop.load("https://media.themoviedb.org/t/p/w780/${movieBackdrop}")

        if (movieOverview.isNullOrBlank()) {
            overview.visibility = View.GONE
        }

        // Get movie credits
        movieViewModel.getMovieCredits(movieId.toString())

        movieViewModel.movieCredits.observe(this@MovieDetailsActivity) { credit ->
            credit?.let {
                val credits = credit.cast.sortedByDescending { it.popularity }.subList(0,5)
                val names = credits.map { it.name }
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
        similarView.adapter = similarListAdapter
        movieViewModel.getMovieSimilar(movieId.toString())

        movieViewModel.movieSimilar.observe(this, Observer { movies ->
            movies?.let {
                similarListAdapter.setMovies(it.subList(0,9))
            }
        })

        //Play button
        val playbtn = findViewById<MaterialButton>(R.id.playBtn)

        playbtn.setOnClickListener{
            val intent = Intent(this, MoviePlayerActivity::class.java)
            if (isWatching) {
                intent.putExtra("movie_id", movieId)
                intent.putExtra("media_type", "movie")
                intent.putExtra("title", movieTitle)
                intent.putExtra("poster_path", posterPath)
                intent.putExtra("progress", playbackPosition)
            }
            else {
                intent.putExtra("movie_id", movieId)
                intent.putExtra("media_type", "movie")
                intent.putExtra("title", movieTitle)
                intent.putExtra("poster_path", posterPath)
            }
            startActivity(intent)
        }

        //Continue Watching
        continueWatchingRepository = ContinueWatchingRepository(watchHistoryDao)
        viewModelFactory = ContinueWatchingViewModelFactory(watchHistoryDao)
        viewModel.getProgressTest(movieId).observe(this, Observer { watchFrom ->
            watchFrom?.let {
                playbackPosition = it.progress
                isWatching = true
                if (isWatching) {
                    playbtn.setText("Tiếp tục xem")
                }
            }
        })
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
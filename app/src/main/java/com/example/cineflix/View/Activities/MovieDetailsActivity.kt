package com.example.cineflix.View.Activities

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.cineflix.Adapters.SimilarListAdapter
import com.example.cineflix.Model.local.playlist.PlayList
import com.example.cineflix.Model.local.playlist.PlayListDatabase
import com.example.cineflix.Model.local.playlist.PlayListRepository
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
import com.example.cineflix.ViewModel.PlayListViewModel
import com.example.cineflix.ViewModel.PlaylistViewModelFactory
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
    private var playbackPosition: Long = 0
    private var isWatching: Boolean = false
    private var isAdded: Boolean = false
    private var movieTitle: String = ""
    private var movieYear: String = ""
    private var movieOverview: String = ""
    private var movieBackdrop: String = ""
    private  var posterPath: String = ""

    private lateinit var continueWatchingRepository: ContinueWatchingRepository
    private val database by lazy { ContinueWatchingDatabase.getInstance(this) }
    private val watchHistoryDao by lazy { database.watchDAO() }
    private lateinit var viewModelFactory: ContinueWatchingViewModelFactory
    private val viewModel: ContinueWatchingViewModel by viewModels(
        factoryProducer = {
            viewModelFactory
        }
    )

    private lateinit var playListRepository: PlayListRepository
    private val playListdatabase by lazy { PlayListDatabase.getInstance(this@MovieDetailsActivity) }
    private val playListDao by lazy { playListdatabase.playListDao() }
    private lateinit var playListViewModelFactory: PlaylistViewModelFactory
    private val playListViewModel: PlayListViewModel by viewModels(
        factoryProducer = {
            playListViewModelFactory
        }
    )


    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {

        val repository = MovieRepository()
        val movieViewModelFactory = MovieViewModelFactory(repository)
        movieViewModel = ViewModelProvider(this, movieViewModelFactory).get(MovieViewModel::class.java)

        playListRepository = PlayListRepository(playListDao)
        playListViewModelFactory = PlaylistViewModelFactory(playListRepository)

        super.onCreate(savedInstanceState)
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)
        setContentView(R.layout.activity_movie_details)

        val movieId = intent.getIntExtra("movie_id",0)
        val backBtn = findViewById<MaterialButton>(R.id.backBtn)

        val title = findViewById<TextView>(R.id.movieTitle)
        val year = findViewById<TextView>(R.id.movieYear)
        val overview = findViewById<TextView>(R.id.movieOverview)
        val actor = findViewById<TextView>(R.id.movieActor)
        val backdrop = findViewById<ImageView>(R.id.MovieBackdrop)
        val addBtn = findViewById<LinearLayout>(R.id.add_btn)
        val addBtnIcon = findViewById<ImageView>(R.id.add_btn_icon)
        val movieRate = findViewById<TextView>(R.id.movieRate)
        val starIcon = findViewById<ImageView>(R.id.star_icon)

        // Get movie details
        movieViewModel.getMovieDetails(movieId.toString())
        movieViewModel.movieDetails.observe(this@MovieDetailsActivity) {movie ->
            movie?.let {
                movieTitle = it.title
                movieYear = it.release_date
                movieOverview = it.overview
                movieBackdrop = it.backdrop_path
                posterPath = it.poster_path
                title.text = movieTitle
                year.text = movieYear.substring(0,4)
                overview.text = movieOverview

                val rated = it.vote_average
                movieRate.text = rated.toString().substring(0,3)
                if (rated < 7 && rated >= 4) {
                    starIcon.setColorFilter(Color.parseColor("#D2D531"))
                    movieRate.setTextColor(Color.parseColor("#D2D531"))
                }
                else if (rated < 4 ) {
                    starIcon.setColorFilter(Color.parseColor("#DB2360"))
                    movieRate.setTextColor(Color.parseColor("#DB2360"))
                }

                backdrop.load("https://media.themoviedb.org/t/p/w780/${movieBackdrop}")
                if (movieOverview.isNullOrBlank()) {
                    overview.visibility = View.GONE
                }
            }

        }

        backBtn.setOnClickListener {
            finish()
        }

        playListViewModel.getPlayList(movieId).observe(this@MovieDetailsActivity, Observer { playLists ->
            if (playLists != null) isAdded = true
            if (isAdded) {
                addBtnIcon.setImageResource(R.drawable.ic_check)
            }
        })

        // Add to playlist
        addBtn.setOnClickListener {
            val playListItem = PlayList(
                movieTitle.toString(),
                movieBackdrop.toString(),
                movieId,
                "movie"
            )
            playListViewModel.viewModelScope.launch {
                if (!isAdded) playListViewModel.insert(playListItem)
                else {
                    playListViewModel.delete(playListItem)
                    isAdded = false
                    addBtnIcon.setImageResource(R.drawable.add)
                }
            }
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
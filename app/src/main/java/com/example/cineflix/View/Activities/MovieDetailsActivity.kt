package com.example.cineflix.View.Activities

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cineflix.Adapters.SimilarListAdapter
import com.example.cineflix.MovieRepository
import com.example.cineflix.OPhimRepository
import com.example.cineflix.R
import com.example.cineflix.ViewModel.MovieViewModel
import com.example.cineflix.ViewModel.MovieViewModelFactory
import com.example.cineflix.ViewModel.OPhimViewModel
import com.example.cineflix.ViewModel.OPhimViewModelFactory
import com.google.android.material.button.MaterialButton
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView
import kotlinx.coroutines.runBlocking


class MovieDetailsActivity : AppCompatActivity() {

    private lateinit var movieViewModel: MovieViewModel
    private lateinit var similarListAdapter: SimilarListAdapter
    private lateinit var oPhimViewModel: OPhimViewModel
    private lateinit var videoUrl: String


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
        val slug = ConvertNameToSlug(movieTitle.toString())

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
//        similarListAdapter = SimilarListAdapter(emptyList())
        similarView.adapter = similarListAdapter
        movieViewModel.getMovieSimilar(movieId.toString())

        movieViewModel.movieSimilar.observe(this, Observer { movies ->
            movies?.let {
                similarListAdapter.setMovies(it.subList(0,9))
            }
        })

        videoUrl = ""
        //Play button
        val playbtn = findViewById<MaterialButton>(R.id.playBtn)
        playbtn.setOnClickListener{
            val intent = Intent(this, MoviePlayerActivity::class.java)
            //get Link from OPhim
            val repositorys = OPhimRepository()
            val oPhimViewModelFactory = OPhimViewModelFactory(repositorys)
            oPhimViewModel = ViewModelProvider(this, oPhimViewModelFactory).get(OPhimViewModel::class.java)
            oPhimViewModel.getOPhimDetails(slug)
            oPhimViewModel.oPhimDetails.observe(this@MovieDetailsActivity) { details ->
                details?.let {
                    val videoUrl = it.get(0).episodes.get(0).server_data.get(0).link_m3u8
                    if (videoUrl.isNotEmpty()) {
                        intent.putExtra("movie_id", movieId.toString())
                        intent.putExtra("video_url", videoUrl)
                        intent.putExtra("movie_title", movieTitle)
                        startActivity(intent)
                    }
                    else {
                        Toast.makeText(this, "Video URL is unavailable", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun ConvertNameToSlug(name: String) : String {
        val regex = Regex("[^a-zA-Z0-9\\s]")
        val slug = convert(name).replace(regex, "")
            .toLowerCase()
            .replace("\\s+".toRegex(), "-")
        return slug
    }

    fun convert(str: String): String {
        var str = str
        str = str.replace("à|á|ạ|ả|ã|â|ầ|ấ|ậ|ẩ|ẫ|ă|ằ|ắ|ặ|ẳ|ẵ".toRegex(), "a")
        str = str.replace("è|é|ẹ|ẻ|ẽ|ê|ề|ế|ệ|ể|ễ".toRegex(), "e")
        str = str.replace("ì|í|ị|ỉ|ĩ".toRegex(), "i")
        str = str.replace("ò|ó|ọ|ỏ|õ|ô|ồ|ố|ộ|ổ|ỗ|ơ|ờ|ớ|ợ|ở|ỡ".toRegex(), "o")
        str = str.replace("ù|ú|ụ|ủ|ũ|ư|ừ|ứ|ự|ử|ữ".toRegex(), "u")
        str = str.replace("ỳ|ý|ỵ|ỷ|ỹ".toRegex(), "y")
        str = str.replace("đ".toRegex(), "d")
        str = str.replace("À|Á|Ạ|Ả|Ã|Â|Ầ|Ấ|Ậ|Ẩ|Ẫ|Ă|Ằ|Ắ|Ặ|Ẳ|Ẵ".toRegex(), "A")
        str = str.replace("È|É|Ẹ|Ẻ|Ẽ|Ê|Ề|Ế|Ệ|Ể|Ễ".toRegex(), "E")
        str = str.replace("Ì|Í|Ị|Ỉ|Ĩ".toRegex(), "I")
        str = str.replace("Ò|Ó|Ọ|Ỏ|Õ|Ô|Ồ|Ố|Ộ|Ổ|Ỗ|Ơ|Ờ|Ớ|Ợ|Ở|Ỡ".toRegex(), "O")
        str = str.replace("Ù|Ú|Ụ|Ủ|Ũ|Ư|Ừ|Ứ|Ự|Ử|Ữ".toRegex(), "U")
        str = str.replace("Ỳ|Ý|Ỵ|Ỷ|Ỹ".toRegex(), "Y")
        str = str.replace("Đ".toRegex(), "D")
        return str
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
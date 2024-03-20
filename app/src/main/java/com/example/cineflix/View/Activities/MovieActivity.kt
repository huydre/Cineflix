package com.example.cineflix.View.Activities

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cineflix.Adapters.MovieAdapter
import com.example.cineflix.Adapters.TVAdapter
import com.example.cineflix.MovieRepository
import com.example.cineflix.R
import com.example.cineflix.View.Fragments.SearchFragment
import com.example.cineflix.ViewModel.MovieViewModel
import com.example.cineflix.ViewModel.MovieViewModelFactory

class MovieActivity : AppCompatActivity() {

    private lateinit var movieViewModel: MovieViewModel
    private lateinit var movieAdapter: MovieAdapter
    private lateinit var tvAdapter: TVAdapter

    private var currentPage = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie)
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        val repository = MovieRepository()
        val movieViewModelFactory = MovieViewModelFactory(repository)
        movieViewModel =
            ViewModelProvider(this, movieViewModelFactory).get(MovieViewModel::class.java)

        val mediaType = intent.getStringExtra("media_type")
        val title = intent.getStringExtra("title")
        val network = intent.getIntExtra("network", 0)

        val spacing = resources.getDimensionPixelSize(R.dimen.grid_spacing)
        val recyclerView = findViewById<RecyclerView>(R.id.rview1)
        val layoutManager = GridLayoutManager(this, 3)
        recyclerView.layoutManager = layoutManager
        recyclerView.addItemDecoration(GridSpacingItemDecoration(3,spacing, true))


        val titleView = findViewById<TextView>(R.id.title)
        titleView.text = title

        val backBtn = findViewById<ImageView>(R.id.backBtn)
        backBtn.setOnClickListener{
            finish()
        }

        movieAdapter = MovieAdapter(emptyList())
        tvAdapter = TVAdapter(emptyList())

        if (mediaType == "movie") {
            recyclerView.adapter = movieAdapter
        }
        else {
            recyclerView.adapter = tvAdapter
            // Get the movie details
            movieViewModel.getNetworkTV(network,currentPage)
            movieViewModel.networkTV.observe(this, Observer { movies ->
                movies?.let {
                    Log.d(TAG, "onCreate: " + it)
                    tvAdapter.setMovies(it)
                }
            })
        }

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (!recyclerView.canScrollVertically(1)) {
                    currentPage++
                    movieViewModel.getNetworkTV(network, currentPage)
                }
            }
        })
    }
}
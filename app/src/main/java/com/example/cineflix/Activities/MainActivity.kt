package com.example.cineflix.Activities

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cineflix.Adapters.MovieListAdapter
import com.example.cineflix.MovieRepository
import com.example.cineflix.R
import com.example.cineflix.ViewModel.MovieViewModel
import com.example.cineflix.ViewModel.MovieViewModelFactory
import dagger.hilt.android.AndroidEntryPoint


class MainActivity : AppCompatActivity() {

    private lateinit var movieViewModelFactory: MovieViewModelFactory
    private lateinit var movieViewModel: MovieViewModel
    private lateinit var movieListAdapter: MovieListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Khởi tạo ViewModelFactory
        val repository = MovieRepository() // Thay thế bằng đối tượng MovieRepository thực tế của bạn
        val movieViewModelFactory = MovieViewModelFactory(repository)

        // Khởi tạo ViewModel thông qua ViewModelProvider sử dụng ViewModelFactory
        movieViewModel = ViewModelProvider(this, movieViewModelFactory).get(MovieViewModel::class.java)

        // Khởi tạo RecyclerView
        val recyclerView: RecyclerView = findViewById(R.id.view1)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        movieListAdapter = MovieListAdapter(emptyList())
        recyclerView.adapter = movieListAdapter

        movieViewModel.getPopularMovies("8012e4149af0c58d8ecbd982582fcbf0", 1)

        // Quan sát dữ liệu từ ViewModel và cập nhật danh sách phim
        movieViewModel.popularMovies.observe(this, Observer { movies ->
            movies?.let {
                movieListAdapter.setMovies(it)
//                Log.d(TAG, "onCreate: ${movieListAdapter.lst}")
            }
        })

    }
}
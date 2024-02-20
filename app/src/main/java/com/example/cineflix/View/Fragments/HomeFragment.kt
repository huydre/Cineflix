package com.example.cineflix.View.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cineflix.Adapters.PopularListAdapter
import com.example.cineflix.MovieRepository
import com.example.cineflix.R
import com.example.cineflix.ViewModel.MovieViewModel
import com.example.cineflix.ViewModel.MovieViewModelFactory

// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class HomeFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var movieViewModel: MovieViewModel
    private lateinit var movieListAdapter: PopularListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val repository = MovieRepository()
        val movieViewModelFactory = MovieViewModelFactory(repository)
        movieViewModel = ViewModelProvider(this, movieViewModelFactory).get(MovieViewModel::class.java)

        val view = inflater.inflate(R.layout.fragment_home, container, false)
        val recyclerView: RecyclerView = view.findViewById(R.id.view1)
        recyclerView.layoutManager = LinearLayoutManager(HomeFragment().context, LinearLayoutManager.HORIZONTAL, false)
        movieListAdapter = PopularListAdapter(emptyList())
        recyclerView.adapter = movieListAdapter

        movieViewModel.getPopularMovies(1)

        movieViewModel.popularMovies.observe(viewLifecycleOwner, Observer { movies ->
            movies?.let {
                movieListAdapter.setMovies(it)
//                Log.d(TAG, "onCreate: ${movieListAdapter.lst}")
            }
        })

        // Inflate the layout for this fragment
        return view
    }
}
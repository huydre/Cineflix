package com.example.cineflix.View.Fragments

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cineflix.Adapters.NowPlayingListAdapter
import com.example.cineflix.Adapters.PopularListAdapter
import com.example.cineflix.Adapters.TopRatedListAdapter
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
    private lateinit var popularListAdapter: PopularListAdapter
    private lateinit var topRatedListAdapter: TopRatedListAdapter
    private lateinit var nowPlayingListAdapter: NowPlayingListAdapter

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

        //View 1
        val recyclerView1: RecyclerView = view.findViewById(R.id.view1)
        recyclerView1.layoutManager = LinearLayoutManager(HomeFragment().context, LinearLayoutManager.HORIZONTAL, false)
        popularListAdapter = PopularListAdapter(emptyList())
        recyclerView1.adapter = popularListAdapter

        movieViewModel.getPopularMovies(1)

        movieViewModel.popularMovies.observe(viewLifecycleOwner, Observer { movies ->
            movies?.let {
                popularListAdapter.setMovies(it)

                if (!it.isEmpty()) {
                    val prgbar1 =  view.findViewById<ProgressBar>(R.id.progressBar)
                    prgbar1.visibility = View.GONE
                }
            }
        })

        //View 2
        val recyclerView2: RecyclerView = view.findViewById(R.id.view2)
        recyclerView2.layoutManager = LinearLayoutManager(HomeFragment().context, LinearLayoutManager.HORIZONTAL, false)
        topRatedListAdapter = TopRatedListAdapter(emptyList())
        recyclerView2.adapter = topRatedListAdapter
        movieViewModel.getTopRatedMovies(1)

        movieViewModel.topRatedMovies.observe(viewLifecycleOwner, Observer { movies ->
            movies?.let {
                topRatedListAdapter.setMovies(it)

                if (!it.isEmpty()) {
                    val prgbar2 =  view.findViewById<ProgressBar>(R.id.progressBar2)
                    prgbar2.visibility = View.GONE
                }

            }
        })

        //View 3
        val recyclerView3 : RecyclerView = view.findViewById(R.id.view3)
        recyclerView3.layoutManager = LinearLayoutManager(HomeFragment().context, LinearLayoutManager.HORIZONTAL, false)
        nowPlayingListAdapter = NowPlayingListAdapter(emptyList())
        recyclerView3.adapter = nowPlayingListAdapter
        movieViewModel.getNowPlayingMovies(1)

        movieViewModel.nowPlayingMovies.observe(viewLifecycleOwner, Observer { movies ->
            movies?.let {
                nowPlayingListAdapter.setMovies(it)
                if(it.isNotEmpty()) {
                    val prgbar3 = view.findViewById<ProgressBar>(R.id.progressBar3)
                    prgbar3.visibility = View.GONE
                }
            }
        })

        // Inflate the layout for this fragment
        return view
    }
}
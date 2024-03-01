package com.example.cineflix.View.Fragments

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import android.widget.TextView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cineflix.Adapters.SearchMultiResultAdapter
import com.example.cineflix.MovieRepository
import com.example.cineflix.R
import com.example.cineflix.ViewModel.MovieViewModel
import com.example.cineflix.ViewModel.MovieViewModelFactory

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private lateinit var searchView : SearchView

class SearchFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var movieViewModel: MovieViewModel
    private lateinit var searchMultiResultListAdapter: SearchMultiResultAdapter


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
        val view = inflater.inflate(R.layout.fragment_search, container, false)

        val repository = MovieRepository()
        val movieViewModelFactory = MovieViewModelFactory(repository)

        searchView = view.findViewById(R.id.searchView)

        movieViewModel = ViewModelProvider(this, movieViewModelFactory).get(MovieViewModel::class.java)

//        movieViewModel.searchMulti.observe(viewLifecycleOwner, Observer { results ->
//            results?.let {
//                val lst = it
//                searchMultiResultListAdapter.setMovies(lst)
//
//                Log.d(TAG, "onCreateView: " + it)
//            }
//        })

        val searchResultRc : RecyclerView = view.findViewById(R.id.search_results_rc)

        val layoutManager = GridLayoutManager(context, 3)
        searchResultRc.layoutManager = layoutManager
//        searchResultRc.layoutManager = LinearLayoutManager(SearchFragment().context, LinearLayoutManager.HORIZONTAL, false)
        searchMultiResultListAdapter = SearchMultiResultAdapter(emptyList())
        searchResultRc.adapter = searchMultiResultListAdapter

//        movieViewModel.getSearchMulti("Shameless")

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Logic for when search button is clicked
                if(query.isNullOrEmpty())return false
                movieViewModel.searchMulti.observe(viewLifecycleOwner, Observer { results ->
                    results?.let {
                        val lst = it.filter { it.media_type != "person" }
                        searchMultiResultListAdapter.setMovies(lst)

                        Log.d(TAG, "onCreateView: " + lst.map { it.media_type })
                    }
                })
                movieViewModel.getSearchMulti(query)
                view.findViewById<TextView>(R.id.resultTitle).visibility = View.VISIBLE

                searchView.clearFocus()
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    searchMultiResultListAdapter.setMovies(emptyList())
                    view.findViewById<TextView>(R.id.resultTitle).visibility = View.GONE
                }
                return false
            }

        })

        return view
    }
}
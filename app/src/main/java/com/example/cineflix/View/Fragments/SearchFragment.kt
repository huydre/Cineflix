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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cineflix.Adapters.SearchHistoryAdapter
import com.example.cineflix.Adapters.SearchMultiResultAdapter
import com.example.cineflix.Model.local.searchHistory.QueryRepository
import com.example.cineflix.Model.local.searchHistory.SearchDatabase
import com.example.cineflix.Model.local.searchHistory.SearchHistory
import com.example.cineflix.MovieRepository
import com.example.cineflix.R
import com.example.cineflix.ViewModel.MovieViewModel
import com.example.cineflix.ViewModel.MovieViewModelFactory
import com.example.cineflix.ViewModel.SearchHistoryViewModel
import com.example.cineflix.ViewModel.SearchVMFactory
import com.google.android.material.button.MaterialButton

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"
private lateinit var searchView : SearchView

class SearchFragment : Fragment(){
    private var param1: String? = null
    private var param2: String? = null

    lateinit var rootView: ComposeView

    private val database by lazy { SearchDatabase.getInstance(requireContext()) }
    private val searchHistoryDao by lazy {database.searchDao()}
    private lateinit var viewModelFactory: SearchVMFactory
    private val viewModel: SearchHistoryViewModel by viewModels(
        factoryProducer = {
            viewModelFactory
        }
    )
    private lateinit var queryRepository: QueryRepository
    private lateinit var searchHistoryAdapter: SearchHistoryAdapter

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

        val searchResultRc : RecyclerView = view.findViewById(R.id.search_results_rc)
        val deleteAllBtn : MaterialButton = view.findViewById(R.id.delete_all_button)
        val searchHistoryRc : RecyclerView = view.findViewById(R.id.search_history_rc)


        // Search History

        searchHistoryRc.visibility = View.VISIBLE
        deleteAllBtn.visibility = View.VISIBLE

        queryRepository = QueryRepository(searchHistoryDao)
        viewModelFactory = SearchVMFactory(queryRepository)

        searchHistoryRc.layoutManager = LinearLayoutManager(SearchFragment().context, LinearLayoutManager.VERTICAL, false)
        searchHistoryAdapter = SearchHistoryAdapter(emptyList()) { query, type ->
            if (type) {
                view.findViewById<TextView>(R.id.resultTitle).visibility = View.VISIBLE
                searchView.setQuery(query.query, true)
            }
            else {
                viewModel.deleteRecord(query)
            }
        }
        searchHistoryRc.adapter = searchHistoryAdapter

        val searchHistoryObserver = Observer<List<SearchHistory>> {
            Log.d(TAG, "onCreateView: " + it)
            if (it.isEmpty()) {
                searchHistoryRc.visibility = View.GONE
                deleteAllBtn.visibility = View.GONE
            }
            searchHistoryAdapter.setMovies(it)
        }
        viewModel.queries.observe(viewLifecycleOwner,searchHistoryObserver)

        deleteAllBtn.setOnClickListener {
            viewModel.deleteAll()
        }

        val layoutManager = GridLayoutManager(context, 3)
        searchResultRc.layoutManager = layoutManager
        searchMultiResultListAdapter = SearchMultiResultAdapter(emptyList())
        searchResultRc.adapter = searchMultiResultListAdapter

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Logic for when search button is clicked
                if(query.isNullOrEmpty())return false

                // Add to search history
                viewModel.searchClicked.value = true
                viewModel.queryText.value = query
                val history = SearchHistory(query = query)
                viewModel.addToHistory(history)

                // Call api search
                movieViewModel.searchMulti.observe(viewLifecycleOwner, Observer { results ->
                    results?.let {
                        val lst = it.filter { it.media_type != "person" }
                        searchMultiResultListAdapter.setMovies(lst)

                        Log.d(TAG, "onCreateView: " + lst.map { it.media_type })
                    }
                })
                movieViewModel.getSearchMulti(query)

                view.findViewById<TextView>(R.id.resultTitle).visibility = View.VISIBLE
                searchHistoryRc.visibility = View.GONE
                deleteAllBtn.visibility = View.GONE

                searchView.clearFocus()
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    searchMultiResultListAdapter.setMovies(emptyList())
                    view.findViewById<TextView>(R.id.resultTitle).visibility = View.GONE
                    searchHistoryRc.visibility = View.VISIBLE
                    deleteAllBtn.visibility = View.VISIBLE
                }
                return false
            }

        })

        return view
    }
}
package com.example.cineflix.View.Fragments

import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cineflix.Adapters.EpisodeListAdapter
import com.example.cineflix.Adapters.SimilarTVListAdapter
import com.example.cineflix.MovieRepository
import com.example.cineflix.R
import com.example.cineflix.View.Activities.TvDetailsActivity
import com.example.cineflix.View.Activities.getTvId
import com.example.cineflix.ViewModel.MovieViewModel
import com.example.cineflix.ViewModel.MovieViewModelFactory
import com.google.android.material.button.MaterialButton

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class TvEpisodeFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var movieViewModel: MovieViewModel
    private lateinit var seasonSelect: MaterialButton
    private var selectedSeason : Int = 1
    private lateinit var episodeListAdapter: EpisodeListAdapter
    private var tvId : String = ""
    private var fisrtSeasonIs0 : Int = 0

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
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_tv_episode, container, false)
        tvId = getTvId()

        val repository = MovieRepository()
        val movieViewModelFactory = MovieViewModelFactory(repository)
        movieViewModel = ViewModelProvider(this, movieViewModelFactory).get(MovieViewModel::class.java)

        var items = emptyArray<String>()

        //
        movieViewModel.getTVDetails(tvId)
        movieViewModel.tvDetails.observe(viewLifecycleOwner, Observer { tvs ->
            tvs?.let {
                val newSeasons = it.seasons.map { "Mùa " + it.season_number.toString() }
                items = newSeasons.toTypedArray()
                if (it.seasons.get(0).season_number == 1) {
                    fisrtSeasonIs0 = 1
                }
//                Log.d(TAG, "onCreateView: " + newSeasons)
            }
        })

        seasonSelect = view.findViewById(R.id.season_select)

        seasonSelect.setText("Mùa ${selectedSeason}")

        seasonSelect.setOnClickListener {
            showSingleChoiceDialog(items, selectedSeason)
        }

        //Episode
        val TVEpisodeView = view.findViewById<RecyclerView>(R.id.tvEpisodeView)
        TVEpisodeView.layoutManager = LinearLayoutManager(HomeFragment().context, LinearLayoutManager.VERTICAL, false)
        episodeListAdapter = EpisodeListAdapter(emptyList())
        TVEpisodeView.adapter = episodeListAdapter

        movieViewModel.getTVSeasonDetails(tvId, selectedSeason.toString())
        movieViewModel.tvSeasonDetails.observe(viewLifecycleOwner, Observer { tvs ->
            tvs?.let {
                val lst = it.episodes
                episodeListAdapter.setMovies(lst)
            }
        })

        return view
    }

    private fun showSingleChoiceDialog(items: Array<String>, selectedItemIndex: Int) {
        val builder = AlertDialog.Builder(requireContext())
        builder.apply {
            setItems(items) { dialog, which ->
                val selectedItem = items[which]
                seasonSelect.setText(selectedItem)
                selectedSeason = which + fisrtSeasonIs0

                Log.d(TAG, "showSingleChoiceDialog: " + which)

                movieViewModel.getTVSeasonDetails(tvId, selectedSeason.toString())
                movieViewModel.tvSeasonDetails.observe(viewLifecycleOwner, Observer { tvs ->
                    tvs?.let {
                        val lst = it.episodes
                        episodeListAdapter.setMovies(lst)
                    }
                })

                dialog.dismiss()
            }
        }
        val dialog = builder.create()
        dialog.show()
    }
}

// Nếu bộ phim nào không có mùa 0 thì selectSeason phải chuyển về 0
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
import com.example.cineflix.MovieRepository
import com.example.cineflix.R
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
    private var selectedSeason : Int = 0


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

        val repository = MovieRepository()
        val movieViewModelFactory = MovieViewModelFactory(repository)
        movieViewModel = ViewModelProvider(this, movieViewModelFactory).get(MovieViewModel::class.java)

        var items = emptyArray<String>()


        movieViewModel.getTVDetails("63174")
        movieViewModel.tvDetails.observe(viewLifecycleOwner, Observer { tvs ->
            tvs?.let {
                val newSeasons = it.seasons.map { "Mùa " + it.season_number.toString() }
                items = newSeasons.toTypedArray()
                if (it.seasons.get(0).season_number == 0) {
                    selectedSeason = 1
                }
            }
        })

        seasonSelect = view.findViewById(R.id.season_select)

        seasonSelect.setOnClickListener {
            showSingleChoiceDialog(items, selectedSeason)
        }

        return view
    }

    private fun showSingleChoiceDialog(items: Array<String>, selectedItemIndex: Int) {
        val builder = AlertDialog.Builder(requireContext())
        builder.apply {
            setSingleChoiceItems(items, selectedItemIndex) { dialog, which ->
                val selectedItem = items[which]
                seasonSelect.setText(selectedItem)
                selectedSeason = which
                Toast.makeText(requireContext(), "You selected: $selectedItem", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
        }
        val dialog = builder.create()
        dialog.show()
    }
}
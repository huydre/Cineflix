package com.example.cineflix.View.Fragments

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cineflix.Adapters.SimilarTVListAdapter
import com.example.cineflix.MovieRepository
import com.example.cineflix.R
import com.example.cineflix.View.Activities.GridSpacingItemDecoration
import com.example.cineflix.View.Activities.getTvId
import com.example.cineflix.ViewModel.MovieViewModel
import com.example.cineflix.ViewModel.MovieViewModelFactory
import com.example.cineflix.databinding.FragmentTvEpisodeBinding
import com.example.cineflix.databinding.FragmentTvSimilarBinding

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [TvSimilarFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class TvSimilarFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private var _binding: FragmentTvSimilarBinding? = null
    private val binding get() = _binding!!


    private lateinit var movieViewModel: MovieViewModel
    private lateinit var similarTVListAdapter: SimilarTVListAdapter

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
        val view = inflater.inflate(R.layout.fragment_tv_similar, container, false)

        val tvID = getTvId()

        val repository = MovieRepository()
        val movieViewModelFactory = MovieViewModelFactory(repository)
        movieViewModel = ViewModelProvider(this, movieViewModelFactory).get(MovieViewModel::class.java)

        val similarTVView = view.findViewById<RecyclerView>(R.id.tvSimilarView)
        val spacing = resources.getDimensionPixelSize(R.dimen.grid_spacing) // Khoảng cách giữa các item
        val includeEdge = true // Bao gồm cả mép của RecyclerView

        val layoutManager = GridLayoutManager(context, 3)
        similarTVView.layoutManager = layoutManager
        similarTVListAdapter = SimilarTVListAdapter(emptyList())
        similarTVView.adapter = similarTVListAdapter

        similarTVView.adapter = similarTVListAdapter

        similarTVView.addItemDecoration(GridSpacingItemDecoration(3,spacing, includeEdge))

        movieViewModel.getTVSimilar(tvID)

        movieViewModel.tvSimilar.observe(viewLifecycleOwner, Observer { tvs ->
            tvs?.let {
                similarTVListAdapter.setMovies(it.subList(0,9))
            }
        })

        return view
    }
}
package com.example.cineflix.View.Fragments

import android.content.ContentValues.TAG
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cineflix.Adapters.PlayListAdapter
import com.example.cineflix.Model.local.playlist.PlayListDatabase
import com.example.cineflix.Model.local.playlist.PlayListRepository
import com.example.cineflix.R
import com.example.cineflix.ViewModel.PlayListViewModel
import com.example.cineflix.ViewModel.PlaylistViewModelFactory

class BookmarkFragment : Fragment() {

    private lateinit var playListAdapter: PlayListAdapter

    private lateinit var playListRepository: PlayListRepository
    private val playListdatabase by lazy { PlayListDatabase.getInstance(requireContext()) }
    private val playListDao by lazy { playListdatabase.playListDao() }
    private lateinit var playListViewModelFactory: PlaylistViewModelFactory
    private val playListViewModel: PlayListViewModel by viewModels(
        factoryProducer = {
            playListViewModelFactory
        }
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bookmark, container, false)

        playListRepository = PlayListRepository(playListDao)
        playListViewModelFactory = PlaylistViewModelFactory(playListRepository)

        val recyclerView : RecyclerView = view.findViewById(R.id.playListRC)
        recyclerView.layoutManager = LinearLayoutManager(BookmarkFragment().context, LinearLayoutManager.VERTICAL, false)
        playListAdapter = PlayListAdapter(listOf())
        recyclerView.adapter = playListAdapter

        playListViewModel.getPlayListAll().observe(viewLifecycleOwner, {
            playListAdapter.setMovies(it)
            Log.d(TAG, "onCreateView: " + it)
        })

        return view
    }
}
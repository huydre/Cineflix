package com.example.cineflix.View.Fragments


import android.content.ContentValues.TAG
import android.graphics.Canvas
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cineflix.Adapters.PlayListAdapter
import com.example.cineflix.Model.local.playlist.PlayListDatabase
import com.example.cineflix.Model.local.playlist.PlayListRepository
import com.example.cineflix.R
import com.example.cineflix.ViewModel.PlayListViewModel
import com.example.cineflix.ViewModel.PlaylistViewModelFactory
import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator


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
        playListAdapter = PlayListAdapter(playListViewModel,listOf())
        recyclerView.adapter = playListAdapter

        playListViewModel.getPlayListAll().observe(viewLifecycleOwner, {
            playListAdapter.setMovies(it)
            Log.d(TAG, "onCreateView: " + it)
        })

        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
            override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val item = playListAdapter.lst[position]

                // Remove the item from your data
                playListAdapter.deleteItem(position)

                // Notify the adapter
                playListAdapter.notifyItemRemoved(position)

                // Optionally, undo the deletion:
                // lst.add(position, item)
                // notifyItemInserted(position)
            }

            override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
                RecyclerViewSwipeDecorator.Builder(
                    c,
                    recyclerView,
                    viewHolder,
                    dX,
                    dY,
                    actionState,
                    isCurrentlyActive
                )
                    .addBackgroundColor(
                        ContextCompat.getColor(
                            view.context,
                            R.color.red
                        )
                    )
                    .addActionIcon(R.drawable.trash_ic)
                    .create()
                    .decorate()

                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        return view
    }
}
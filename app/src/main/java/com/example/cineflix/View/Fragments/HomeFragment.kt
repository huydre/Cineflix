package com.example.cineflix.View.Fragments

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.cineflix.Adapters.AiringTodayTVListAdapter
import com.example.cineflix.Adapters.ContinueWatchingListAdapter
import com.example.cineflix.Adapters.NowPlayingListAdapter
import com.example.cineflix.Adapters.PopularListAdapter
import com.example.cineflix.Adapters.PopularListTVAdapter
import com.example.cineflix.Adapters.TopRatedListAdapter
import com.example.cineflix.Adapters.TopRatedTVListAdapter
import com.example.cineflix.Model.local.playlist.PlayList
import com.example.cineflix.Model.local.playlist.PlayListDatabase
import com.example.cineflix.Model.local.playlist.PlayListRepository
import com.example.cineflix.Model.local.watching.ContinueWatching
import com.example.cineflix.Model.local.watching.ContinueWatchingDatabase
import com.example.cineflix.Model.local.watching.ContinueWatchingRepository
import com.example.cineflix.MovieRepository
import com.example.cineflix.R
import com.example.cineflix.View.Activities.MovieActivity
import com.example.cineflix.View.Activities.MoviePlayerActivity
import com.example.cineflix.ViewModel.ContinueWatchingViewModel
import com.example.cineflix.ViewModel.ContinueWatchingViewModelFactory
import com.example.cineflix.ViewModel.MovieViewModel
import com.example.cineflix.ViewModel.MovieViewModelFactory
import com.example.cineflix.ViewModel.PlayListViewModel
import com.example.cineflix.ViewModel.PlaylistViewModelFactory
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.android.material.button.MaterialButton
import com.google.android.material.imageview.ShapeableImageView
import kotlinx.coroutines.launch

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
    private lateinit var popularListTVAdapter: PopularListTVAdapter
    private lateinit var topRatedTVListAdapter: TopRatedTVListAdapter
    private lateinit var airingTodayTVListAdapter: AiringTodayTVListAdapter
    private lateinit var continueWatchingListAdapyer: ContinueWatchingListAdapter

    private var isAdded: Boolean = false

    private lateinit var continueWatchingRepository: ContinueWatchingRepository
    private val database by lazy { ContinueWatchingDatabase.getInstance(requireContext()) }
    private val watchHistoryDao by lazy { database.watchDAO() }
    private lateinit var viewModelFactory: ContinueWatchingViewModelFactory
    private val viewModel: ContinueWatchingViewModel by viewModels(
        factoryProducer = {
            viewModelFactory
        }
    )

    private lateinit var playListRepository: PlayListRepository
    private val playListdatabase by lazy { PlayListDatabase.getInstance(requireContext()) }
    private val playListDao by lazy { playListdatabase.playListDao() }
    private lateinit var playListViewModelFactory: PlaylistViewModelFactory
    private val playListViewModel: PlayListViewModel by viewModels(
        factoryProducer = {
            playListViewModelFactory
        }
    )

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

        playListRepository = PlayListRepository(playListDao)
        playListViewModelFactory = PlaylistViewModelFactory(playListRepository)

        val repository = MovieRepository()
        val movieViewModelFactory = MovieViewModelFactory(repository)
        movieViewModel = ViewModelProvider(this, movieViewModelFactory).get(MovieViewModel::class.java)

        val view = inflater.inflate(R.layout.fragment_home, container, false)

        val bigPoster = view.findViewById<ShapeableImageView>(R.id.bigPoster)
        val playBtn = view.findViewById<MaterialButton>(R.id.playBtn)
        val addToPlayList = view.findViewById<MaterialButton>(R.id.myListBtn)

        //View 1
        val recyclerView1: RecyclerView = view.findViewById(R.id.view1)
        recyclerView1.layoutManager = LinearLayoutManager(HomeFragment().context, LinearLayoutManager.HORIZONTAL, false)
        popularListAdapter = PopularListAdapter(emptyList())
        recyclerView1.adapter = popularListAdapter

        movieViewModel.getPopularMovies(1)

        movieViewModel.popularMovies.observe(viewLifecycleOwner, Observer { movies ->
            movies?.let {
                val lst = it.subList(1,11)

                val bigPosterMovie = it.get(0)
                popularListAdapter.setMovies(lst)

                val playListItem = PlayList(
                    bigPosterMovie.title,
                    bigPosterMovie.backdrop_path,
                    bigPosterMovie.id,
                    "movie"
                )

                playListViewModel.getPlayList(bigPosterMovie.id).observe(viewLifecycleOwner, Observer { playLists ->
                    if (playLists != null) isAdded = true
                    if (isAdded) {
                        addToPlayList.icon = resources.getDrawable(R.drawable.ic_check)
                    }
                })

                addToPlayList.setOnClickListener {
                    playListViewModel.viewModelScope.launch {
                        if (!isAdded) playListViewModel.insert(playListItem)
                        else {
                            playListViewModel.delete(playListItem)
                            isAdded = false
                            addToPlayList.icon = resources.getDrawable(R.drawable.add)
                        }
                    }
                }

                playBtn.setOnClickListener {
                    val intent = Intent(context, MoviePlayerActivity::class.java)
                    intent.putExtra("movie_id", bigPosterMovie.id)
                    intent.putExtra("media_type", "movie")
                    intent.putExtra("title", bigPosterMovie.title)
                    intent.putExtra("poster_path", bigPosterMovie.poster_path)
                    context?.startActivity(intent)
                    Navigation.createNavigateOnClickListener(R.id.action_homeFragment_to_moviePlayerActivity2)
                }

                bigPoster.load("https://media.themoviedb.org/t/p/w500/${bigPosterMovie.poster_path}")

                if (!it.isEmpty()) {
                    val shimmer1 =  view.findViewById<ShimmerFrameLayout>(R.id.shimmerFrameLayout1)

                    val fadeIn = AlphaAnimation(0f, 1f)
                    fadeIn.duration = 600 // Đặt thời gian fade
                    fadeIn.fillAfter = true
                    fadeIn.setAnimationListener(object : Animation.AnimationListener {
                        override fun onAnimationStart(animation: Animation) {}

                        override fun onAnimationEnd(animation: Animation) {
                            shimmer1.visibility = View.GONE
                        }

                        override fun onAnimationRepeat(animation: Animation) {}
                    })
                    recyclerView1.startAnimation(fadeIn)

                    val fadeIn2 = AlphaAnimation(0f, 1f)
                    fadeIn2.duration = 900
                    fadeIn2.fillAfter = true
                    fadeIn2.setAnimationListener(object : Animation.AnimationListener {
                        override fun onAnimationStart(animation: Animation) {}

                        override fun onAnimationEnd(animation: Animation) {
                        }

                        override fun onAnimationRepeat(animation: Animation) {}
                    })
                    bigPoster.startAnimation(fadeIn2)
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
                val lst = it.subList(0,10)
                topRatedListAdapter.setMovies(lst)

                if (!it.isEmpty()) {
                    val shimmer2 =  view.findViewById<ShimmerFrameLayout>(R.id.shimmerFrameLayout2)
                    shimmer2.visibility = View.GONE
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
                val lst = it.subList(0,10)
                nowPlayingListAdapter.setMovies(lst)
                if(it.isNotEmpty()) {
                    val shimmer3 = view.findViewById<ShimmerFrameLayout>(R.id.shimmerFrameLayout3)
                    shimmer3.visibility = View.GONE
                }
            }
        })

        //View 4
        val recyclerView4 : RecyclerView = view.findViewById(R.id.view4)
        recyclerView4.layoutManager = LinearLayoutManager(HomeFragment().context, LinearLayoutManager.HORIZONTAL, false)
        popularListTVAdapter = PopularListTVAdapter(emptyList())
        recyclerView4.adapter = popularListTVAdapter
        movieViewModel.getTVPopular(1)

        movieViewModel.tvPopular.observe(viewLifecycleOwner, Observer { tvs ->
            tvs?.let {
                val lst = it.subList(0,10)
                popularListTVAdapter.setMovies(lst)
                if(it.isNotEmpty()) {
                    val shimmer4 = view.findViewById<ShimmerFrameLayout>(R.id.shimmerFrameLayout4)
                    shimmer4.visibility = View.GONE
                }
            }
        })

        //View 5
        val recyclerView5: RecyclerView = view.findViewById(R.id.view5)
        recyclerView5.layoutManager = LinearLayoutManager(HomeFragment().context, LinearLayoutManager.HORIZONTAL, false)
        topRatedTVListAdapter = TopRatedTVListAdapter(emptyList())
        recyclerView5.adapter = topRatedTVListAdapter
        movieViewModel.getTVTopRated(1)

        movieViewModel.tvTopRated.observe(viewLifecycleOwner, Observer { tvs ->
            tvs?.let {
                val lst = it
                topRatedTVListAdapter.setMovies(lst)
                if(it.isNotEmpty()) {
                    val shimmer5 = view.findViewById<ShimmerFrameLayout>(R.id.shimmerFrameLayout5)
                    shimmer5.visibility = View.GONE
                }
        } })

        //View 6
        val recyclerView6: RecyclerView = view.findViewById(R.id.view6)
        recyclerView6.layoutManager = LinearLayoutManager(HomeFragment().context, LinearLayoutManager.HORIZONTAL, false)
        airingTodayTVListAdapter = AiringTodayTVListAdapter(emptyList())
        recyclerView6.adapter = airingTodayTVListAdapter
        movieViewModel.getTVAiringToday(1)

        movieViewModel.tvAiringToday.observe(viewLifecycleOwner, Observer { tvs ->
            tvs?.let {
                val lst = it
                airingTodayTVListAdapter.setMovies(lst)
                if(it.isNotEmpty()) {
                    val shimmer6 = view.findViewById<ShimmerFrameLayout>(R.id.shimmerFrameLayout6)
                    shimmer6.visibility = View.GONE
                }
            }
        })

        //Continue Watching
        continueWatchingRepository = ContinueWatchingRepository(watchHistoryDao)
        viewModelFactory = ContinueWatchingViewModelFactory(watchHistoryDao)

        val recyclerViewCW: RecyclerView = view.findViewById(R.id.viewCW)
        recyclerViewCW.layoutManager  = LinearLayoutManager(HomeFragment().context, LinearLayoutManager.HORIZONTAL, false)
        continueWatchingListAdapyer = ContinueWatchingListAdapter(emptyList(), viewModel)
        recyclerViewCW.adapter = continueWatchingListAdapyer

        val viewStateObserver = Observer<List<ContinueWatching>> { watchFrom ->
            val textCW = view.findViewById<TextView>(R.id.textViewCW)
            if (watchFrom.isNotEmpty()) {
                recyclerViewCW.visibility = View.VISIBLE
                textCW.visibility = View.VISIBLE
            }
            else {
                textCW.visibility = View.GONE
                recyclerViewCW.visibility = View.GONE
            }
            continueWatchingListAdapyer.setMovies(watchFrom)
        }
        viewModel.allWatchHistory.observe(viewLifecycleOwner,viewStateObserver)

        //netflix
        val netflixBtn : ImageView = view.findViewById(R.id.netflix)
        netflixBtn.setOnClickListener {
            val intent = Intent(context, MovieActivity::class.java)
            intent.putExtra("media_type", "tv")
            intent.putExtra("title", "Netflix")
            intent.putExtra("network", 213)
            startActivity(intent)
        }
        // Inflate the layout for this fragment
        return view
    }
}
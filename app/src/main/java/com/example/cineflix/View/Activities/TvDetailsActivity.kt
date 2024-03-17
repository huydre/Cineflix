package com.example.cineflix.View.Activities

import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import coil.load
import com.example.cineflix.Adapters.ContinueWatchingListAdapter
import com.example.cineflix.Adapters.TabLayoutTvDetailsAdapter
import com.example.cineflix.Model.local.watching.ContinueWatching
import com.example.cineflix.Model.local.watching.ContinueWatchingDatabase
import com.example.cineflix.Model.local.watching.ContinueWatchingRepository
import com.example.cineflix.MovieRepository
import com.example.cineflix.R
import com.example.cineflix.View.Fragments.HomeFragment
import com.example.cineflix.ViewModel.ContinueWatchingViewModel
import com.example.cineflix.ViewModel.ContinueWatchingViewModelFactory
import com.example.cineflix.ViewModel.MovieViewModel
import com.example.cineflix.ViewModel.MovieViewModelFactory
import com.google.android.material.button.MaterialButton
import com.google.android.material.tabs.TabLayout
import androidx.activity.viewModels
import com.example.cineflix.Model.local.watching.ContinueWatchingDAO
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

private var tvID : String = ""
private var movieTitle : String? = ""
private var numberOfSeason : Int = 0
private lateinit var posterPath: String

class TvDetailsActivity : AppCompatActivity() {

    private lateinit var movieViewModel: MovieViewModel
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager2: ViewPager2
    private lateinit var tabLayoutAdapter: TabLayoutTvDetailsAdapter
    private var playbackPosition: Long = 0
    private var isWatching: Boolean = false
    private var season: Int = 1
    private var episode: Int = 1

    private lateinit var continueWatchingRepository: ContinueWatchingRepository
    private val database by lazy { ContinueWatchingDatabase.getInstance(this) }
    private val watchHistoryDao by lazy { database.watchDAO() }
    private lateinit var viewModelFactory: ContinueWatchingViewModelFactory
    private val viewModel: ContinueWatchingViewModel by viewModels(
        factoryProducer = {
            viewModelFactory
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {

        val repository = MovieRepository()
        val movieViewModelFactory = MovieViewModelFactory(repository)
        movieViewModel = ViewModelProvider(this, movieViewModelFactory).get(MovieViewModel::class.java)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tv_details)
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        val TVId = intent.getIntExtra("movie_id",0)
        tvID = TVId.toString()
        movieTitle = intent.getStringExtra("tv_title")
        val movieYear = intent.getStringExtra("tv_year")
        val movieOverview = intent.getStringExtra("tv_overview")
        val tvBackdropPath = intent.getStringExtra("tv_backdrop")
        posterPath = intent.getStringExtra("poster_path").toString()

        val title = findViewById<TextView>(R.id.tvTitle)
        val year = findViewById<TextView>(R.id.tvYear)
        val overview = findViewById<TextView>(R.id.tvOverview)
        val actor = findViewById<TextView>(R.id.tvActor)
        val tvSeasonCount = findViewById<TextView>(R.id.tvSeasonCount)
        val TvBackdrop = findViewById<ImageView>(R.id.TVbackdrop)
        val playBtn = findViewById<MaterialButton>(R.id.playBtnTV)
        val tvRate = findViewById<TextView>(R.id.tvRate)
        val starIcon = findViewById<ImageView>(R.id.star_icon)
        val backBtn = findViewById<MaterialButton>(R.id.backBtn)

        title.text = movieTitle
        year.text = movieYear.toString().substring(0,4)
        overview.text = movieOverview
        TvBackdrop.load("https://media.themoviedb.org/t/p/w780/${tvBackdropPath}")

        backBtn.setOnClickListener {
            finish()
        }



        //Get TV Details
        movieViewModel.getTVDetails(TVId.toString())
        movieViewModel.tvDetails.observe(this@TvDetailsActivity) {tvs ->
            tvs?.let {
                tvSeasonCount.text = "${tvs.number_of_seasons.toString()} Mùa"
                numberOfSeason = tvs.number_of_seasons
                val rated = it.vote_average
                tvRate.text = rated.toString().substring(0,3)
                if (rated < 7 && rated >= 4) {
                    starIcon.setColorFilter(Color.parseColor("#D2D531"))
                    tvRate.setTextColor(Color.parseColor("#D2D531"))
                }
                else if (rated < 4 ) {
                    starIcon.setColorFilter(Color.parseColor("#DB2360"))
                    tvRate.setTextColor(Color.parseColor("#DB2360"))
                }
//                title.text = it.name
//                year.text = it.first_air_date.substring(0,4)
//                overview.text = it.overview
//                TvBackdrop.load("https://media.themoviedb.org/t/p/w780/${it.backdrop_path}")
            }
        }


        //Get TV credits
        movieViewModel.getTvCredits(TVId.toString())
        movieViewModel.tvCredit.observe(this@TvDetailsActivity) { credit ->
            credit?.let {
                val credits = credit.cast.sortedByDescending { it.popularity }.subList(0,5)
                val names = credits.map { it.name }
                actor.text = "Diễn viên: " + names.joinToString(separator = ", ") + "..."
            }
        }

        //Tab layout
        tabLayout = findViewById(R.id.tab_layout)
        viewPager2 = findViewById(R.id.view_pager2)

        tabLayoutAdapter = TabLayoutTvDetailsAdapter(supportFragmentManager, lifecycle)

        tabLayout.addTab(tabLayout.newTab().setText("Danh sách tập"))
        tabLayout.addTab(tabLayout.newTab().setText("Nội dung tương tự"))

        viewPager2.adapter = tabLayoutAdapter

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                if (tab != null) {
                    viewPager2.currentItem = tab.position
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {

            }

            override fun onTabReselected(tab: TabLayout.Tab?) {

            }

        })

        viewPager2.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                tabLayout.selectTab(tabLayout.getTabAt(position))


            }
        })



        playBtn.setOnClickListener {
            val intent = Intent(this, MoviePlayerActivity::class.java)
            if (isWatching) {
                intent.putExtra("movie_id", TVId)
                intent.putExtra("media_type", "tv")
                intent.putExtra("title", movieTitle)
                intent.putExtra("season", season)
                intent.putExtra("episode", episode)
                intent.putExtra("poster_path", posterPath)
                intent.putExtra("progress", playbackPosition)
            }
            else {
                intent.putExtra("movie_id", TVId)
                intent.putExtra("media_type", "tv")
                intent.putExtra("title", movieTitle)
                intent.putExtra("season", 1)
                intent.putExtra("episode", 1)
                intent.putExtra("poster_path", posterPath)
            }
            startActivity(intent)
        }

        //Continue Watching
        continueWatchingRepository = ContinueWatchingRepository(watchHistoryDao)
        viewModelFactory = ContinueWatchingViewModelFactory(watchHistoryDao)
        viewModel.getProgressTest(TVId).observe(this, Observer { watchFrom ->
            watchFrom?.let {
                playbackPosition = it.progress
                season = it.season
                episode = it.episode
                isWatching = true
                if (isWatching) {
                    playBtn.setText("Tiếp tục xem M${season}:T${episode}")
                }
            }
        })
    }
}

fun getTvId(): String {
    return tvID
}

fun getTvTitle(): String? {
    return movieTitle
}

fun getTVSeasonCount(): Int {
    return numberOfSeason
}

fun getTVPosterPath(): String {
    return posterPath
}
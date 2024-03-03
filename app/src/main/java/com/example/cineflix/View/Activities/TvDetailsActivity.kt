package com.example.cineflix.View.Activities

import android.content.ContentValues.TAG
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.widget.ViewPager2
import com.example.cineflix.Adapters.SimilarListAdapter
import com.example.cineflix.Adapters.TabLayoutTvDetailsAdapter
import com.example.cineflix.MovieRepository
import com.example.cineflix.R
import com.example.cineflix.ViewModel.MovieViewModel
import com.example.cineflix.ViewModel.MovieViewModelFactory
import com.google.android.material.tabs.TabLayout
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.views.YouTubePlayerView

class TvDetailsActivity : AppCompatActivity() {

    private lateinit var movieViewModel: MovieViewModel
    private lateinit var tabLayout: TabLayout
    private lateinit var viewPager2: ViewPager2
    private lateinit var tabLayoutAdapter: TabLayoutTvDetailsAdapter

    override fun onCreate(savedInstanceState: Bundle?) {

        val repository = MovieRepository()
        val movieViewModelFactory = MovieViewModelFactory(repository)
        movieViewModel = ViewModelProvider(this, movieViewModelFactory).get(MovieViewModel::class.java)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_tv_details)
        window.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS)

        val TVId = intent.getIntExtra("tv_id",0)
        val movieTitle = intent.getStringExtra("tv_title")
        val movieYear = intent.getStringExtra("tv_year")
        val movieOverview = intent.getStringExtra("tv_overview")


        val title = findViewById<TextView>(R.id.tvTitle)
        val year = findViewById<TextView>(R.id.tvYear)
        val overview = findViewById<TextView>(R.id.tvOverview)
        val actor = findViewById<TextView>(R.id.tvActor)

        title.text = movieTitle
        year.text = movieYear.toString().substring(0,4)
        overview.text = movieOverview

        movieViewModel.getTvVideos(TVId.toString())

        val youTubePlayerView = findViewById<YouTubePlayerView>(R.id.youtube_player_view2)
        lifecycle.addObserver(youTubePlayerView)

        youTubePlayerView.addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
            override fun onReady(youTubePlayer: YouTubePlayer) {
                movieViewModel.tvVideos.observe(this@TvDetailsActivity) { videos ->
                    videos?.let {
                        Log.d(TAG, "onReady: " + it)
                        val videoId = it.results.firstOrNull()?.key ?: ""
                        youTubePlayer.loadVideo(videoId, 0f)
                    }
                }
            }
        })

        //Get TV credits
        movieViewModel.getTvCredits(TVId.toString())

        movieViewModel.tvCredit.observe(this@TvDetailsActivity) { credit ->
            credit?.let {
                val credits = credit.cast.sortedByDescending { it.popularity }.subList(0,5)
                val names = credits.map { it.name }
//                val actorList = names.joinToString {  }
                actor.text = "Diễn viên: " + names.joinToString(separator = ", ") + "..."
            }
        }

        //Tab layout
        tabLayout = findViewById(R.id.tab_layout)
        viewPager2 = findViewById(R.id.view_pager2)

        tabLayoutAdapter = TabLayoutTvDetailsAdapter(supportFragmentManager, lifecycle)

        tabLayout.addTab(tabLayout.newTab().setText("Danh sách tập phim"))
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
    }
}
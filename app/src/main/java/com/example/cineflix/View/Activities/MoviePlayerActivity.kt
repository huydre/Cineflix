package com.example.cineflix.View.Activities

import android.content.ContentValues.TAG
import android.content.pm.ActivityInfo
import android.net.Uri
import android.os.Bundle
import android.os.PowerManager
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.cineflix.OPhimRepository
import com.example.cineflix.R
import com.example.cineflix.Utils.OPhim
import com.example.cineflix.ViewModel.OPhimViewModel
import com.example.cineflix.ViewModel.OPhimViewModelFactory
import com.google.android.exoplayer2.*
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory
import com.google.android.exoplayer2.extractor.ts.DefaultTsPayloadReaderFactory
import com.google.android.exoplayer2.source.MediaSource
import com.google.android.exoplayer2.source.ProgressiveMediaSource
import com.google.android.exoplayer2.source.dash.DashMediaSource
import com.google.android.exoplayer2.source.dash.DefaultDashChunkSource
import com.google.android.exoplayer2.source.hls.HlsMediaSource
import com.google.android.exoplayer2.ui.AspectRatioFrameLayout
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MoviePlayerActivity : AppCompatActivity(), Player.Listener{

    private lateinit var simpleExoplayer: SimpleExoPlayer
    private var playbackPosition: Long = 0
    private lateinit var videoUrl: String
    private lateinit var buffer: ProgressBar
    private lateinit var play : ImageButton
    private lateinit var pause : ImageButton
    private lateinit var goBack: ImageButton
    private lateinit var title: TextView
    private lateinit var screenScale: LinearLayout
    private lateinit var lockLL : LinearLayout
    private var fit = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_player)

        val movieId = intent.getStringExtra("movie_id")
        val movieTitle = intent.getStringExtra("movie_title").toString()

        //ExoPlayer
        videoUrl = intent.getStringExtra("video_url").toString()
        fullscreen()
        buffer = findViewById(R.id.buffering)
        pause = findViewById(R.id.exo_pause)
        play = findViewById(R.id.exo_pause)
        goBack = findViewById(R.id.videoView_go_back)
        title = findViewById(R.id.videoView_title)
        screenScale = findViewById(R.id.videoView_screen_size)
        val playerView = findViewById<PlayerView>(R.id.video_view)
        val screenResizeTv : TextView = findViewById(R.id.screen_resize_text)
        val screenResizeIv : ImageView = findViewById(R.id.screen_resize_img)

        title.text = movieTitle

        goBack.setOnClickListener {
            finish()
        }

        screenScale.setOnClickListener{
            when(fit) {
                1 -> {
                    playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FILL
                    screenResizeTv.text = "Fill"
                    screenResizeIv.setImageResource(R.drawable.fill_screen)
                    fit = 2
                }

                2 -> {
                    playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_FIT
                    screenResizeTv.text = "Fit"
                    screenResizeIv.setImageResource(R.drawable.fit_screen)
                    fit = 3
                }

                else -> {
                    playerView.resizeMode = AspectRatioFrameLayout.RESIZE_MODE_ZOOM
                    screenResizeTv.text = "Zoom"
                    screenResizeIv.setImageResource(R.drawable.baseline_zoom_out_map_24)
                    fit = 1
                }
            }
        }

    }

    override fun onStart() {
        super.onStart()
        initializePlayer()
        Log.d(TAG, "onStart: started")
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
        Log.d(TAG, "onStop: stopped")
    }
    override fun onPause() {
        super.onPause()
        releasePlayer()
    }

    private fun initializePlayer() {
        simpleExoplayer = SimpleExoPlayer.Builder(this).build()
        preparePlayer(videoUrl)
    }

    private fun preparePlayer(videoUrl: String) {
        val uri = Uri.parse(videoUrl)
        val mediaSource = buildMediaSource(uri)
        simpleExoplayer.setMediaSource(mediaSource, false)
        simpleExoplayer.playWhenReady = true
        simpleExoplayer.addListener(this)
        val playerViewFullscreen = findViewById<PlayerView>(R.id.video_view)
        playerViewFullscreen.player = simpleExoplayer
        simpleExoplayer.seekTo(playbackPosition)
        simpleExoplayer.prepare()
    }

    private fun buildMediaSource(uri: Uri): MediaSource {
        val lastPathSegment = uri.lastPathSegment
        return if (lastPathSegment?.contains("mp4") == true) {
            ProgressiveMediaSource.Factory(DefaultDataSourceFactory(this, "channel-video-player"))
                .createMediaSource(MediaItem.fromUri(uri))
        }
        else if (lastPathSegment?.contains("m3u8") == true) {
            HlsMediaSource.Factory(DefaultHttpDataSource.Factory())
                .createMediaSource(MediaItem.fromUri(uri))
        } else if (lastPathSegment?.contains("ts") == true) {
            val defaultExtractorsFactory = DefaultExtractorsFactory()
            defaultExtractorsFactory.setTsExtractorFlags(DefaultTsPayloadReaderFactory.FLAG_DETECT_ACCESS_UNITS)
            return ProgressiveMediaSource.Factory(
                DefaultDataSourceFactory(this, "channel-video-player"),
                defaultExtractorsFactory).createMediaSource(MediaItem.fromUri(uri))
        } else {
            val dashChunkSourceFactory = DefaultDashChunkSource.Factory(
                DefaultHttpDataSource.Factory()
            )
            val manifestDataSourceFactory = DefaultHttpDataSource.Factory()
            DashMediaSource.Factory(dashChunkSourceFactory, manifestDataSourceFactory)
                .createMediaSource(MediaItem.fromUri(uri))
        }
    }

    private fun releasePlayer() {
        playbackPosition = simpleExoplayer.currentPosition
        simpleExoplayer.release()
    }

    override fun onPlayerError(error: PlaybackException) {
        // handle error
        Log.e("VideoPlayerActivity", "error: $error")
    }

    override fun onDestroy() {
        super.onDestroy()
        releasePlayer()
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        if (playbackState == Player.STATE_BUFFERING) {
            buffer.visibility = View.VISIBLE
            play.visibility = View.GONE
            pause.visibility = View.GONE
        }
        else {
            play.visibility = View.VISIBLE
            pause.visibility = View.VISIBLE
            buffer.visibility = View.GONE
        }
    }

    private fun fullscreen() {
        // Ẩn ActionBar
        supportActionBar?.hide()
        // Đặt activity thành Fullscreen
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
    }
}
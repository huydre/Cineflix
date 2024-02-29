package com.example.cineflix.View.Activities

import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.content.Context
import android.content.pm.ActivityInfo
import android.content.res.Resources
import android.media.AudioManager
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.provider.Settings
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.SeekBar
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
import com.google.android.exoplayer2.ui.DefaultTimeBar
import com.google.android.exoplayer2.ui.PlayerView
import com.google.android.exoplayer2.ui.TimeBar
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory
import com.google.android.exoplayer2.upstream.DefaultHttpDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.abs


class MoviePlayerActivity : AppCompatActivity(), Player.Listener, GestureDetector.OnGestureListener, AudioManager.OnAudioFocusChangeListener{

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
    private lateinit var brightnessLL: LinearLayout
    private var fit = 1
    private var minSwipeY: Float = 0f
    private var brightness: Int = 0
    private lateinit var brightnessSeek : SeekBar
    private lateinit var volumeLL: LinearLayout
    private lateinit var volumeSeek : SeekBar
    private var volume : Int = 0
    private var audioManager: AudioManager? = null
    private var maxVolume: Int = 0
    private lateinit var gestureDetector: GestureDetector
    private var isLocked = false
    private lateinit var brightnessImage: ImageView
    private lateinit var unlockIv : LinearLayout


    @SuppressLint("ClickableViewAccessibility")
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
        lockLL  = findViewById(R.id.videoView_lock_screen)
        unlockIv = findViewById(R.id.videoView_unlock_screen)

        gestureDetector = GestureDetector(this, this)
        findViewById<View>(android.R.id.content).setOnTouchListener { _, event -> gestureDetector.onTouchEvent(event) }

        brightnessImage = findViewById(R.id.videoView_brightness_image)
        brightnessLL = findViewById(R.id.videoView_two_layout)
        brightnessSeek = findViewById(R.id.videoView_brightness)
//        val currbrightness = Settings.System.getInt(contentResolver, Settings.System.SCREEN_BRIGHTNESS)*3
        brightnessSeek.max = 30

//        brightness = currbrightness / 10

        if(audioManager == null) audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
        maxVolume = audioManager!!.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
        volumeLL = findViewById(R.id.volume_ll)
        volumeSeek = findViewById(R.id.volume_seek)
        volumeSeek.max = maxVolume
        volume = maxVolume/2

        title.text = movieTitle

        goBack.setOnClickListener {
            finish()
        }

        lockLL.setOnClickListener{
            isLocked = true
            playerView.useController = false
        }

        unlockIv.setOnClickListener {
            isLocked = false
            playerView.useController = true
            playerView.showController()
            unlockIv.visibility = View.GONE
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

        playerView.setOnTouchListener { _, motionEvent ->
            if (!isLocked) {
                gestureDetector.onTouchEvent(motionEvent)
                if (motionEvent.action == MotionEvent.ACTION_UP) {
                    brightnessLL.visibility = View.GONE
                    volumeLL.visibility = View.GONE
                }
            }
            else {
                if (unlockIv.visibility == View.VISIBLE) {
                    unlockIv.visibility = View.GONE
                } else {
                    unlockIv.visibility = View.VISIBLE
                    Handler(Looper.getMainLooper()).postDelayed({
                        unlockIv.visibility = View.GONE
                    }, 5000)
                }
            }
            return@setOnTouchListener false
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
        seekBarFeature()
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
            play.setImageResource(0)
            pause.setImageResource(0)
        }
        else {
            play.setImageResource(R.drawable.netlfix_play_button)
            pause.setImageResource(R.drawable.netflix_pause_button)
            buffer.visibility = View.GONE
        }
        super.onPlaybackStateChanged(playbackState)
    }

    private fun fullscreen() {
//        supportActionBar?.hide()
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        window.decorView.apply {
            systemUiVisibility = (
                    View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                            View.SYSTEM_UI_FLAG_IMMERSIVE or
                            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                            View.SYSTEM_UI_FLAG_FULLSCREEN
                    )
        }
    }

    override fun onScroll(event: MotionEvent?, event1: MotionEvent, dX: Float, dY: Float): Boolean {
        minSwipeY += dY
        val sWidth = Resources.getSystem().displayMetrics.widthPixels

        if(abs(dX)< abs(dY) && abs(minSwipeY) > 50){
            if(event!!.x < sWidth/2){
                //Vuốt tăng giảm độ sáng
                brightnessLL.visibility = View.VISIBLE
                volumeLL.visibility = View.GONE
                val increase = dY > 0
                val newValue = if(increase) brightness + 1 else brightness - 1

                if(newValue in 0..30) brightness = newValue
                brightnessSeek.progress = brightness
                setBrightnessImage(brightnessSeek.progress)
                setScreenBrightness(brightness)
            }
            else{
                //Vuốt tăng giảm âm lượng
                brightnessLL.visibility = View.GONE
                volumeLL.visibility = View.VISIBLE

                val increase = dY > 0
                val newValue = if(increase) volume + 5 else volume - 5
                if(newValue in 0..maxVolume) volume = newValue
                volumeSeek.progress = volume
                audioManager!!.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0)
            }
            minSwipeY = 0f
        }
        return true
    }

    private fun setScreenBrightness(value: Int){
        val d = 1.0f/30
        val lp = this.window.attributes
        lp.screenBrightness = d * value
        this.window.attributes = lp
        setBrightnessImage(brightnessSeek.progress)
    }

    private fun setBrightnessImage(value: Int) {
        if (value == 0)  brightnessImage.setImageResource(R.drawable.netflix_brightness_one)
        if (value == 10)  brightnessImage.setImageResource(R.drawable.netflix_brightness_two)
        if (value == 20)  brightnessImage.setImageResource(R.drawable.netflix_brightness_three)
        if (value == 30)  brightnessImage.setImageResource(R.drawable.netflix_brightness_four)
    }

    private fun seekBarFeature() {
        findViewById<DefaultTimeBar>(R.id.exo_progress).addListener(object : TimeBar.OnScrubListener{
            override fun onScrubStart(timeBar: TimeBar, position: Long) {
                simpleExoplayer.pause()
            }

            override fun onScrubMove(timeBar: TimeBar, position: Long) {
                simpleExoplayer.seekTo(position)
            }

            override fun onScrubStop(timeBar: TimeBar, position: Long, canceled: Boolean) {
                simpleExoplayer.play()
            }

        })
    }

    private fun playerPause(){
        simpleExoplayer.pause()
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return gestureDetector.onTouchEvent(event)
    }

    override fun onSingleTapUp(p0: MotionEvent): Boolean = false

    override fun onDown(p0: MotionEvent): Boolean = false

    override fun onShowPress(p0: MotionEvent) = Unit

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        super.onBackPressed()
//        if(subSelectBg.visibility != View.VISIBLE && !qualitySelectBg.isVisible && !openSubll.isVisible)
//            super.onBackPressed()
//        else if (openSubll.isVisible){
//            openSubll.visibility = View.GONE
//            subSelectBg.visibility = View.VISIBLE
//        }
//        else{
//            subSelectBg.visibility = View.GONE
//            qualitySelectBg.visibility = View.GONE
//            mainPlayer.visibility = View.VISIBLE
//            playerPlay()
//        }
    }

    override fun onLongPress(p0: MotionEvent) = Unit

    override fun onFling(p0: MotionEvent?, p1: MotionEvent, p2: Float, p3: Float): Boolean  = false

    override fun onAudioFocusChange(focusChange: Int) {
        if(focusChange <= 0) playerPause()
    }
}
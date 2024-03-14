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
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatTextView
import androidx.core.view.GestureDetectorCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.cineflix.OPhimRepository
import com.example.cineflix.R
import com.example.cineflix.Utils.OPhim
import com.example.cineflix.ViewModel.OPhimViewModel
import com.example.cineflix.ViewModel.OPhimViewModelFactory
import com.github.vkay94.dtpv.youtube.YouTubeOverlay
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
import com.google.android.material.button.MaterialButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.math.abs


class MoviePlayerActivity : AppCompatActivity(), Player.Listener, GestureDetector.OnGestureListener, AudioManager.OnAudioFocusChangeListener{

    private val _videoUrl = MutableLiveData<String>()
    val videoUrl: LiveData<String> = _videoUrl

    private val _numberOfEpisode = MutableLiveData<Int>()
    val numberOfEpisode: LiveData<Int> = _numberOfEpisode

    private lateinit var simpleExoplayer: SimpleExoPlayer
    private var playbackPosition: Long = 0
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
    private var seasonCount: Int = 0
    private lateinit var gestureDetector: GestureDetector
    private var isLocked = false
    private lateinit var brightnessImage: ImageView
    private lateinit var unlockIv : LinearLayout
    private lateinit var sourceAndSubtile: LinearLayout

    private lateinit var oPhimViewModel: OPhimViewModel



    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_player)

        val movieId = intent.getStringExtra("movie_id")
        val mediaType = intent.getStringExtra("media_type")
        val movieTitle = intent.getStringExtra("title").toString()
        var season = intent.getIntExtra("season", 0)
        var episode = intent.getIntExtra("episode", 0)

        getOphimVideoUrl(movieTitle, mediaType.toString(), season, episode)

        //ExoPlayer
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
        sourceAndSubtile = findViewById(R.id.sourceAndSubtitleLL)
        val videoViewTrackBtn = findViewById<LinearLayout>(R.id.videoView_track)
        val trackCancleBtn = findViewById<MaterialButton>(R.id.cancle_btn)
        val nextEPBtn = findViewById<LinearLayout>(R.id.videoView_next_ep)

        videoViewTrackBtn.setOnClickListener{
            sourceAndSubtile.visibility = View.VISIBLE
        }

        trackCancleBtn.setOnClickListener {
            sourceAndSubtile.visibility = View.GONE
        }

        nextEPBtn.setOnClickListener {
            // Chuyển sang tập tiếp theo
            val currentEpisodeUrl = videoUrl.value
            episode += 1
            getOphimVideoUrl(movieTitle, mediaType.toString(), season, episode)
            // Nếu tập tiếp theo không có thì chuyển qua season mới
            if (currentEpisodeUrl == videoUrl.value && season < seasonCount) {
                season += 1
                episode = 1
                getOphimVideoUrl(movieTitle, mediaType.toString(), season, episode)
            }
            else {
                nextEPBtn.visibility = View.GONE
            }
            title.text = "${movieTitle} Phần ${season} Tập ${episode}"
        }

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

        if (mediaType == "movie") {
            title.text = movieTitle
        }
        else {
            seasonCount = getTVSeasonCount()
            nextEPBtn.visibility = View.VISIBLE
            title.text = "${movieTitle} Phần ${season} Tập ${episode}"
        }

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

        val gestureDetectorDouble = GestureDetectorCompat(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDoubleTap(e: MotionEvent): Boolean {

                if (e.x > playerView.width / 2) {
                    // Double tapped on the right side - forward seek
                    simpleExoplayer.seekTo(simpleExoplayer.currentPosition + 10000)
//                        seekBar.progress = player.currentPosition.toInt()
                } else {
                    // Double tapped on the left side - backward seek
                    simpleExoplayer.seekTo(simpleExoplayer.currentPosition - 10000)
//                        seekBar.progress = player.currentPosition.toInt()
                }
                playerView.useController = true
                return super.onDoubleTap(e)
            }
        })

        playerView.setOnTouchListener { _, motionEvent ->
            gestureDetectorDouble.onTouchEvent(motionEvent)
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
    }

    override fun onStop() {
        super.onStop()
        releasePlayer()
    }
    override fun onPause() {
        super.onPause()
        releasePlayer()
    }

    private fun initializePlayer() {
        simpleExoplayer = SimpleExoPlayer.Builder(this).build()
//        preparePlayer(videoUrl.value.toString())
        videoUrl.observe(this) {url ->
            if  (!url.isNullOrEmpty()) {
                preparePlayer(url)
            }

        }
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
    }

    override fun onLongPress(p0: MotionEvent) = Unit

    override fun onFling(p0: MotionEvent?, p1: MotionEvent, p2: Float, p3: Float): Boolean  = false

    override fun onAudioFocusChange(focusChange: Int) {
        if(focusChange <= 0) playerPause()
    }

    fun getOphimVideoUrl(title: String, mediaType: String, s: Int, e: Int) {
        var slug = ConvertNameToSlug(title)
        if (mediaType == "tv") {
            slug += "-phan-${s}"
        }
        val repositorys = OPhimRepository()
        val oPhimViewModelFactory = OPhimViewModelFactory(repositorys)
        oPhimViewModel = ViewModelProvider(this, oPhimViewModelFactory).get(OPhimViewModel::class.java)
        oPhimViewModel.getOPhimDetails(slug)
        oPhimViewModel.oPhimDetails.observe(this@MoviePlayerActivity) { details ->
            details?.let {
                if (mediaType == "movie") {
                    _videoUrl.value = it.get(0).episodes.get(0).server_data.get(0).link_m3u8
                }
                else {
                    _numberOfEpisode.value = it.get(0).episodes.get(0).server_data.size
                    if (numberOfEpisode.value!! >= e ) {
                        _videoUrl.value = it.get(0).episodes.get(0).server_data.get(e-1).link_m3u8
                    }
                }
            }
        }
        Log.d(TAG, "getOphimVideoUrl: " + slug)
    }

    private fun ConvertNameToSlug(name: String) : String {
        val regex = Regex("[^a-zA-Z0-9\\s]")
        val slug = convert(name).replace(regex, "")
            .toLowerCase()
            .replace("\\s+".toRegex(), "-")
        return slug
    }

    fun convert(str: String): String {
        var str = str
        str = str.replace("à|á|ạ|ả|ã|â|ầ|ấ|ậ|ẩ|ẫ|ă|ằ|ắ|ặ|ẳ|ẵ".toRegex(), "a")
        str = str.replace("è|é|ẹ|ẻ|ẽ|ê|ề|ế|ệ|ể|ễ".toRegex(), "e")
        str = str.replace("ì|í|ị|ỉ|ĩ".toRegex(), "i")
        str = str.replace("ò|ó|ọ|ỏ|õ|ô|ồ|ố|ộ|ổ|ỗ|ơ|ờ|ớ|ợ|ở|ỡ".toRegex(), "o")
        str = str.replace("ù|ú|ụ|ủ|ũ|ư|ừ|ứ|ự|ử|ữ".toRegex(), "u")
        str = str.replace("ỳ|ý|ỵ|ỷ|ỹ".toRegex(), "y")
        str = str.replace("đ".toRegex(), "d")
        str = str.replace("À|Á|Ạ|Ả|Ã|Â|Ầ|Ấ|Ậ|Ẩ|Ẫ|Ă|Ằ|Ắ|Ặ|Ẳ|Ẵ".toRegex(), "A")
        str = str.replace("È|É|Ẹ|Ẻ|Ẽ|Ê|Ề|Ế|Ệ|Ể|Ễ".toRegex(), "E")
        str = str.replace("Ì|Í|Ị|Ỉ|Ĩ".toRegex(), "I")
        str = str.replace("Ò|Ó|Ọ|Ỏ|Õ|Ô|Ồ|Ố|Ộ|Ổ|Ỗ|Ơ|Ờ|Ớ|Ợ|Ở|Ỡ".toRegex(), "O")
        str = str.replace("Ù|Ú|Ụ|Ủ|Ũ|Ư|Ừ|Ứ|Ự|Ử|Ữ".toRegex(), "U")
        str = str.replace("Ỳ|Ý|Ỵ|Ỷ|Ỹ".toRegex(), "Y")
        str = str.replace("Đ".toRegex(), "D")
        return str
    }
}


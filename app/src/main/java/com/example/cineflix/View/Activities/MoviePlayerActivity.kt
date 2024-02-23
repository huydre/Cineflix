package com.example.cineflix.View.Activities

import android.content.ContentValues.TAG
import android.content.pm.ActivityInfo
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import android.webkit.WebView
import android.webkit.WebViewClient
import com.example.cineflix.R

class MoviePlayerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_movie_player)

        // Ẩn ActionBar
        supportActionBar?.hide()

        // Đặt activity thành fullscreen
        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE

        val movieId = intent.getStringExtra("movie_id")
        Log.d(TAG, "onCreate: " + movieId)

        val webview = findViewById<WebView>(R.id.web_view)
        webview.settings.javaScriptEnabled = true
        webview.webViewClient = WebViewClient()
        webview.loadUrl("https://multiembed.mov/?video_id=${movieId}&tmdb=1")
    }
}
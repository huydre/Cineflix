package com.example.cineflix.Utils

import android.util.Log
import com.lagradost.nicehttp.Requests
import com.lagradost.nicehttp.TAG
import kotlin.math.log

class OPhim {
    val Url = "https://ophim1.com/phim/"
    private val app = Requests()

    suspend fun getLinks(isMovie: Boolean, slug: String, s: Int, e: Int ) : String? {
        var videoUrl : String? = null
        val getUrl = Url + slug
        try {
            val response = app.get(getUrl)

            Log.d(TAG, "getLinks: " + response)

            return videoUrl
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

}
package com.example.cineflix.Utils

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import com.lagradost.nicehttp.Requests
import com.lagradost.nicehttp.TAG
import java.util.Base64


class SuperEmbed() {

    private fun getHashBasedOnIndex(hash: String, index: String): String {
        var result = ""
        for (i in hash.indices step 2) {
            val j = hash.substring(i, i + 2)
            result += (j.toInt(16) xor index[(i / 2) % index.length].code).toChar()
        }
        return result
    }

    private val app = Requests()
    private val gson = Gson()
    private val proxy = ""
    suspend fun getLink(isMovie: Boolean, id: String, s: Int, e: Int, src:String = "en") : Pair<String?,String?> {
        var videoUrl : String? = null
        val getUrl = if(isMovie) "https://multiembed.mov/?video_id=$id&tmdb=1" else "https://multiembed.mov/?video_id=$id&tmdb=1&s=$s&e=$e"
        try {
            val absoluteUrl = app.get(getUrl)

            Log.d(TAG, "getLinks: " + absoluteUrl)

            return Pair(null,null)
        } catch (e: Exception) {
            e.printStackTrace()
            return Pair(null,null)
        }
    }
}

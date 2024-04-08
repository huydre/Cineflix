package com.example.cineflix.Utils

import android.util.Log
import com.google.gson.Gson
import com.lagradost.nicehttp.Requests
import com.lagradost.nicehttp.TAG
import com.lagradost.nicehttp.addGenericDns
import com.lagradost.nicehttp.ignoreAllSSLErrors
import okhttp3.Cache
import okhttp3.OkHttpClient
import java.io.File

class SuperEmbed {
    private val app = Requests(baseClient = getBaseClient())
    private val gson = Gson()
    suspend fun getLink(isMovie: Boolean, id : String, s: Int, e: Int, src: String = "en" ) {
        val url = if(!isMovie) " https://multiembed.mov/?video_id=$id&tmdb=1&season=$s&episode=$e" else " https://multiembed.mov/?video_id=$id&tmdb=1"
        try {
            val response = app.get(url).toString()
            Log.d(TAG, "getLink: " + response)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}

fun getBaseClient(): OkHttpClient {
    fun OkHttpClient.Builder.addCloudFlareDns() = (
            addGenericDns(
                "https://cloudflare-dns.com/dns-query",
                // https://www.cloudflare.com/ips/
                listOf(
                    "1.1.1.1",
                    "1.0.0.1",
                    "2606:4700:4700::1111",
                    "2606:4700:4700::1001"
                )
            ))

    val appCache = Cache(File("cacheDir", "okhttpcache"), 10 * 1024 * 1024)

    val baseClient = OkHttpClient.Builder()
        .followRedirects(true)
        .followSslRedirects(true)
        .ignoreAllSSLErrors()
        .cache(
            appCache
        )
        .apply {
            addCloudFlareDns()
        }.build()
    return baseClient
}
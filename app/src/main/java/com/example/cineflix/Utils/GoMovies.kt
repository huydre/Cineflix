package com.example.cineflix.Utils

import android.util.Base64
import com.google.gson.Gson
import com.lagradost.nicehttp.Requests
import org.json.JSONArray
import org.jsoup.nodes.Element
import org.jsoup.select.Elements
import java.nio.charset.StandardCharsets
import java.security.MessageDigest
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class GoMovies{

    class AES {

        fun decrypt(encrypted: String, password: String): String {
            val keySize = 8
            val ivSize = 4
            val cipherText = Base64.decode(encrypted, Base64.DEFAULT)
            val prefix = ByteArray(8)
            System.arraycopy(cipherText, 0, prefix, 0, 8)
            val salt = ByteArray(8)
            System.arraycopy(cipherText, 8, salt, 0, 8)
            val trueCipherText = ByteArray(cipherText.size - 16)
            System.arraycopy(cipherText, 16, trueCipherText, 0, cipherText.size - 16)
            val javaKey = ByteArray(keySize * 4)
            val javaIv = ByteArray(ivSize * 4)
            evpKDF(
                password.toByteArray(StandardCharsets.UTF_8),
                keySize,
                ivSize,
                salt,
                javaKey,
                javaIv
            )
            val aesCipherForEncryption = Cipher.getInstance("AES/CBC/PKCS5Padding")
            val ivSpec = IvParameterSpec(javaIv)
            aesCipherForEncryption.init(Cipher.DECRYPT_MODE, SecretKeySpec(javaKey, "AES"), ivSpec)
            val byteMsg = aesCipherForEncryption.doFinal(trueCipherText)
            return String(byteMsg, StandardCharsets.UTF_8)
        }

        private fun evpKDF(
            password: ByteArray,
            keySize: Int,
            ivSize: Int,
            salt: ByteArray,
            resultKey: ByteArray,
            resultIv: ByteArray
        ): ByteArray {
            return evpKDF(password, keySize, ivSize, salt, 1, "MD5", resultKey, resultIv)
        }

        private fun evpKDF(
            password: ByteArray,
            keySize: Int,
            ivSize: Int,
            salt: ByteArray,
            iterations: Int,
            hashAlgorithm: String,
            resultKey: ByteArray,
            resultIv: ByteArray
        ): ByteArray {
            val targetKeySize = keySize + ivSize
            val derivedBytes = ByteArray(targetKeySize * 4)
            var numberOfDerivedWords = 0
            var block: ByteArray? = null
            val hasher = MessageDigest.getInstance(hashAlgorithm)
            while (numberOfDerivedWords < targetKeySize) {
                if (block != null) {
                    hasher.update(block)
                }
                hasher.update(password)
                block = hasher.digest(salt)
                hasher.reset()
                for (i in 1 until iterations) {
                    block = hasher.digest(block)
                    hasher.reset()
                }
                System.arraycopy(
                    block, 0, derivedBytes, numberOfDerivedWords * 4,
                    minOf(block?.size!!, (targetKeySize - numberOfDerivedWords) * 4)
                )
                numberOfDerivedWords += block.size / 4
            }
            System.arraycopy(derivedBytes, 0, resultKey, 0, keySize * 4)
            System.arraycopy(derivedBytes, keySize * 4, resultIv, 0, ivSize * 4)
            return derivedBytes
        }

    }

    private val aes = AES()
    private val gson = Gson()
    private val app = Requests()
    private val proxy = ""
    private val headers = mapOf("X-Requested-With" to "XMLHttpRequest")
    private val baseUrl = "https://gomovies.sx"
    suspend fun search(s: Int, ep:Int ,query: String,isMovie: Boolean,y :String) : Pair<String?,String?>{
        val squery = query.lowercase().replace(" ","-")
        val sources: Elements

        val searchRes = app.get(
            "${proxy}https://gomovies.sx/search/${squery}",
            headers = headers
        ).document

        val search = searchRes.select("div.flw-item h2 a")
        val year = searchRes.select("div.flw-item div.fd-infor").map{
            it.getElementsByTag("span")[0].text()
        }

        var mediaId = ""
        var i = 0
        for (se in search) {
            if (se.attr("title") == query && onConflict(se,isMovie,year, y,i)) {
                mediaId = se.attr("href").substringAfter("gomovies-")
                println(mediaId)
                break
            }
            i++
        }

        if (mediaId == "") return Pair( null, null)
        if(!isMovie) {
            val seasons = app.get(
                "${proxy}https://gomovies.sx/ajax/v2/tv/seasons/${mediaId}", headers = headers
            ).document.select(".ss-item")

//        for (s in seasons) {
//            println(s)
//        }
            val dataId = seasons[s - 1].attr("data-id")

            val episodes = app.get(
                "${proxy}$baseUrl/ajax/season/episodes/${dataId}", headers = headers
            ).document.select(".eps-item")

//            for (ep in episodes)
//                println(ep)

            val epDataId = episodes[ep - 1].attr("data-id")

            sources = app.get(
                "${proxy}$baseUrl/ajax/episode/servers/$epDataId",
                headers = headers
            ).document.select("li.nav-item a")
        }else{
            sources = app.get(
                "${proxy}$baseUrl/ajax/episode/list/$mediaId",
                headers = headers
            ).document.select("li.nav-item a")
        }
        var upCloudId = ""
        for (source in sources) {
            if (source.attr("title").contains("UpCloud")) upCloudId = if(!isMovie)source.attr("data-id") else source.attr("data-linkid")
        }
        if (upCloudId == "") return Pair( null, null)
//        println("upcloudid below")
//        println(upCloudId)

        val upCloudSource = app.get("${proxy}$baseUrl/ajax/sources/$upCloudId").toString()
        val upcloudSrc = gson.fromJson(upCloudSource, UpCloud::class.java)
//        println(upcloudSrc.link)
        upcloudSrc.link = upcloudSrc.link?.replace("?z=", "")
        val upDataID = upcloudSrc.link?.substringAfter("embed-4/")
        val streamRes = app.get(
            "https://rabbitstream.net/ajax/embed-4/getSources?id=${upDataID}",
            headers = headers,
            referer = "https://rabbitstream.net"
        ).toString()

        val stream = gson.fromJson(streamRes, StreamRes::class.java)
//        println(stream)

        val sub = getSubs2(stream.tracks)


        val scriptJs = app.get("https://rabbitstream.net/js/player/prod/e4-player.min.js").toString()
//        println(decryptionKeyResponse)

//        val listType = object : TypeToken<List<List<Int>>>() {}.type
        val decryptionKey: List<Pair<Int,Int>>? = extractKey(scriptJs)
        if (decryptionKey.isNullOrEmpty()) println("Cant extract key")
//        println(decryptionKey)

        var extractedKey = ""
        var strippedSources = stream.sources
        var totalledOffset = 0
        decryptionKey?.forEach { pair ->
            val start = pair.first + totalledOffset;
            val end = start + pair.second;
            extractedKey += stream.sources.slice(start until end)
            strippedSources = strippedSources.replace(
                stream.sources.substring(start, end),
                ""
            )
            totalledOffset += pair.second
        }
        val key = extractedKey
        val data = strippedSources
        val decryptedStream = aes.decrypt(data, key)
//        println(decryptedStream)
        val jsonArray = JSONArray(decryptedStream)
        val videoSrc = gson.fromJson(jsonArray.getJSONObject(0).toString(),VidFile::class.java)
        println(videoSrc)
        return Pair(videoSrc.file, sub)

    }
    private fun onConflict(item : Element, movie: Boolean, year: List<String>, y: String, i: Int): Boolean {
        return if(movie && item.attr("href").contains("/movie/") && year[i] == y ) true
        else !movie && item.attr("href").contains("/tv/")
    }

    private fun extractKey(script: String): List<Pair<Int, Int>>? {
        val startOfSwitch = script.lastIndexOf("switch")
        val endOfCases = script.indexOf("partKeyStartPosition")
        val switchBody = script.slice(startOfSwitch until endOfCases)

        val nums = mutableListOf<Pair<Int, Int>>()
        val matches = Regex(":[a-zA-Z0-9]+=([a-zA-Z0-9]+),[a-zA-Z0-9]+=([a-zA-Z0-9]+);").findAll(switchBody)
        for (match in matches) {
            val innerNumbers = mutableListOf<Int>()
            for (varMatch in listOf(match.groupValues[1], match.groupValues[2])) {
                val regex = Regex("${varMatch}=0x([a-zA-Z0-9]+)")
                val varMatches = regex.findAll(script).toList()
                val lastMatch = varMatches.lastOrNull()
                if (lastMatch == null) {
                    return null
                }
                val number = Integer.parseInt(lastMatch.groupValues[1], 16)
                innerNumbers.add(number)
            }

            nums.add(Pair(innerNumbers[0], innerNumbers[1]))
        }

        return nums
    }


    data class StreamRes(

        val server: Int,
        val sources: String,
        val tracks: ArrayList<Track>
    )

    data class Track(
        val file: String,
        val kind: String,
        val label: String
    )

    data class UpCloud(
        val type: String?,
        var link: String?,
        val sources: ArrayList<String>?,
        val tracks: ArrayList<String>?,
        val title: String?
    )

    data class VidFile(
        val file: String?,
        val type: String?
    )

    private fun getSubs(tracks: ArrayList<Track>?): ArrayList<String>? {
        if(tracks == null) return null
        return if (tracks.size == 1){
            arrayListOf(tracks[0].file)
        } else{
            val subs = arrayListOf<String>()
            for(track in tracks){
                if(track.label.contains("English"))
                    subs.add(track.file)
            }
            subs
        }
    }

    private fun getSubs2(tracks: ArrayList<Track>?): String? {
        if(tracks == null) return null
        val subUrl : MutableMap<String,String> = mutableMapOf()
        tracks.forEach {
            subUrl[it.label] = it.file
        }
        return gson.toJson(subUrl)
    }
}
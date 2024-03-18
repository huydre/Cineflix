package com.example.cineflix

import com.example.cineflix.Model.network.OPhimRespone
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Path


private val retrofit: Retrofit by lazy {
    Retrofit.Builder()
        .baseUrl("https://ophim1.com/")
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}

val oPhimApiService: OPhimApiService by lazy {
    retrofit.create(OPhimApiService::class.java)
}

interface OPhimApiService {
    @GET("phim/{slug}")
    suspend fun getOPhimDetails(
        @Path("slug") slug: String
    ) : Response<OPhimRespone>
}

class OPhimRepository {
    suspend fun getOPhimDetails(slug: String): Response<OPhimRespone>{
        return oPhimApiService.getOPhimDetails(slug)
    }
}
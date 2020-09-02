package de.jannis_jahr.motioncapturingapp.network.services

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// https://dev.to/paulodhiambo/kotlin-and-retrofit-network-calls-2353 : Modified
object ServiceBuilder {
    private val client = OkHttpClient.Builder().build()

    private var retrofit = Retrofit.Builder()
        .baseUrl("https://api.themoviedb.org/")
        .addConverterFactory(GsonConverterFactory.create())
        .client(client)
        .build()

    fun buildRetrofit(url:String) {
        retrofit = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create())
            .client(client)
            .build()
    }

    fun<T> buildService(service: Class<T>): T{
        return retrofit.create(service)
    }
}
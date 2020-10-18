package de.jannis_jahr.motioncapturingapp.network.services

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.text.DateFormat

// https://dev.to/paulodhiambo/kotlin-and-retrofit-network-calls-2353 : Modified
object ServiceBuilder {
    val gson = GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
            .create()

    private val client = OkHttpClient.Builder().build()

    private var retrofit = Retrofit.Builder()
        .baseUrl("https://api.themoviedb.org/")
        .addConverterFactory(GsonConverterFactory.create(gson))
        .client(client)
        .build()

    fun buildRetrofit(url:String) {
        retrofit = Retrofit.Builder()
            .baseUrl(url)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .client(client)
            .build()
    }

    fun<T> buildService(service: Class<T>): T{
        return retrofit.create(service)
    }
}
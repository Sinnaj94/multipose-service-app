package de.jannis_jahr.motioncapturingapp.utils

import android.content.SharedPreferences
import de.jannis_jahr.motioncapturingapp.network.services.MocapService
import de.jannis_jahr.motioncapturingapp.network.services.authentication.BasicAuthInterceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NetworkUtils {
    companion object {
        public fun getService(host: String, token: String) : MocapService {
            val client = OkHttpClient.Builder()
                            .addInterceptor(BasicAuthInterceptor(token, ""))
                            .build()
            return Retrofit.Builder()
                .baseUrl(host)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(MocapService::class.java)
        }

        public fun getService(sharedPrefs: SharedPreferences) : MocapService? {
            val token= sharedPrefs.getString("token", null)
            val host = sharedPrefs.getString("hostname", null)
            if(token != null && host != null) {
                return getService(host, token)
            }
            return null
        }
    }
}
package de.jannis_jahr.motioncapturingapp.utils

import android.content.SharedPreferences
import com.google.gson.GsonBuilder
import de.jannis_jahr.motioncapturingapp.network.services.MocapService
import de.jannis_jahr.motioncapturingapp.network.services.authentication.BasicAuthInterceptor
import de.jannis_jahr.motioncapturingapp.preferences.ApplicationConstants
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class NetworkUtils {
    companion object {
        val gson = GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss")
                .create()
        public fun getServiceNoAuth(host: String) : MocapService {
            return Retrofit.Builder()
                .baseUrl("$host${ApplicationConstants.BASE_ROUTE}")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(MocapService::class.java)
        }

        public fun getService(host: String, token: String) : MocapService {
            return getService(host, token, "")
        }

        public fun getService(host: String, user: String, pass: String): MocapService {
            val client = OkHttpClient.Builder()
                .addInterceptor(BasicAuthInterceptor(user, pass))
                .build()
            return Retrofit.Builder()
                .baseUrl("$host${ApplicationConstants.BASE_ROUTE}")
                .client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()
                .create(MocapService::class.java)
        }

        public fun logout(sharedPrefs: SharedPreferences) {
            val edit = sharedPrefs.edit()
            edit.remove("token")
            edit.commit()
        }

        public fun getService(sharedPrefs: SharedPreferences) : MocapService? {
            val token= sharedPrefs.getString("token", null)
            val host = sharedPrefs.getString("hostname", null)
            if(token != null && host != null) {
                return getService(host, token)
            }
            return null
        }

        fun getHost(sharedPrefs: SharedPreferences) : String? {
            return sharedPrefs.getString("hostname", null)
        }
    }
}
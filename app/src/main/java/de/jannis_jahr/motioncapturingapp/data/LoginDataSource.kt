package de.jannis_jahr.motioncapturingapp.data

import android.os.AsyncTask
import de.jannis_jahr.motioncapturingapp.network.services.MocapService
import de.jannis_jahr.motioncapturingapp.network.services.authentication.BasicAuthInterceptor
import de.jannis_jahr.motioncapturingapp.network.services.model.Token
import de.jannis_jahr.motioncapturingapp.network.services.model.User
import okhttp3.OkHttpClient
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
class LoginDataSource : AsyncTask<Array<String>, Void, Response<User>>() {
    companion object {
        val TAG = "LOGIN_DATA_SOURCE"
    }
    fun login(host: String, username: String, password: String, callback: Callback<Token>) {
        // TODO: handle loggedInUser authentication
        // TODO: Auth auslagern.
        val client = OkHttpClient.Builder()
            .addInterceptor(BasicAuthInterceptor(username, password))
            .build()
        val request = Retrofit.Builder()
                .baseUrl(host)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(MocapService::class.java)
        val call = request.authorize()
        call.enqueue(callback)
    }

    fun logout() {
        // TODO: revoke authentication
    }

    override fun doInBackground(vararg p0: Array<String>?): Response<User> {
        TODO("Not yet implemented")
    }
}
package de.jannis_jahr.motioncapturingapp.data

import android.os.AsyncTask
import de.jannis_jahr.motioncapturingapp.network.services.MocapService
import de.jannis_jahr.motioncapturingapp.network.services.authentication.BasicAuthInterceptor
import de.jannis_jahr.motioncapturingapp.network.services.model.Token
import de.jannis_jahr.motioncapturingapp.network.services.model.User
import de.jannis_jahr.motioncapturingapp.utils.NetworkUtils.Companion.getService
import de.jannis_jahr.motioncapturingapp.utils.NetworkUtils.Companion.getServiceNoAuth
import okhttp3.MediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody
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
        val request = getService(host, username, password)
        val call = request.authorize()
        call.enqueue(callback)
    }

    fun logout() {
        // TODO: revoke authentication
    }

    override fun doInBackground(vararg p0: Array<String>?): Response<User> {
        TODO("Not yet implemented")
    }

    fun register(host: String, username: String, password: String, callback: Callback<User>) {
        val request = getServiceNoAuth(host)
        val call = request.register(username = RequestBody.create(MediaType.parse("text/plain"), username),
            password = RequestBody.create(MediaType.parse("text/plain"), password))
        call.enqueue(callback)
    }
}
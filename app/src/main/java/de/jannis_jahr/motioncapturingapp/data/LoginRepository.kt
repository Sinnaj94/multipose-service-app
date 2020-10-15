package de.jannis_jahr.motioncapturingapp.data

import android.content.Context
import android.content.SharedPreferences
import de.jannis_jahr.motioncapturingapp.data.model.LoggedInUser
import de.jannis_jahr.motioncapturingapp.network.services.model.Token
import de.jannis_jahr.motioncapturingapp.network.services.model.User
import retrofit2.Callback

/**
 * Class that requests authentication and user information from the remote data source and
 * maintains an in-memory cache of login status and user credentials information.
 */

class LoginRepository(val dataSource: LoginDataSource) {

    // in-memory cache of the loggedInUser object
    var user: LoggedInUser? = null
        private set

    val isLoggedIn: Boolean
        get() = user != null

    init {
        // If user credentials will be cached in local storage, it is recommended it be encrypted
        // @see https://developer.android.com/training/articles/keystore
        user = null
    }

    fun logout() {
        user = null
        dataSource.logout()
    }

    fun login(host: String, username: String, password: String, callback: Callback<Token>) {
        val result = dataSource.login(host, username, password, callback)
    }

    fun login(token: String, host: String, callback: Callback<Token>) {
        val result = dataSource.login(host, token, "", callback)
    }

    fun register(host: String, username: String, password: String, callback: Callback<User>) {
        // handle login
        dataSource.register(host, username, password, callback)
    }

    private fun getSharedPrefs(context: Context): SharedPreferences? {
        return context.getSharedPreferences("users", Context.MODE_PRIVATE)
    }

    public fun loadConnection(context: Context) : Boolean {
        val prefs = getSharedPrefs(context)
        val token= prefs?.getString("token", null)
        val host = prefs?.getString("hostname", null)
        if(token != null && host != null) {
            user = LoggedInUser(token, host)
            return true
        }
        return false
    }

    public fun setConnection(user: LoggedInUser, context: Context) {
        val prefs = getSharedPrefs(context)
        this.user = user
        // Save to shared preferences
        val edit = prefs!!.edit()
        edit.putString("token", user.token)
        edit.putString("hostname", user.host)
        edit.apply()
    }
}
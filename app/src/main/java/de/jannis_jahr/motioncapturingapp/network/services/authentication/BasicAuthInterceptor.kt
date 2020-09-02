package de.jannis_jahr.motioncapturingapp.network.services.authentication

import okhttp3.Credentials
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import java.io.IOException

// source: https://stackoverflow.com/questions/43366164/retrofit-and-okhttp-basic-authentication
class BasicAuthInterceptor(user: String, password: String) : Interceptor {
    private var credentials: String? = null

    init {
        credentials = Credentials.basic(user, password)
    }

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response? {
        val request: Request = chain.request()
        val authenticatedRequest: Request = request.newBuilder()
            .header("Authorization", credentials!!).build()
        return chain.proceed(authenticatedRequest)
    }

}
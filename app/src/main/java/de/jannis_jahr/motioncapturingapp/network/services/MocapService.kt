package de.jannis_jahr.motioncapturingapp.network.services

import retrofit2.Call
import retrofit2.http.GET
import de.jannis_jahr.motioncapturingapp.network.services.model.Status
import de.jannis_jahr.motioncapturingapp.network.services.model.Token
import de.jannis_jahr.motioncapturingapp.network.services.model.User
import retrofit2.http.POST

interface MocapService {
    @GET("status/")
    fun getStatus() : Call<Status>

    @POST("users/")
    fun register() : Call<User>

    @GET("users/{id}")
    fun getUser() : Call<User>

    @GET("users/login")
    fun login() : Call<User>

    @GET("auth/token")
    fun authorize() : Call<Token>
}
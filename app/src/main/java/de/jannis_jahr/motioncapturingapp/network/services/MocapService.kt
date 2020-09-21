package de.jannis_jahr.motioncapturingapp.network.services

import de.jannis_jahr.motioncapturingapp.network.services.model.Job
import retrofit2.Call
import retrofit2.http.GET
import de.jannis_jahr.motioncapturingapp.network.services.model.Status
import de.jannis_jahr.motioncapturingapp.network.services.model.Token
import de.jannis_jahr.motioncapturingapp.network.services.model.User
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

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

    @Multipart
    @POST("jobs/")
    fun addJob(@Part video: MultipartBody.Part, @Part("autoplay") autoplay: Boolean) : Call<Job>

    @GET("jobs/")
    fun getJobs() : Call<List<Job>>
}
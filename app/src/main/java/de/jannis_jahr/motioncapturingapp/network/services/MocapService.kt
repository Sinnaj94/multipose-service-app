package de.jannis_jahr.motioncapturingapp.network.services

import de.jannis_jahr.motioncapturingapp.network.services.model.Job
import retrofit2.Call
import de.jannis_jahr.motioncapturingapp.network.services.model.Status
import de.jannis_jahr.motioncapturingapp.network.services.model.Token
import de.jannis_jahr.motioncapturingapp.network.services.model.User
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

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

    @POST("jobs/")
    fun addJob(@Body name: RequestBody?) : Call<Job>

    @PUT("jobs/{id}/upload")
    fun uploadJob(@Path("id") user : String, @Part video: MultipartBody.Part) : Call<Job>

    @GET("jobs/")
    fun getJobs(@Query("result_code") result_code: Int?) : Call<List<Job>>
}
package de.jannis_jahr.motioncapturingapp.network.services

import de.jannis_jahr.motioncapturingapp.network.services.model.*
import retrofit2.Call
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface MocapService {
    @GET("status/")
    fun getStatus() : Call<APIStatus>

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
    fun addJob(@Part("name") name: RequestBody?) : Call<Job>

    @Multipart
    @PUT("jobs/{id}/upload")
    fun uploadJob(@Path("id") user : String, @Part video: MultipartBody.Part) : Call<Job>

    @GET("jobs/")
    fun getJobs(@Query("result_code") result_code: Int?) : Call<List<Job>>

    @GET("jobs/{id}")
    fun getJob(@Path("id") id : String) : Call<Job>

    @GET("jobs/{id}/status")
    fun getJobStatus(@Path("id") id: String) : Call<Status>
}
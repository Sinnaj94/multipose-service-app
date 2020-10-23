package de.jannis_jahr.motioncapturingapp.network.services

import de.jannis_jahr.motioncapturingapp.network.services.model.*
import retrofit2.Call
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface MocapService {
    @GET("status/")
    fun getStatus() : Call<APIStatus>

    @Multipart
    @POST("users/")
    fun register(@Part("username") username : RequestBody, @Part("password") password : RequestBody) : Call<User>

    @GET("users/{id}")
    fun getUser() : Call<User>

    @GET("users/login")
    fun login() : Call<User>

    @GET("auth/token")
    fun authorize() : Call<Token>

    @FormUrlEncoded
    @POST("jobs/")
    fun addJob(@Field("name") name: String, @Field("tags") tags: ArrayList<String>) : Call<Job>

    @Multipart
    @PUT("jobs/{id}/upload")
    fun uploadJob(@Path("id") user : String, @Part video: MultipartBody.Part) : Call<Job>

    @GET("jobs/")
    fun getJobs(@Query("result_code") result_code: Int?) : Call<List<Job>>

    @GET("jobs/{id}")
    fun getJob(@Path("id") id : String) : Call<Job>

    @GET("jobs/{id}/status")
    fun getJobStatus(@Path("id") id: String) : Call<Status>

    @GET("results/{id}")
    fun getResult(@Path("id") id: String): Call<Result>

    @DELETE("jobs/{id}")
    fun deleteJob(@Path("id") id: String) : Call<Boolean>

    @GET("posts/")
    fun getPosts( @Query("tags[]") tags: ArrayList<String>?) : Call<List<Job>>

    @POST("posts/{id}")
    fun postJob(@Path("id") id: String) : Call<Job>

    @DELETE("posts/{id}")
    fun deleteJobPost(@Path("id") id: String) : Call<Job>

    @POST("jobs/{id}/bookmark")
    fun postBookmark(@Path("id") id: String) : Call<BookmarkStatus>

    @DELETE("jobs/{id}/bookmark")
    fun deleteBookmark(@Path("id") id: String) : Call<BookmarkStatus>
}
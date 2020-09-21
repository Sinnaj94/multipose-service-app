package de.jannis_jahr.motioncapturingapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.FileUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import de.jannis_jahr.motioncapturingapp.network.services.MocapService
import de.jannis_jahr.motioncapturingapp.network.services.authentication.BasicAuthInterceptor
import de.jannis_jahr.motioncapturingapp.network.services.model.Job
import kotlinx.android.synthetic.main.activity_send_job.*
import kotlinx.android.synthetic.main.fragment_add.*
import kotlinx.android.synthetic.main.fragment_add.video_view
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

class SendJobActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_job)
        val path = intent.getStringExtra("video_file")
        video_view.setVideoPath(path)
        video_view.setOnPreparedListener {
            video_view.start()
            it.isLooping = true
        }
        // cancel button
        cancel_action.setOnClickListener {
            setResult(Activity.RESULT_CANCELED)
            finish()
        }

        analyse_action.setOnClickListener {
            // Send to server
            // TODO: handle loggedInUser authentication
            // TODO: Auth auslagern.

            val sp= applicationContext.getSharedPreferences("users", Context.MODE_PRIVATE)

            val token= sp?.getString("token", null)
            val host = sp?.getString("hostname", null)

            if(token != null && host != null) {
                val client = OkHttpClient.Builder()
                    .addInterceptor(BasicAuthInterceptor(token, ""))
                    .build()
                val request = Retrofit.Builder()
                    .baseUrl(host)
                    .client(client)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(MocapService::class.java)

                //val video = RequestBody.create("video/mp4", )
                val file = File(path!!)
                val video_file = RequestBody.create(MediaType.parse("video/mp4"), file)
                val body = MultipartBody.Part.createFormData("video", file.name,video_file)
                analyse_action.isEnabled = false
                val call = request.addJob(body, true)
                call.enqueue(object : Callback<Job> {

                    override fun onResponse(call: Call<Job>, response: Response<Job>) {
                        if(response.code() == 200) {
                            Toast.makeText(application, R.string.video_upload_success, Toast.LENGTH_SHORT).show()
                            // Finish and set Result to Okay
                            setResult(Activity.RESULT_OK)
                            finish()
                        } else {
                            Toast.makeText(application, R.string.error, Toast.LENGTH_SHORT).show()
                        }
                        analyse_action.isEnabled = true
                    }

                    override fun onFailure(call: Call<Job>, t: Throwable) {
                        Toast.makeText(application, R.string.network_error, Toast.LENGTH_SHORT).show()
                        analyse_action.isEnabled = true
                    }

                })
            }
        }
    }

}
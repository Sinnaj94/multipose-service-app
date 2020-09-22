package de.jannis_jahr.motioncapturingapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import de.jannis_jahr.motioncapturingapp.network.services.MocapService
import de.jannis_jahr.motioncapturingapp.network.services.authentication.BasicAuthInterceptor
import de.jannis_jahr.motioncapturingapp.network.services.model.Job
import de.jannis_jahr.motioncapturingapp.utils.RealPathUtil
import kotlinx.android.synthetic.main.activity_send_job.*
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
    var fileURI : Uri? = null

    override fun onBackPressed() {
        exit()
    }

    private fun exit() {
        setResult(Activity.RESULT_CANCELED)
        finish()
    }

    private fun getService(host: String, token: String) : MocapService {
        val client = OkHttpClient.Builder()
                        .addInterceptor(BasicAuthInterceptor(token, ""))
                        .build()
        return Retrofit.Builder()
            .baseUrl(host)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MocapService::class.java)
    }

    private fun send() {
        val sp= applicationContext.getSharedPreferences("users", Context.MODE_PRIVATE)

        val token= sp?.getString("token", null)
        val host = sp?.getString("hostname", null)

        if(token != null && host != null) {
            val service = getService(host, token)
            fullUploadJob(service, fileURI!!)
        }
    }

    private fun fullUploadJob(service: MocapService, file: Uri) {
        // Create "Job" Object
        // TODO: Name
        var nameConverted : RequestBody = if(!job_name.text.isBlank()) {
            RequestBody.create(MediaType.parse("text/plain"), job_name.text.toString())
        } else {
            RequestBody.create(MediaType.parse("text/plain"), "My Job")
        }
        val call = service.addJob(nameConverted)
        call.enqueue(object : Callback<Job> {
            override fun onResponse(call: Call<Job>, response: Response<Job>) {
                Toast.makeText(applicationContext, "Job \"${response.body()?.name}\" was created", Toast.LENGTH_SHORT).show()

                if(response.code() == 200) {
                    val intent = Intent()
                    intent.putExtra("id", response.body()?.id.toString())
                    intent.data = fileURI
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                }
            }

            override fun onFailure(call: Call<Job>, t: Throwable) {
                Toast.makeText(applicationContext, "Something went wrong", Toast.LENGTH_SHORT).show()
                t.printStackTrace()
            }

        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_send_job)
        setContentView(R.layout.activity_send_job)
        fileURI = intent.data
        video_view.setDataSource(RealPathUtil.getRealPath(applicationContext, fileURI))
        video_view.prepare {
            it.start()
            it.isLooping = true
        }
        // Show home as up
        supportActionBar?.setDisplayHomeAsUpEnabled(true);
        supportActionBar?.setDisplayShowHomeEnabled(true);
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> onBackPressed()
            R.id.send_to_server -> send()
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        //menuInflater.inflate(R.menu.settings_menu, menu)
        menuInflater.inflate(R.menu.send_menu, menu)
        return true
    }

}
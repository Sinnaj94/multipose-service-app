package de.jannis_jahr.motioncapturingapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.material.chip.Chip
import de.jannis_jahr.motioncapturingapp.network.services.MocapService
import de.jannis_jahr.motioncapturingapp.network.services.model.Job
import de.jannis_jahr.motioncapturingapp.preferences.ApplicationConstants
import de.jannis_jahr.motioncapturingapp.utils.NetworkUtils
import kotlinx.android.synthetic.main.activity_send_job.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SendJobActivity : AppCompatActivity() {
    var fileURI : Uri? = null
    var tagList: ArrayList<String> = arrayListOf()
    var player: ExoPlayer? = null
    override fun onBackPressed() {
        exit()
    }

    private fun exit() {
        setResult(Activity.RESULT_CANCELED)
        player?.stop()
        finish()
    }

    private fun send() {
        val service = NetworkUtils.getService(applicationContext.getSharedPreferences(ApplicationConstants.PREFERENCES, Context.MODE_PRIVATE))
        fullUploadJob(service!!, fileURI!!)
        player?.stop()
        player
    }

    private fun fullUploadJob(service: MocapService, file: Uri) {
        // Create "Job" Object
        // TODO: Name
        var name : String = if(!job_name.text.isBlank()) {
            job_name.text.toString()
        } else {
            "My Job"
        }


        val call = service.addJob(name, tagList)
        call.enqueue(object : Callback<Job> {
            override fun onResponse(call: Call<Job>, response: Response<Job>) {

                if(response.code() == 200) {
                    Toast.makeText(applicationContext, "Job \"${response.body()?.name}\" was created", Toast.LENGTH_SHORT).show()
                    val intent = Intent()
                    intent.putExtra("id", response.body()?.id.toString())
                    intent.data = file
                    setResult(Activity.RESULT_OK, intent)
                    finish()
                } else {
                    Toast.makeText(applicationContext, "Job \"${response.body()?.name}\" could not be created", Toast.LENGTH_SHORT).show()
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
        player = SimpleExoPlayer.Builder(applicationContext).build()
        val vid = MediaItem.fromUri(fileURI!!)
        player!!.setMediaItem(vid)
        player!!.repeatMode = Player.REPEAT_MODE_ALL
        player!!.prepare()
        player!!.play()
        video_view.player = player
        // Show home as up
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)

        add_tag.setOnClickListener {
            addTag()
        }

        job_tag.setOnKeyListener { v, keyCode, event ->
            if(event.action == KeyEvent.ACTION_DOWN &&
                    keyCode == KeyEvent.KEYCODE_ENTER) {
                addTag()
                return@setOnKeyListener false
            }
            true
        }
    }

    fun addTag() {
        if(!job_tag.text.isEmpty()) {
            val addedText = job_tag.text.toString().toLowerCase().trim()
            if(!tagList.contains(addedText)) {
                val chip = Chip(tags.context)
                chip.text = addedText
                chip.isCloseIconVisible = true
                chip.setOnCloseIconClickListener {
                    tags.removeView(it)
                    tagList.remove(addedText)
                }
                tagList.add(addedText)
                tags.addView(chip)
                job_tag.text.clear()
            }
        }
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
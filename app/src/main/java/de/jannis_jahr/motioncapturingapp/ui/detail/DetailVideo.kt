package de.jannis_jahr.motioncapturingapp.ui.detail

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.ProgressBar
import android.widget.SeekBar
import android.widget.TextView
import android.widget.VideoView
import androidx.fragment.app.Fragment
import com.google.android.exoplayer2.ExoPlayerFactory
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.SimpleExoPlayer
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector
import com.google.android.exoplayer2.ui.PlayerView
import de.jannis_jahr.motioncapturingapp.R
import de.jannis_jahr.motioncapturingapp.network.services.MocapService
import de.jannis_jahr.motioncapturingapp.network.services.model.Job
import de.jannis_jahr.motioncapturingapp.preferences.ApplicationConstants
import de.jannis_jahr.motioncapturingapp.utils.NetworkUtils
import kotlinx.android.synthetic.main.fragment_detail_output_video.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


private const val ARG_ID = "id"

open class DetailVideo : Fragment() {
    // TODO: Rename and change types of parameters
    private var id: Int? = null
    val instanceType = this

    lateinit var prefs : SharedPreferences
    private lateinit var service : MocapService
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            id = it.getInt(ARG_ID)
        }
        prefs = requireContext().getSharedPreferences(ApplicationConstants.PREFERENCES, Context.MODE_PRIVATE)
        service = NetworkUtils.getService(prefs)!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_detail_source_video, container, false)
        refreshData(view)
        return view
    }


    fun refreshData(view: View) {
        val call = service.getJob(id!!)
        call.enqueue(object : Callback<Job> {
            override fun onResponse(call: Call<Job>, response: Response<Job>) {
                if(response.code() == 200) {
                    val myVideoURL : String =
                        response.body()!!.input_video_url
                    val uri = Uri.parse(prefs.getString("hostname", "") + myVideoURL)
                    val videoHolder = view.findViewById<PlayerView>(R.id.video_view)
                    val player = SimpleExoPlayer.Builder(requireContext()).build()
                    val vid = MediaItem.fromUri(uri)
                    player.setMediaItem(vid)
                    player.repeatMode = Player.REPEAT_MODE_ALL
                    player.prepare()
                    player.play()
                    videoHolder.player = player
                }
            }

            override fun onFailure(call: Call<Job>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }

}

class DetailHTMLView : Fragment() {
    private var myID: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            myID = it.getString(ARG_ID)
        }
    }

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_detail_output_video, container, false)
        val webView = view.findViewById<WebView>(R.id.web_view)
        webView.settings.javaScriptEnabled = true
        webView.settings.domStorageEnabled = true
        webView.settings.databaseEnabled = true
        
        val myHost = NetworkUtils.getHost(requireContext().getSharedPreferences(ApplicationConstants.PREFERENCES, Context.MODE_PRIVATE))
        webView.loadUrl("$myHost${ApplicationConstants.BASE_ROUTE}results/$myID/render_html")
        val borderSeekBar = view.findViewById<SeekBar>(R.id.border_filter)
        val u0SeekBar = view.findViewById<SeekBar>(R.id.u0_filter)
        if(myID != null) {
            val l = PropertiesChangeListener(
                    webView,
                    myHost!!,
                    myID!!,
                    borderSeekBar,
                    u0SeekBar,
                    view.findViewById(R.id.border_filter_text),
                    view.findViewById(R.id.u0_filter_text)
            )
            borderSeekBar.setOnSeekBarChangeListener(l)
            u0SeekBar.setOnSeekBarChangeListener(l)
        }

        return view
    }

    class PropertiesChangeListener(private val webView: WebView,
                                   private val host: String,
                                   private val id: String,
                                   private val borderSeekBar: SeekBar,
                                   private val u0SeekBar: SeekBar,
                                   private val borderText: TextView,
                                   private val u0Text : TextView
                                   ) : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
            if(seekBar == borderSeekBar) {
                borderText.text = "Border: ${formatBorder().toString()}"
            } else if(seekBar == u0SeekBar) {
                u0Text.text = "Cutoff frequency: ${formatU0().toString()}"
            }
        }

        override fun onStartTrackingTouch(seekBar: SeekBar?) {
        }

        fun formatBorder() : Int {
            return borderSeekBar.progress * 50
        }

        fun formatU0() : Int {
            return (u0SeekBar.progress) * 10
        }

        override fun onStopTrackingTouch(seekBar: SeekBar?) {
            val u0 = formatU0()
            val border = formatBorder()
            if(u0 == 0) {
                webView.loadUrl("$host${ApplicationConstants.BASE_ROUTE}results/$id/render_html")
                return
            }
            webView.loadUrl("$host${ApplicationConstants.BASE_ROUTE}results/$id/" +
                    "render_html_filtered?border=$border&u0=$u0")
        }

    }
}
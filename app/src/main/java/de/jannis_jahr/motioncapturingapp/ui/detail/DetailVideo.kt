package de.jannis_jahr.motioncapturingapp.ui.detail

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.VideoView
import de.jannis_jahr.motioncapturingapp.R
import de.jannis_jahr.motioncapturingapp.network.services.MocapService
import de.jannis_jahr.motioncapturingapp.network.services.model.Job
import de.jannis_jahr.motioncapturingapp.preferences.ApplicationConstants
import de.jannis_jahr.motioncapturingapp.utils.NetworkUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private const val ARG_ID = "id"

open class DetailVideo : Fragment() {
    // TODO: Rename and change types of parameters
    private var id: String? = null
    val instanceType = this
    lateinit var prefs : SharedPreferences
    private lateinit var service : MocapService
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            id = it.getString(ARG_ID)
        }
        prefs = context!!.getSharedPreferences(ApplicationConstants.PREFERENCES, Context.MODE_PRIVATE)
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
                    view.findViewById<TextView>(R.id.job_title).text = response.body()!!.name
                    val myVideoURL : String =
                    if(instanceType is DetailSourceVideo) {
                        response.body()!!.input_video_url
                    } else {
                        response.body()!!.result.output_video_url
                    }
                    val uri = Uri.parse(prefs.getString("hostname", "") + myVideoURL)
                    val videoHolder = view.findViewById<VideoView>(R.id.video_view)
                    videoHolder.setVideoURI(uri)
                    videoHolder.start()
                }
            }

            override fun onFailure(call: Call<Job>, t: Throwable) {
                TODO("Not yet implemented")
            }

        })
    }

    companion object {
        @JvmStatic
        fun newInstance(id: String) =
            DetailSourceVideo().apply {
                arguments = Bundle().apply {
                    putString(ARG_ID, id)
                }
            }
    }
}

class DetailSourceVideo : DetailVideo() {

}

class DetailOutputVideo : DetailVideo() {

}
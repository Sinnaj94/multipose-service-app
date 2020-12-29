package de.jannis_jahr.motioncapturingapp.ui.dashboard

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.clans.fab.FloatingActionButton
import com.github.clans.fab.FloatingActionMenu
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener
import de.jannis_jahr.motioncapturingapp.R
import de.jannis_jahr.motioncapturingapp.SendJobActivity
import de.jannis_jahr.motioncapturingapp.network.services.MocapService
import de.jannis_jahr.motioncapturingapp.network.services.model.Job
import de.jannis_jahr.motioncapturingapp.preferences.ApplicationConstants
import de.jannis_jahr.motioncapturingapp.ui.adapters.MyPagerAdapter
import de.jannis_jahr.motioncapturingapp.ui.jobs.JobsListFragment
import de.jannis_jahr.motioncapturingapp.utils.NetworkUtils
import de.jannis_jahr.motioncapturingapp.utils.RealPathUtil
import kotlinx.android.synthetic.main.fragment_dashboard.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

/**
 * Fragment where the user can see his own jobs and send videos to the server
 */
class DashboardFragment : Fragment() {

    private val TAG = "JOBS"
    private val VIDEO_CODE = 1
    private val SEND_CODE = 2
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        //ViewModelProviders.of(this).get(DashboardViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)

        val floatingMenu : FloatingActionMenu = root.findViewById(R.id.floating_action_menu)

        val record : FloatingActionButton = root.findViewById(R.id.record_video)
        val upload : FloatingActionButton = root.findViewById(R.id.upload_video)

        // Try to record a video
        record.setOnClickListener {
            floatingMenu.close(true)
            if (context?.packageManager?.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)!!) { // First check if camera is available in the device
                if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA) == PackageManager.PERMISSION_DENIED) {
                    ActivityCompat.requestPermissions(this.requireActivity(), arrayOf(Manifest.permission.CAMERA), 1);
                } else {
                    val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
                    startActivityForResult(intent, VIDEO_CODE);
                }

            }
        }

        // Choose a video from gallery
        upload.setOnClickListener {
            floatingMenu.close(true)
            val intent = Intent()
            intent.type = "video/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Video"), VIDEO_CODE)
        }


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        /*val fragmentAdapter = MyPagerAdapter(childFragmentManager)
        viewpager_main.adapter = fragmentAdapter
        tabs.setupWithViewPager(viewpager_main)*/
    }

    private fun uploadVideo(service: MocapService, uri: Uri, id: Int) {
        //val video = RequestBody.create("video/mp4", )
        val file = File(RealPathUtil.getRealPath(context, uri))
        val video_file = RequestBody.create(MediaType.parse("video/mp4"), file)
        val body = MultipartBody.Part.createFormData("video", file.name, video_file)

        val call = service.uploadJob(id, body)

        call.enqueue(object : Callback<Job> {

            override fun onResponse(call: Call<Job>, response: Response<Job>) {
                if(response.code() == 200) {
                    Toast.makeText(context, R.string.video_upload_success, Toast.LENGTH_SHORT).show()
                    // Finish and set Result to Okay
                    //setResult(Activity.RESULT_OK)
                    //finish()
                } else {
                    Toast.makeText(context, R.string.error, Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Job>, t: Throwable) {
                Toast.makeText(context, R.string.network_error, Toast.LENGTH_SHORT).show()
                t.printStackTrace()
            }

        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == VIDEO_CODE && resultCode == Activity.RESULT_OK) {
            val sendJob = Intent(context, SendJobActivity::class.java)
            sendJob.data = data!!.data
            startActivityForResult(sendJob, SEND_CODE)
        } else if(requestCode == SEND_CODE && resultCode == Activity.RESULT_OK) {
            // Send video
            val service = NetworkUtils.getService(requireContext().getSharedPreferences(ApplicationConstants.PREFERENCES, Context.MODE_PRIVATE))
            uploadVideo(service!!, data!!.data!!, data.getIntExtra("id", -1))
        }
    }
}
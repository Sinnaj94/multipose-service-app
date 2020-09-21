package de.jannis_jahr.motioncapturingapp.ui.add

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.hluhovskyi.camerabutton.CameraButton
import com.hluhovskyi.camerabutton.rxjava2.RxCameraButton
import com.otaliastudios.cameraview.CameraListener
import com.otaliastudios.cameraview.VideoResult
import com.otaliastudios.cameraview.controls.Mode
import de.jannis_jahr.motioncapturingapp.R
import de.jannis_jahr.motioncapturingapp.SendJobActivity
import de.jannis_jahr.motioncapturingapp.ui.observers.AddVideoObservable
import de.jannis_jahr.motioncapturingapp.ui.observers.AddVideoObserver
import kotlinx.android.synthetic.main.fragment_add.*
import java.io.File


class AddFragment : Fragment(), AddVideoObservable {
    override val observers: ArrayList<AddVideoObserver> = arrayListOf()
    private lateinit var addViewModel: AddViewModel
    companion object {
        val TAG = "ADD_FRAGMENT"
        val Image_Capture_Code = 1
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        addViewModel =
            ViewModelProviders.of(this).get(AddViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_add, container, false)

        val buttonCamera = root.findViewById<CameraButton>(R.id.camera_button)
        RxCameraButton.photoEvents(buttonCamera)
            .subscribe {
                Toast.makeText(context, R.string.prompt_hold_to_record_video, Toast.LENGTH_SHORT).show()
            }

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        camera.setLifecycleOwner(viewLifecycleOwner)
        camera.mode = Mode.VIDEO

        camera_button.setOnVideoEventListener( object: CameraButton.OnVideoEventListener {
            override fun onStart() {
                camera.takeVideoSnapshot(File.createTempFile("cached_video", "mp4", null))
            }

            override fun onFinish() {
                camera.stopVideo()
            }

            override fun onCancel() {
                camera.stopVideo()
            }
        })

        camera.addCameraListener(object : CameraListener() {

            override fun onVideoTaken(result: VideoResult) {
                //startActivityForResult()
                val sendJob = Intent(context, SendJobActivity::class.java)
                sendJob.putExtra("video_file", result.file.path)
                startActivity(sendJob)
            }

            override fun onVideoRecordingStart() {
                Log.d(TAG, "Starting video now")
            }

            override fun onVideoRecordingEnd() {
                Log.d(TAG, "Stopping video now")
            }
        })

        activity?.window?.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        activity?.actionBar?.setDisplayHomeAsUpEnabled(true)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(resultCode == Activity.RESULT_OK) {
            // Notify main activity to switch
            sendUpdateEvent()
        }
    }

    override fun onDetach() {
        super.onDetach()
        requireActivity().window.clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
    }
}
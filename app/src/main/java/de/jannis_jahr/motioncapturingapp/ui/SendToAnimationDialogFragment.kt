package de.jannis_jahr.motioncapturingapp.ui

import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.chip.ChipGroup
import de.jannis_jahr.motioncapturingapp.R
import de.jannis_jahr.motioncapturingapp.network.services.model.Job
import de.jannis_jahr.motioncapturingapp.preferences.ApplicationConstants
import de.jannis_jahr.motioncapturingapp.utils.NetworkUtils
import java.io.BufferedWriter
import java.io.IOException
import java.io.OutputStream
import java.io.PrintWriter
import java.lang.Exception
import java.net.InetSocketAddress
import java.net.Socket

class SendToAnimationDialogFragment : DialogFragment() {
    var url: String? = null
    val map : HashMap<Chip, Socket> = hashMapOf()
    val socketOutputStream : HashMap<Socket, OutputStream> = hashMapOf()
    var runningThread = true
    companion object {
        fun newInstance(bvh_url: String) : SendToAnimationDialogFragment {
            val f = SendToAnimationDialogFragment()

            val args = Bundle()
            args.putString("url", bvh_url)
            f.arguments = args

            return f
        }
    }


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        url = requireArguments().getString("url")
        val v = inflater.inflate(R.layout.fragment_send_to_animation, container, false)
        val group = v.findViewById<ChipGroup>(R.id.animation_software_group)
        val t = Thread {
            for(i in 0..255) {
                if(!runningThread) {
                    return@Thread
                }
                try {
                    Log.d("PROGRESS", i.toString())
                    val s = Socket()
                    s.connect(InetSocketAddress("192.168.178.$i", 65432), 50)
                    val a = s.getInputStream().read()
                    this@SendToAnimationDialogFragment.requireActivity().runOnUiThread {
                        val chip = Chip(group.context)
                        chip.isCheckable = true

                        var text = "Unknow Animation Software"
                        when(a) {
                            0 -> text = "Blender 2.80"
                        }
                        chip.text = text
                        group.addView(chip)

                        map[chip] = s
                    }
                } catch(e : IOException) {

                }
            }
        }
        t.start()

        val send = v.findViewById<Button>(R.id.button_send)
        val cancel = v.findViewById<Button>(R.id.button_cancel)
        cancel.setOnClickListener {
            dismiss()
        }

        send.setOnClickListener {
            var toastShowed = false
            map.forEach {
                if(it.key.isChecked) {
                    Thread {
                        val o: OutputStream = if(!socketOutputStream.containsKey(it.value)) {
                            it.value.getOutputStream()
                        } else {
                            socketOutputStream[it.value]!!
                        }
                        val output = PrintWriter(o)

                        output.write("url=" + NetworkUtils.getHost(requireContext().getSharedPreferences(ApplicationConstants.PREFERENCES,
                                Context.MODE_PRIVATE)) + url)
                        output.write("\n")
                        output.write("token=" + requireContext().getSharedPreferences(ApplicationConstants.PREFERENCES,
                                Context.MODE_PRIVATE).getString("token", "")!!)
                        output.flush()
                    }.start()
                    if (!toastShowed) {
                        Toast.makeText(context, "File sending complete. Import it in your animation software.", Toast.LENGTH_LONG).show()
                        toastShowed = true
                    }
                    dismiss()

                }
            }
        }

        return v
    }

    override fun onDismiss(dialog: DialogInterface) {
        Log.d("CLEANING", "UPPPP")
        runningThread = false
        map.forEach {
            try {
                it.value.shutdownOutput()
                it.value.shutdownInput()
                it.value.close()
            } catch(e : Exception) {
                e.printStackTrace()
            }

        }
        super.onDismiss(dialog)
    }
}
package de.jannis_jahr.motioncapturingapp.ui.user

import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import de.jannis_jahr.motioncapturingapp.R
import de.jannis_jahr.motioncapturingapp.ui.observers.AddVideoObservable
import de.jannis_jahr.motioncapturingapp.ui.observers.AddVideoObserver


class UserFragment : Fragment(), AddVideoObservable {
    var path : Uri? = null
    override val observers: ArrayList<AddVideoObserver> = arrayListOf()
    private lateinit var userViewModel: UserViewModel
    companion object {
        val TAG = "ADD_FRAGMENT"
        val Image_Capture_Code = 1
    }



    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.send_menu, menu)
        super.onCreateOptionsMenu(menu, inflater);
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

    }
}
package de.jannis_jahr.motioncapturingapp.ui.detail

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import de.jannis_jahr.motioncapturingapp.R

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ID = "id"

/**
 * A simple [Fragment] subclass.
 * Use the [DetailOperations.newInstance] factory method to
 * create an instance of this fragment.
 */
class DetailOperations : Fragment() {
    // TODO: Rename and change types of parameters
    private var id: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            id = it.getString(ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_detail_operations, container, false)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DetailOperations.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DetailOperations().apply {
                arguments = Bundle().apply {
                    putString(ID, param1)
                }
            }
    }
}
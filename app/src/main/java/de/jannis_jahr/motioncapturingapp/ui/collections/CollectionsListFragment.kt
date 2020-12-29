package de.jannis_jahr.motioncapturingapp.ui.home

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import de.jannis_jahr.motioncapturingapp.R
import de.jannis_jahr.motioncapturingapp.preferences.ApplicationConstants
import de.jannis_jahr.motioncapturingapp.ui.JobsRequestType
import de.jannis_jahr.motioncapturingapp.ui.adapters.JobsAdapter
import de.jannis_jahr.motioncapturingapp.ui.jobs.JobsViewModel
import de.jannis_jahr.motioncapturingapp.ui.view.JobListTagsObserver
import de.jannis_jahr.motioncapturingapp.ui.view_holders.JobViewHolder
import de.jannis_jahr.motioncapturingapp.utils.NetworkUtils
import kotlinx.android.synthetic.main.fragment_home.*

/**
 * Fragment where the Bookmarks are stored
 */
class CollectionsListFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var jobsViewModel: JobsViewModel
    private lateinit var jobList : ListView
    lateinit var myJobs : ArrayList<JobViewHolder>

    var textCardCount : TextView? = null
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val sharedPrefs = requireContext().getSharedPreferences(
                ApplicationConstants.PREFERENCES, Context.MODE_PRIVATE
        )
        setHasOptionsMenu(true)

        val manager = NetworkUtils.getService(sharedPrefs)
        // Create the JobsViewModel
        jobsViewModel = JobsViewModel(sharedPrefs, JobsRequestType.BOOKMARKS, null, null)
        // inflate the view
        val v = inflater.inflate(R.layout.fragment_collections, container, false)
        jobList = v.findViewById(R.id.posts_list)
        myJobs = arrayListOf<JobViewHolder>()

        // Create a job adapter
        val adapter = JobsAdapter(requireContext(), R.layout.big_list_item_jobs, myJobs, null)
        jobList.adapter = adapter
        jobList.emptyView = v.findViewById(R.id.posts_placeholder)

        // Observe the jobsviewmodel and see, if it refreshes
        jobsViewModel.getJobs().observe(viewLifecycleOwner) { item ->
            // Remove the handlers
            myJobs.forEach { it.removeHandlers() }
            myJobs.clear()
            // Add all jobs
            myJobs.addAll(item.map { JobViewHolder(it) })
            adapter.notifyDataSetChanged()
        }
        jobList.setOnItemClickListener { _, _, i, l ->
            //onJobsTap(i)
        }

        return v
    }




    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        pull_refresh_jobs.isRefreshing = true
        // Observe the is Refreshing attribute
        jobsViewModel.isRefreshing.observe(viewLifecycleOwner) {
            pull_refresh_jobs.isRefreshing = it
        }
        pull_refresh_jobs.setOnRefreshListener(this)
    }

    override fun onRefresh() {
        jobsViewModel.loadJobs()
    }
}
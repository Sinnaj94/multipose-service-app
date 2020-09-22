package de.jannis_jahr.motioncapturingapp.ui.jobs

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import de.jannis_jahr.motioncapturingapp.R
import de.jannis_jahr.motioncapturingapp.network.services.model.Job
import de.jannis_jahr.motioncapturingapp.preferences.ApplicationConstants
import de.jannis_jahr.motioncapturingapp.ui.adapters.JobsAdapter
import kotlinx.android.synthetic.main.fragment_finished_jobs.*



abstract class JobsListFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener{
    abstract var resultCode: Int
    // TODO: Rename and change types of parameters
    private lateinit var jobList : ListView

    private lateinit var jobsViewModel: JobsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val sharedPrefs = context!!.getSharedPreferences(
            ApplicationConstants.PREFERENCES, Context.MODE_PRIVATE
        )
        jobsViewModel = JobsViewModel(sharedPrefs, resultCode)

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_finished_jobs, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        jobList = view.findViewById<ListView>(R.id.job_list)
        var myJobs = arrayListOf<Job>()
        val adapter = JobsAdapter(context!!, R.layout.list_item_jobs, myJobs)
        jobList.adapter = adapter
        jobsViewModel.getJobs().observe(viewLifecycleOwner, {
            myJobs.clear()
            myJobs.addAll(it)
            adapter.notifyDataSetChanged()
        })

        pull_refresh.setOnRefreshListener(this)

        jobsViewModel.isRefreshing.observe(viewLifecycleOwner, {
            pull_refresh.isRefreshing = it
        })
    }

    override fun onRefresh() {
        jobsViewModel.loadJobs()
    }
}

class FinishedJobsFragment: JobsListFragment() {
    override var resultCode: Int
        get() = 1
        set(value) {}
}


class FailedJobsFragment: JobsListFragment() {
    override var resultCode: Int
        get() = -1
        set(value) {}
}

class PendingJobsFragment: JobsListFragment() {
    override var resultCode: Int
        get() = 0
        set(value) {}
}
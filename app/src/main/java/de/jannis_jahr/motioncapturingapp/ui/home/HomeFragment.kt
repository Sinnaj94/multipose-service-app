package de.jannis_jahr.motioncapturingapp.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.observe
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import de.jannis_jahr.motioncapturingapp.R
import de.jannis_jahr.motioncapturingapp.preferences.ApplicationConstants
import de.jannis_jahr.motioncapturingapp.ui.adapters.JobsAdapter
import de.jannis_jahr.motioncapturingapp.ui.jobs.JobsViewModel
import de.jannis_jahr.motioncapturingapp.ui.view_holders.JobViewHolder
import kotlinx.android.synthetic.main.fragment_finished_jobs.*
import kotlinx.android.synthetic.main.fragment_home.*

class HomeFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var jobsViewModel: JobsViewModel
    private lateinit var jobList : ListView
    lateinit var myJobs : ArrayList<JobViewHolder>
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val sharedPrefs = requireContext().getSharedPreferences(
            ApplicationConstants.PREFERENCES, Context.MODE_PRIVATE
        )
        jobsViewModel = JobsViewModel(sharedPrefs, true, null)
        val v = inflater.inflate(R.layout.fragment_home, container, false)
        jobList = v.findViewById(R.id.posts_list)
        myJobs = arrayListOf<JobViewHolder>()
        val adapter = JobsAdapter(requireContext(), R.layout.big_list_item_jobs, myJobs)
        jobList.adapter = adapter
        jobList.emptyView = v.findViewById(R.id.posts_placeholder)
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
        jobsViewModel.isRefreshing.observe(viewLifecycleOwner) {
            pull_refresh_jobs.isRefreshing = it
        }
        pull_refresh_jobs.setOnRefreshListener(this)
    }

    override fun onRefresh() {

    }
}
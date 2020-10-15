package de.jannis_jahr.motioncapturingapp.ui.jobs

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import de.jannis_jahr.motioncapturingapp.ui.detail.JobDetailActivity
import de.jannis_jahr.motioncapturingapp.R
import de.jannis_jahr.motioncapturingapp.preferences.ApplicationConstants
import de.jannis_jahr.motioncapturingapp.ui.adapters.JobsAdapter
import de.jannis_jahr.motioncapturingapp.ui.view_holders.JobViewHolder
import kotlinx.android.synthetic.main.fragment_finished_jobs.*



abstract class JobsListFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener{
    abstract var resultCode: Int
    // TODO: Rename and change types of parameters
    private lateinit var jobList : ListView
    lateinit var myJobs : ArrayList<JobViewHolder>
    private lateinit var jobsViewModel: JobsViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val sharedPrefs = requireContext().getSharedPreferences(
            ApplicationConstants.PREFERENCES, Context.MODE_PRIVATE
        )
        jobsViewModel = JobsViewModel(sharedPrefs, resultCode)
        jobsViewModel.loadJobs()

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_finished_jobs, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        jobList = view.findViewById<ListView>(R.id.job_list)
        myJobs = arrayListOf<JobViewHolder>()
        val adapter = JobsAdapter(requireContext(), R.layout.list_item_jobs, myJobs)
        jobList.adapter = adapter
        jobsViewModel.getJobs().observe(viewLifecycleOwner, { item ->
            // Remove the handlers
            myJobs.forEach { it.removeHandlers() }
            myJobs.clear()
            // Add all jobs
            myJobs.addAll(item.map { JobViewHolder(it) })
            adapter.notifyDataSetChanged()
        } )

        pull_refresh.setOnRefreshListener(this)

        jobsViewModel.isRefreshing.observe(viewLifecycleOwner, {
            pull_refresh.isRefreshing = it
        })
        pull_refresh.isRefreshing = true
        jobList.setOnItemClickListener { adapterView, clickedView, i, l ->
            onJobsTap(i)
        }
    }


    abstract fun onJobsTap(position : Int)

    fun debugJob(position: Int) {
        Toast.makeText(context, "Job ${myJobs[position].job.name} was clicked.", Toast.LENGTH_SHORT).show()
    }


    override fun onResume() {
        super.onResume()
        Log.d("RESUME", "Resuming fragment")
        jobsViewModel.loadJobs()
    }

    override fun onRefresh() {
        jobsViewModel.loadJobs()
    }

    fun showJobDetail(position: Int) {
        val intent = Intent(context, JobDetailActivity::class.java)
        intent.putExtra("id", myJobs[position].job.id.toString())
        intent.putExtra("title", myJobs[position].job.name)
        activity!!.startActivity(intent)
    }
}

class FinishedJobsFragment: JobsListFragment() {
    override var resultCode: Int
        get() = 1
        set(value) {}

    override fun onJobsTap(position: Int) {
        showJobDetail(position)
    }
}


class FailedJobsFragment: JobsListFragment() {
    override var resultCode: Int
        get() = -1
        set(value) {}

    override fun onJobsTap(position: Int) {
        debugJob(position)
    }
}

class PendingJobsFragment: JobsListFragment() {
    override var resultCode: Int
        get() = 0
        set(value) {}

    override fun onJobsTap(position: Int) {
        debugJob(position)
    }
}
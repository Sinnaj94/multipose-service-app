package de.jannis_jahr.motioncapturingapp.ui.dashboard

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import de.jannis_jahr.motioncapturingapp.R
import de.jannis_jahr.motioncapturingapp.network.services.helpers.HostnameSharedPreferenceLiveData
import de.jannis_jahr.motioncapturingapp.network.services.helpers.TokenSharedPreferenceLiveData
import de.jannis_jahr.motioncapturingapp.network.services.model.Job
import de.jannis_jahr.motioncapturingapp.preferences.ApplicationConstants
import de.jannis_jahr.motioncapturingapp.ui.adapters.JobsAdapter
import kotlinx.android.synthetic.main.fragment_dashboard.*

class DashboardFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var dashboardViewModel: DashboardViewModel
    private lateinit var jobList : ListView
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val sharedPrefs = context!!.getSharedPreferences(
            ApplicationConstants.PREFERENCES, Context.MODE_PRIVATE
        )
        dashboardViewModel = DashboardViewModel(sharedPrefs)
            //ViewModelProviders.of(this).get(DashboardViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_dashboard, container, false)
        val textView: TextView = root.findViewById(R.id.text_dashboard)


        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        jobList = view.findViewById<ListView>(R.id.job_list)

        val adapter = JobsAdapter(context!!, R.layout.list_item_jobs, arrayListOf())
        jobList.adapter = adapter
        dashboardViewModel.getJobs().observe(viewLifecycleOwner, Observer {
            adapter.clear()
            adapter.addAll(it)
        })

        pull_to_refresh.setOnRefreshListener(this)

        dashboardViewModel.isRefreshing.observe(viewLifecycleOwner, Observer {
            pull_to_refresh.isRefreshing = it
        })
    }

    override fun onRefresh() {
        dashboardViewModel.loadJobs()
    }


}
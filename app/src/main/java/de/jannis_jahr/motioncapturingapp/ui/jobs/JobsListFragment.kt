package de.jannis_jahr.motioncapturingapp.ui.jobs

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.AdapterView
import android.widget.AdapterView.AdapterContextMenuInfo
import android.widget.ListView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.observe
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import de.jannis_jahr.motioncapturingapp.JobDetailActivity
import de.jannis_jahr.motioncapturingapp.R
import de.jannis_jahr.motioncapturingapp.preferences.ApplicationConstants
import de.jannis_jahr.motioncapturingapp.ui.adapters.JobsAdapter
import de.jannis_jahr.motioncapturingapp.ui.view_holders.JobViewHolder
import de.jannis_jahr.motioncapturingapp.utils.NetworkUtils
import kotlinx.android.synthetic.main.fragment_finished_jobs.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


abstract class JobsListFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener{
    abstract var resultCode: Int?
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
        jobsViewModel = JobsViewModel(sharedPrefs, false, resultCode)

        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_finished_jobs, container, false)
        jobList = v.findViewById<ListView>(R.id.job_list)
        registerForContextMenu(jobList)
        myJobs = arrayListOf<JobViewHolder>()
        val adapter = JobsAdapter(requireContext(), R.layout.list_item_jobs, myJobs, null)
        jobList.adapter = adapter
        jobList.emptyView = v.findViewById(R.id.jobs_placeholder)
        jobsViewModel.getJobs().observe(viewLifecycleOwner) { item ->
            // Remove the handlers
            myJobs.forEach { it.removeHandlers() }
            myJobs.clear()
            // Add all jobs
            myJobs.addAll(item.map { JobViewHolder(it) })
            adapter.notifyDataSetChanged()
        }

        jobList.setOnItemClickListener { _, _, i, l ->
            onJobsTap(i)
        }
        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        pull_refresh.isRefreshing = true
        jobsViewModel.isRefreshing.observe(viewLifecycleOwner) {
            pull_refresh.isRefreshing = it
        }
        pull_refresh.setOnRefreshListener(this)
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        val info = item.menuInfo as AdapterContextMenuInfo
        when(item.itemId) {
            0 -> {
                showDeletePrompt(info.position)
            }
        }
        return true
    }

    override fun onCreateContextMenu(menu: ContextMenu, v: View, menuInfo: ContextMenu.ContextMenuInfo?) {
        if(v.id == jobList.id) {
            val lv = v as ListView
            val acmi = menuInfo as AdapterView.AdapterContextMenuInfo
            val obj = lv.getItemAtPosition(acmi.position) as JobViewHolder

            menu.add("Delete")
        }
    }

    abstract fun onJobsTap(position : Int)

    fun debugJob(position: Int) {
        Toast.makeText(context, "Job ${myJobs[position].job.name} was clicked.", Toast.LENGTH_SHORT).show()
    }


    override fun onResume() {
        super.onResume()
        jobsViewModel.loadJobs()
    }

    override fun onRefresh() {
        jobsViewModel.loadJobs()
    }

    fun showJobDetail(position: Int) {
        when(myJobs[position].job.result.result_code) {
            -1 -> showDeletePrompt(position)
            0 -> return
            1 -> {
                val intent = Intent(context, JobDetailActivity::class.java)
                intent.putExtra("id", myJobs[position].job.id.toString())
                intent.putExtra("title", myJobs[position].job.name)
                requireActivity().startActivity(intent)
            }
        }
    }

    fun showDeletePrompt(position: Int) {
        val builder: AlertDialog.Builder? = activity?.let {
            AlertDialog.Builder(it)
        }

        builder?.setMessage(R.string.delete_dialog_message)!!
                .setTitle(R.string.delete_dialog_title)
                .setPositiveButton(R.string.delete, DialogInterface.OnClickListener { dialog, which ->
                    // remove via api call
                    val service =
                            NetworkUtils.getService(requireContext().getSharedPreferences(
                                    ApplicationConstants.PREFERENCES,
                                    Context.MODE_PRIVATE))
                    val call = service!!.deleteJob(myJobs[position].job.id.toString())
                    call.enqueue(object: Callback<Boolean> {
                        override fun onFailure(call: Call<Boolean>, t: Throwable) {
                        }

                        override fun onResponse(call: Call<Boolean>, response: Response<Boolean>) {
                            if(response.code() == 200) {
                                jobsViewModel.loadJobs()
                            }
                        }

                    })
                }
                )

        val dialog: AlertDialog? = builder.create()

        dialog!!.show()

    }
}

class FinishedJobsFragment: JobsListFragment() {
    override var resultCode: Int?
        get() = null
        set(value) {}

    override fun onJobsTap(position: Int) {
        showJobDetail(position)
    }
}


class FailedJobsFragment: JobsListFragment() {
    override var resultCode: Int?
        get() = -1
        set(value) {}

    override fun onJobsTap(position: Int) {
        debugJob(position)
    }
}

class PendingJobsFragment: JobsListFragment() {
    override var resultCode: Int?
        get() = 0
        set(value) {}

    override fun onJobsTap(position: Int) {
        debugJob(position)
    }
}
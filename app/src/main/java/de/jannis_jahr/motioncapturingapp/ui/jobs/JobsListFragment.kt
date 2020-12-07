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
import de.jannis_jahr.motioncapturingapp.network.services.model.JobStatistics
import de.jannis_jahr.motioncapturingapp.preferences.ApplicationConstants
import de.jannis_jahr.motioncapturingapp.ui.JobsRequestType
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
    lateinit var adapter : JobsAdapter
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

        jobsViewModel = JobsViewModel(sharedPrefs, JobsRequestType.DASHBOARD, resultCode)

        // Inflate the layout for this fragment
        val v = inflater.inflate(R.layout.fragment_finished_jobs, container, false)
        jobList = v.findViewById<ListView>(R.id.job_list)
        registerForContextMenu(jobList)
        myJobs = arrayListOf<JobViewHolder>()
        adapter = JobsAdapter(requireContext(), R.layout.big_list_item_jobs, myJobs, null)
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
        loadStats()

        return v
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        pull_refresh.isRefreshing = true
        jobsViewModel.isRefreshing.observe(viewLifecycleOwner) {
            pull_refresh.isRefreshing = it
        }
        pull_refresh.setOnRefreshListener(this)
        // Remove jobs on click
        num_failed.setOnClickListener {
            val dialogClickListener = DialogInterface.OnClickListener { dialog, which ->
                when (which) {
                    DialogInterface.BUTTON_POSITIVE -> {
                        Toast.makeText(context, "Deleted failed jobs.", Toast.LENGTH_SHORT).show()
                        loadStats()
                        val ser = NetworkUtils.getService(requireContext().getSharedPreferences(
                                ApplicationConstants.PREFERENCES,
                                Context.MODE_PRIVATE
                        ))
                        val call = ser!!.deleteFailedJobs()
                        call.enqueue(object : Callback<Int> {
                            override fun onFailure(call: Call<Int>, t: Throwable) {

                            }

                            override fun onResponse(call: Call<Int>, response: Response<Int>) {
                                if(response.code() == 200) {
                                    loadStats()
                                }
                            }

                        })
                    }
                    DialogInterface.BUTTON_NEGATIVE -> {
                    }
                }
            }

            val builder = AlertDialog.Builder(context)
            builder.setMessage("These job have failed. Delete them?").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show()
        }
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
        adapter.clear()
        adapter.notifyDataSetChanged()
        jobsViewModel.loadJobs()
        loadStats()
    }

    override fun onRefresh() {
        jobsViewModel.loadJobs()
        loadStats()
    }

    fun loadStats() {
        // Load statistics
        val ser = NetworkUtils.getService(requireContext().getSharedPreferences(
                ApplicationConstants.PREFERENCES,
                Context.MODE_PRIVATE
        ))
        val call = ser!!.getJobStatistics()
        call.enqueue(object: Callback<JobStatistics> {
            override fun onFailure(call: Call<JobStatistics>, t: Throwable) {

            }

            override fun onResponse(call: Call<JobStatistics>, response: Response<JobStatistics>) {
                if(response.code() == 200) {
                    if(response.body()!!.failed == 0 && response.body()!!.pending == 0) {
                        other_categories.visibility = View.GONE
                    } else {
                        other_categories.visibility = View.VISIBLE
                        num_failed.text = "Failed Jobs: ${response.body()!!.failed}"
                        num_pending.text = "Pending Jobs: ${response.body()!!.pending}"
                    }
                }
            }

        })
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
                                adapter.clear()
                                adapter.notifyDataSetChanged()
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
        get() = 1
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
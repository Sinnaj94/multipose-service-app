package de.jannis_jahr.motioncapturingapp.ui.home

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.lifecycle.observe
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import de.jannis_jahr.motioncapturingapp.R
import de.jannis_jahr.motioncapturingapp.preferences.ApplicationConstants
import de.jannis_jahr.motioncapturingapp.ui.adapters.JobsAdapter
import de.jannis_jahr.motioncapturingapp.ui.jobs.JobsViewModel
import de.jannis_jahr.motioncapturingapp.ui.view_holders.JobViewHolder
import de.jannis_jahr.motioncapturingapp.utils.NetworkUtils
import kotlinx.android.synthetic.main.activity_send_job.*
import kotlinx.android.synthetic.main.fragment_finished_jobs.*
import kotlinx.android.synthetic.main.fragment_home.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener {

    private lateinit var jobsViewModel: JobsViewModel
    private lateinit var jobList : ListView
    lateinit var myJobs : ArrayList<JobViewHolder>
    private var tagList: ArrayList<String> = arrayListOf()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val sharedPrefs = requireContext().getSharedPreferences(
            ApplicationConstants.PREFERENCES, Context.MODE_PRIVATE
        )
        setHasOptionsMenu(true)
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

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.filter -> {
                showFilters()
            }
        }
        return true
    }

    fun showFilters() {
        val builder: AlertDialog.Builder? = activity?.let {
            AlertDialog.Builder(it)
        }

        val v = layoutInflater.inflate(R.layout.tag_manager, null)
        val tags = v.findViewById<ChipGroup>(R.id.tags)
        val job_tag = v.findViewById<EditText>(R.id.job_tag)
        val add_tag = v.findViewById<ImageButton>(R.id.add_tag)
        //stags.addView(tags)
        builder!!.setTitle("Add filters")
                .setPositiveButton("Apply"
                ) { _, _ ->
                    jobsViewModel.loadJobs(tagList)
                }
                .setView(v)

        add_tag.setOnClickListener {
            addTag(job_tag, tags)
        }

        job_tag.setOnKeyListener { v, keyCode, event ->
            if(event.action == KeyEvent.ACTION_DOWN &&
                    keyCode == KeyEvent.KEYCODE_ENTER) {
                addTag(job_tag, tags)
                return@setOnKeyListener false
            }
            true
        }


        val dialog: AlertDialog? = builder.create()

        for(tag in tagList) {
            val chip = Chip(tags.context)
            chip.text = tag
            chip.isCloseIconVisible = true
            chip.setOnCloseIconClickListener {
                tags.removeView(it)
                tagList.remove(tag)
            }
            tags.addView(chip)
        }
        dialog!!.show()
    }

    fun addTag(job_tag: EditText, tags: ChipGroup) {
        if(!job_tag.text.isEmpty()) {
            val addedText = job_tag.text.toString().toLowerCase().trim()
            if(!tagList.contains(addedText)) {
                val chip = Chip(tags.context)
                chip.text = addedText
                chip.isCloseIconVisible = true
                chip.setOnCloseIconClickListener {
                    tags.removeView(it)
                    tagList.remove(addedText)
                }
                tagList.add(addedText)
                tags.addView(chip)
                job_tag.text.clear()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        pull_refresh_jobs.isRefreshing = true
        jobsViewModel.isRefreshing.observe(viewLifecycleOwner) {
            pull_refresh_jobs.isRefreshing = it
        }
        pull_refresh_jobs.setOnRefreshListener(this)
    }

    override fun onRefresh() {
        jobsViewModel.loadJobs(tagList)
    }
}
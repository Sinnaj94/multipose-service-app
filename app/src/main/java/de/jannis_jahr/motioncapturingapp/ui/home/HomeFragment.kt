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

class HomeFragment : Fragment(), SwipeRefreshLayout.OnRefreshListener, JobListTagsObserver{

    private lateinit var jobsViewModel: JobsViewModel
    private lateinit var jobList : ListView
    lateinit var myJobs : ArrayList<JobViewHolder>
    var textCardCount : TextView? = null
    private lateinit var tagList: TagList
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val sharedPrefs = requireContext().getSharedPreferences(
            ApplicationConstants.PREFERENCES, Context.MODE_PRIVATE
        )
        tagList = TagList(sharedPrefs)
        setHasOptionsMenu(true)
        val manager = NetworkUtils.getService(sharedPrefs)
        jobsViewModel = JobsViewModel(sharedPrefs, JobsRequestType.JOBS, null, tagList)
        val v = inflater.inflate(R.layout.fragment_home, container, false)
        jobList = v.findViewById(R.id.posts_list)
        myJobs = arrayListOf<JobViewHolder>()
        val adapter = JobsAdapter(requireContext(), R.layout.big_list_item_jobs, myJobs, this)
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

    /* https://stackoverflow.com/questions/43194243/notification-badge-on-action-item-android */
    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.home_menu, menu)
        val menuItem = menu.findItem(R.id.filter)

        val actionView = menuItem.actionView
        textCardCount = actionView.findViewById(R.id.cart_badge)
        badgeSetup()

        actionView.setOnClickListener {
            onOptionsItemSelected(menuItem)
        }
    }

    private fun badgeSetup() {
        if(tagList.isEmpty()) {
            textCardCount?.visibility = View.GONE
        } else {
            textCardCount?.visibility = View.VISIBLE
            textCardCount?.text = tagList.size.toString()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.filter -> {
                showFilters()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
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
            addTag(job_tag.text.toString(), tags)
            job_tag.text.clear()
        }

        job_tag.setOnKeyListener { v, keyCode, event ->
            if(event.action == KeyEvent.ACTION_DOWN &&
                    keyCode == KeyEvent.KEYCODE_ENTER) {
                addTag(job_tag.text.toString(), tags)
                job_tag.text.clear()
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
                badgeSetup()
            }
            tags.addView(chip)
        }
        dialog!!.show()
    }

    fun convertTag(job_tag: String): String? {
        if(!job_tag.isEmpty()) {
            val addedText = job_tag.toString().toLowerCase().trim()
            return addedText
        }
        return null
    }

    fun addTag(job_tag: String, tags: ChipGroup) {
        val addedText = convertTag(job_tag) ?: return
        if(tagList.contains(addedText)) return
        val chip = Chip(tags.context)
        chip.text = addedText
        chip.isCloseIconVisible = true
        chip.setOnCloseIconClickListener {
            tags.removeView(it)
            tagList.remove(addedText)
            badgeSetup()
        }
        tagList.add(addedText)
        tags.addView(chip)
        badgeSetup()
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

    override fun notifyAdd(tag: String) {
        val conv = convertTag(tag)?:return
        if(tagList.contains(conv)) return
        tagList.add(conv)
        badgeSetup()
        jobsViewModel.loadJobs(tagList)
    }

    override fun notifyRemove(tag: String) {
        Log.d("FILTERING", tag)
        val conv = convertTag(tag)?:return
        tagList.remove(conv)
        badgeSetup()
        jobsViewModel.loadJobs(tagList)
    }

    class TagList(val sharedPreferences: SharedPreferences) : ArrayList<String>() {
        init {
            addAll(sharedPreferences.getStringSet(
                    ApplicationConstants.TAGS,
                    setOf()
            )!!.toTypedArray())
        }
        override fun add(element: String): Boolean {
            val edit = sharedPreferences.edit()
            val bool = super.add(element)
            edit.putStringSet(ApplicationConstants.TAGS, this.toMutableSet())
            edit.apply()
            return bool
        }

        override fun remove(element: String): Boolean {
            val bool = super.remove(element)
            val edit = sharedPreferences.edit()
            edit.putStringSet(ApplicationConstants.TAGS, this.toMutableSet())
            edit.apply()
            return bool
        }
    }
}
package de.jannis_jahr.motioncapturingapp.ui.adapters

import android.app.AlertDialog
import android.content.Context
import android.content.SharedPreferences
import android.graphics.drawable.Drawable
import android.media.MediaParser
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.FragmentTransaction
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import de.jannis_jahr.motioncapturingapp.R
import de.jannis_jahr.motioncapturingapp.network.services.authentication.BasicAuthorization
import de.jannis_jahr.motioncapturingapp.network.services.model.Bookmark
import de.jannis_jahr.motioncapturingapp.network.services.model.BookmarkStatus
import de.jannis_jahr.motioncapturingapp.network.services.model.Job
import de.jannis_jahr.motioncapturingapp.preferences.ApplicationConstants
import de.jannis_jahr.motioncapturingapp.ui.SendToAnimationDialogFragment
import de.jannis_jahr.motioncapturingapp.ui.view.JobListTagsObservable
import de.jannis_jahr.motioncapturingapp.ui.view.JobListTagsObserver
import de.jannis_jahr.motioncapturingapp.ui.view_holders.JobViewHolder
import de.jannis_jahr.motioncapturingapp.utils.NetworkUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.PrintWriter
import java.net.Socket
import java.text.SimpleDateFormat
import kotlin.system.exitProcess

class JobsAdapter(
        context: Context, resource: Int, list: ArrayList<JobViewHolder>, override var observer: JobListTagsObserver?
) : ArrayAdapter<JobViewHolder>(context, resource, list), JobListTagsObservable {
    var resource: Int
    var list: ArrayList<JobViewHolder>
    var vi: LayoutInflater


    internal class ViewHolder(view: View?) {
        var image : ImageView? = view?.findViewById(R.id.thumbnail)
        var date : TextView? = view?.findViewById(R.id.job_date)
        var name : TextView? = view?.findViewById(R.id.job_name)
        var progressBar : ProgressBar? = view?.findViewById(R.id.progress_job)
        var progressStage: TextView? = view?.findViewById(R.id.job_stage)
        var problem: ImageButton? = view?.findViewById(R.id.job_problem)
        var progressIcon: ImageView? = view?.findViewById(R.id.job_icon)
        var username: TextView? = view?.findViewById(R.id.user_name)
        var webView: WebView? = view?.findViewById(R.id.webView)
        var bookmarkView: ImageView? = view?.findViewById(R.id.bookmark_view)
        var bookmarkState = false
        var description: TextView? = view?.findViewById(R.id.my_job_description)
        var tags: ChipGroup? = view?.findViewById(R.id.tag_group)
        var isPublicSwitch: Switch? = view?.findViewById(R.id.is_public)
        var bookmarked: Chip? = view?.findViewById(R.id.bookmark_chip)
        var sendTo3D: ImageButton? = view?.findViewById(R.id.send_to_3d)
    }



    @RequiresApi(Build.VERSION_CODES.O)
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val holder: ViewHolder
        val rowView: View?

        if(convertView == null) {
            rowView = vi.inflate(resource, null)
            holder = ViewHolder(rowView)
            rowView.tag = holder
        } else {
            rowView = convertView
            holder = rowView.tag as ViewHolder
        }
        val jv = list[position]
        val formatter = SimpleDateFormat("dd.MM.yyyy HH:mm");
        holder.date?.text = formatter.format(jv.job.date_updated)
        holder.name?.text = jv.job.name
        val resultCode = jv.job.result.result_code
        when (resultCode) {
            2 -> {
                holder.progressIcon!!.setImageResource(R.drawable.ic_baseline_hourglass_bottom_24)
            }
            0 -> {
                holder.progressIcon!!.setImageResource(R.drawable.ic_baseline_sync_24)
            }
            1 -> {
                holder.progressIcon!!.setImageResource(R.drawable.ic_baseline_check_circle_24)
            }
            -1 -> {
                holder.progressIcon!!.setImageResource(R.drawable.ic_baseline_report_problem_24)
            }
        }
        bindImage(position = jv.job, thumb = holder)
        if(holder.webView != null) {
            holder.webView?.settings!!.javaScriptEnabled = true
            holder.webView?.settings!!.domStorageEnabled = true
            holder.webView?.settings!!.databaseEnabled = true
            val myHost = NetworkUtils.getHost(context.getSharedPreferences(ApplicationConstants.PREFERENCES, Context.MODE_PRIVATE))
            holder.webView?.loadUrl("$myHost${ApplicationConstants.BASE_ROUTE}results/${jv.job.id}/render_html")

        }

        if(holder.bookmarkView != null) {
            holder.bookmarkView!!.setOnClickListener {
                holder.bookmarkState = !holder.bookmarkState
                when(holder.bookmarkState) {
                    true -> {
                        holder.bookmarkView!!.setImageResource(R.drawable.ic_baseline_bookmark_24)
                        DrawableCompat.setTint(
                            DrawableCompat.wrap(holder.bookmarkView!!.drawable),
                            ContextCompat.getColor(context, R.color.colorAccent)
                        )
                    }
                    false -> {
                        holder.bookmarkView!!.setImageResource(R.drawable.ic_baseline_bookmark_border_24)
                        DrawableCompat.setTint(
                            DrawableCompat.wrap(holder.bookmarkView!!.drawable),
                            ContextCompat.getColor(context, android.R.color.darker_gray)
                        )
                    }
                }
            }
        }

        if(holder.username != null) {
            holder.username!!.text = jv.job.user.username
        }

        if(holder.description != null) {
            holder.description!!.text = jv.job.description
        }
        if(holder.tags != null) {
            val sharedPrefs = context.getSharedPreferences(
                    ApplicationConstants.PREFERENCES, Context.MODE_PRIVATE
            )
            val array = sharedPrefs.getStringSet(
                    ApplicationConstants.TAGS,
                    setOf())
            holder.tags!!.removeAllViews()
            if(jv.job.tags.isEmpty()) {
                holder.tags!!.visibility = View.GONE
            } else {
                holder.tags!!.visibility = View.VISIBLE
            }
            for(t in jv.job.tags) {
                val chip = Chip(holder.tags!!.context)
                chip.text = t.text
                chip.isCheckable = true
                if(array!!.contains(t.text)) {
                    chip.isChecked = true
                }
                chip.setOnCheckedChangeListener { _, isChecked ->
                    if(isChecked) {
                        observer?.notifyAdd(chip.text.toString())
                    } else {
                        observer?.notifyRemove(chip.text.toString())
                    }
                }
                holder.tags!!.addView(chip)
            }

        }

        if(holder.isPublicSwitch != null) {
            holder.isPublicSwitch!!.isChecked = jv.job.public
            val s = NetworkUtils.getService(context.getSharedPreferences(ApplicationConstants.PREFERENCES,
                    Context.MODE_PRIVATE))
            holder.isPublicSwitch!!.setOnCheckedChangeListener { buttonView, isChecked ->
                if(isChecked) {
                    val call = s!!.postJob(jv.job.id.toString())
                    call.enqueue(object: Callback<Job> {
                        override fun onFailure(call: Call<Job>, t: Throwable) {
                            holder.isPublicSwitch!!.isChecked = false
                        }

                        override fun onResponse(call: Call<Job>, response: Response<Job>) {
                            if(response.code() == 200) {
                                Toast.makeText(context, "Job is public now.", Toast.LENGTH_LONG).show()
                            } else {
                                holder.isPublicSwitch!!.isChecked = false
                            }
                        }

                    })
                } else {
                    val call = s!!.deleteJobPost(jv.job.id.toString())
                    call.enqueue(object: Callback<Job> {
                        override fun onFailure(call: Call<Job>, t: Throwable) {
                            holder.isPublicSwitch!!.isChecked = true
                        }

                        override fun onResponse(call: Call<Job>, response: Response<Job>) {
                            if (response.code() == 200) {
                                Toast.makeText(context, "Job is private now.", Toast.LENGTH_LONG).show()
                            } else {
                                holder.isPublicSwitch!!.isChecked = true
                            }
                        }
                    })
                }
            }
        }

        if(holder.bookmarked != null) {
            holder.bookmarked!!.isChecked = jv.job.bookmarked
            holder.bookmarked!!.text = jv.job.num_bookmarks.toString()
            holder.bookmarked!!.setOnCheckedChangeListener { _, isChecked ->
                val s = NetworkUtils.getService(context.getSharedPreferences(ApplicationConstants.PREFERENCES,
                        Context.MODE_PRIVATE))
                if(isChecked) {
                    val call = s!!.postBookmark(jv.job.id.toString())
                    call.enqueue(object: Callback<BookmarkStatus> {
                        override fun onFailure(call: Call<BookmarkStatus>, t: Throwable) {
                        }

                        override fun onResponse(call: Call<BookmarkStatus>, response: Response<BookmarkStatus>) {
                            if(response.code() == 200) {
                                holder.bookmarked!!.text = response.body()!!.count.toString()
                            }
                        }

                    })
                } else {
                    val call = s!!.deleteBookmark(jv.job.id.toString())
                    call.enqueue(object: Callback<BookmarkStatus> {
                        override fun onFailure(call: Call<BookmarkStatus>, t: Throwable) {
                        }

                        override fun onResponse(call: Call<BookmarkStatus>, response: Response<BookmarkStatus>) {
                            if(response.code() == 200) {
                                if(response.isSuccessful) {
                                    holder.bookmarked!!.text = response.body()!!.count.toString()
                                }
                            }
                        }

                    })
                }

            }
        }

        if(holder.sendTo3D != null) {
            holder.sendTo3D!!.setOnClickListener {
                val fm = (context as AppCompatActivity).supportFragmentManager
                val ft = fm.beginTransaction()
                val prev = fm.findFragmentByTag("dialog")
                if(prev != null) {
                    ft.remove(prev)
                }
                val f = SendToAnimationDialogFragment()

                val args = Bundle()
                args.putString("url", jv.job.result.output_bvh)
                f.arguments = args

                f.show(ft, "dialog")
            }

        }

        return rowView!!
    }



    private fun bindImage(position: Job, thumb: ViewHolder) {
        val jv = list.first {
            it.job.id == position.id
        }

        jv.imageBinderHandler = Handler(Looper.getMainLooper())
        val runnable = Runnable {
            bindImage(position, thumb)
        }

        val sharedPreferences = context.getSharedPreferences("users", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", "")
        val host = sharedPreferences.getString("hostname", "")
        val thumbnailPath = host + jv.job.thumbnail_url
        var retries = 4
        val fullThumbnailUrl = GlideUrl(
            thumbnailPath,
            LazyHeaders.Builder()
                .addHeader("Authorization", BasicAuthorization(token,"").buildHeader()!!)
                .build()
        )

        Glide
            .with(context)
            .load(fullThumbnailUrl)
            .transition(DrawableTransitionOptions.withCrossFade())
            .listener(object: RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    retries--
                    if(retries > 0) {
                        jv.imageBinderHandler.postDelayed(runnable, 1000)
                        Log.d("FAILED TO LOAD","Attempt again")
                    }
                    return true
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    return false
                }

            }).into(thumb.image!!)
    }

    init {
        this.resource = resource
        this.vi = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        this.list = list
    }

    private val inflater: LayoutInflater
            = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

    override fun getCount(): Int {
        return list.size
    }

    override fun getItem(p0: Int): JobViewHolder? {
        return list[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

    override fun clear() {
        super.clear()
    }



}
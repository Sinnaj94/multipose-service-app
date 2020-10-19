package de.jannis_jahr.motioncapturingapp.ui.adapters

import android.content.Context
import android.graphics.drawable.Drawable
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import de.jannis_jahr.motioncapturingapp.R
import de.jannis_jahr.motioncapturingapp.network.services.authentication.BasicAuthorization
import de.jannis_jahr.motioncapturingapp.network.services.model.Job
import de.jannis_jahr.motioncapturingapp.preferences.ApplicationConstants
import de.jannis_jahr.motioncapturingapp.ui.view_holders.JobViewHolder
import de.jannis_jahr.motioncapturingapp.utils.NetworkUtils
import java.text.SimpleDateFormat

class JobsAdapter(
    context: Context, resource: Int, list: ArrayList<JobViewHolder>
) : ArrayAdapter<JobViewHolder>(context, resource, list) {
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
        var webView: WebView? = view?.findViewById(R.id.webView)
        var bookmarkView: ImageView? = view?.findViewById(R.id.bookmark_view)
        var bookmarkState = false
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
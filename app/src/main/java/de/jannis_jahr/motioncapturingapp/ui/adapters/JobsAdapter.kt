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
import de.jannis_jahr.motioncapturingapp.network.services.model.Result
import android.widget.*
import androidx.annotation.RequiresApi
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
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

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
        if(resultCode == 2 || resultCode == 0) {
            when(resultCode) {
                0 -> holder.progressIcon!!.setImageResource(R.drawable.ic_baseline_sync_24)
                2 -> holder.progressIcon!!.setImageResource(R.drawable.ic_baseline_hourglass_bottom_24)
            }
            val service = NetworkUtils.getService(context.getSharedPreferences(ApplicationConstants.PREFERENCES, Context.MODE_PRIVATE))
            jv.progressHandler.post(object : Runnable {
                override fun run() {
                    val currentHandler = this
                    val call = service!!.getResult(jv.job.id.toString())
                    call.enqueue(object : Callback<Result> {
                        @RequiresApi(Build.VERSION_CODES.N)
                        override fun onResponse(call: Call<Result>, response: Response<Result>) {
                            //holder.problem!!.visibility = View.GONE
                            //holder.progressBar?.visibility = View.VISIBLE

                            if(response.code() == 200) {
                                val result = response.body()!!
                                when(result.result_code) {
                                    -1 -> holder.progressIcon!!.setImageResource(R.drawable.ic_baseline_report_problem_24)
                                    0 -> {
                                        holder.progressIcon!!.setImageResource(R.drawable.ic_baseline_sync_24)
                                        jv.progressHandler.postDelayed(currentHandler, 1000)
                                    }
                                    1 -> holder.progressIcon!!.setImageResource(R.drawable.ic_baseline_check_circle_24)
                                    2 -> {
                                        holder.progressIcon!!.setImageResource(R.drawable.ic_baseline_hourglass_bottom_24)
                                        jv.progressHandler.postDelayed(currentHandler, 1000)
                                    }
                                }
                            }
                        }

                        override fun onFailure(call: Call<Result>, t: Throwable) {
                            // TODO
                        }

                    })
                }
            })
        } else if(resultCode == 1) {
            holder.progressIcon!!.setImageResource(R.drawable.ic_baseline_check_circle_24)
        } else if(resultCode == -1) {
            holder.progressIcon!!.setImageResource(R.drawable.ic_baseline_report_problem_24)
        }
        if(jv.job.video_uploaded) {
            bindImage(position, holder.image!!)
        }

        return rowView!!
    }


    fun bindImage(position: Int, thumb: ImageView) {
        val jv = list[position]

        jv.imageBinderHandler = Handler(Looper.getMainLooper())
        val runnable = Runnable {
            bindImage(position, thumb)
        }

        val sharedPreferences = context.getSharedPreferences("users", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", "")
        val host = sharedPreferences.getString("hostname", "")
        val thumbnailPath = host + list[position].job.thumbnail_url
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

            })
            .into(thumb)
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

    override fun notifyDataSetChanged() {
        this.setNotifyOnChange(false);
        sort { o1, o2 ->
            o2.job.date_updated.compareTo(o1.job.date_updated)
        }
        this.setNotifyOnChange(true);
    }

    override fun clear() {
        super.clear()
    }
}
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
import de.jannis_jahr.motioncapturingapp.network.services.model.Status
import de.jannis_jahr.motioncapturingapp.preferences.ApplicationConstants
import de.jannis_jahr.motioncapturingapp.ui.view_holders.JobViewHolder
import de.jannis_jahr.motioncapturingapp.utils.NetworkUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

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
    }



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

        holder.date?.text = jv.job.date_updated
        holder.name?.text = jv.job.name
        val resultCode = jv.job.result.result_code
        if(resultCode == 0) {
            if(!jv.job.video_uploaded) {
                holder.progressStage!!.text = "uploading"
            }
            val service = NetworkUtils.getService(context.getSharedPreferences(ApplicationConstants.PREFERENCES, Context.MODE_PRIVATE))
            jv.progressHandler.post(object : Runnable {
                override fun run() {
                    val currentHandler = this
                    val call = service!!.getJobStatus(jv.job.id.toString())
                    call.enqueue(object : Callback<Status> {
                        @RequiresApi(Build.VERSION_CODES.N)
                        override fun onResponse(call: Call<Status>, response: Response<Status>) {
                            holder.problem!!.visibility = View.GONE
                            holder.progressBar?.visibility = View.VISIBLE

                            if(response.code() == 200) {
                                val status = response.body()!!
                                if (!status.finished) {
                                    if(status.problem) {
                                        holder.problem!!.visibility = View.VISIBLE
                                        holder.progressBar!!.visibility = View.GONE
                                        holder.progressStage!!.text = "Action required"
                                    } else {
                                        holder.progressStage!!.visibility = View.VISIBLE
                                        holder.progressBar!!.visibility = View.VISIBLE
                                        holder.progressStage!!.text = status.stage.name
                                        if(status.stage.progress == null) {
                                            holder.progressBar!!.isIndeterminate = true
                                        } else {
                                            holder.progressBar!!.isIndeterminate = false
                                            holder.progressBar!!.max = 100
                                            holder.progressBar!!.setProgress((status.stage.progress * 100).toInt(), true)
                                        }
                                        jv.progressHandler.postDelayed(currentHandler, 1000)
                                    }
                                } else {
                                    holder.progressStage!!.text = "finished"
                                    holder.progressBar!!.visibility = View.INVISIBLE
                                    Toast.makeText(context, "Yeah, Job ${jv.job.name} finished", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }

                        override fun onFailure(call: Call<Status>, t: Throwable) {
                            // TODO
                        }

                    })
                }
            })
        } else {
            holder.progressBar?.visibility = View.INVISIBLE
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
                    jv.imageBinderHandler.postDelayed(runnable, 1000)
                    Log.d("FAILED TO LOAD","Attempt again")
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

    override fun clear() {
        super.clear()
    }
}
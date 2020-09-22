package de.jannis_jahr.motioncapturingapp.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import de.jannis_jahr.motioncapturingapp.R
import de.jannis_jahr.motioncapturingapp.network.services.authentication.BasicAuthorization
import de.jannis_jahr.motioncapturingapp.network.services.model.Job

class JobsAdapter(
    context: Context, resource: Int, list: ArrayList<Job>
) : ArrayAdapter<Job>(context, resource, list) {
    var resource: Int
    var list: ArrayList<Job>
    var vi: LayoutInflater

    internal class ViewHolder(view: View?) {
        var image : ImageView? = view?.findViewById(R.id.thumbnail)
        var date : TextView? = view?.findViewById(R.id.job_date)
        var name : TextView? = view?.findViewById(R.id.job_name)
        var result : TextView? = view?.findViewById(R.id.job_status)
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

        holder.date?.text = list[position].date_updated
        holder.name?.text = list[position].name
        holder.result?.text = list[position].result.result_code.toString()
        if(list[position].video_uploaded) {
            bindImage(position, holder.image!!)
        }
        return rowView!!
    }
    
    fun bindImage(position: Int, thumb: ImageView) {
        val sharedPreferences = context.getSharedPreferences("users", Context.MODE_PRIVATE)
        val token = sharedPreferences.getString("token", "")
        val host = sharedPreferences.getString("hostname", "")
        val thumbnailPath = host + list[position].thumbnail_url
        val fullThumbnailUrl = GlideUrl(
            thumbnailPath,
            LazyHeaders.Builder()
                .addHeader("Authorization", BasicAuthorization(token,"").buildHeader()!!)
                .build()
        )

        Glide
            .with(context)
            .load(fullThumbnailUrl)
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

    override fun getItem(p0: Int): Job? {
        return list[p0]
    }

    override fun getItemId(p0: Int): Long {
        return p0.toLong()
    }

}
package de.jannis_jahr.motioncapturingapp.ui.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.BaseAdapter
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import de.jannis_jahr.motioncapturingapp.R
import de.jannis_jahr.motioncapturingapp.network.services.model.Job

class JobsAdapter(
    context: Context, resource: Int, list: ArrayList<Job>
) : ArrayAdapter<Job>(context, resource, list) {
    var resource: Int
    var list: ArrayList<Job>
    var vi: LayoutInflater

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var retView: View

        if(convertView == null) {
            retView = vi.inflate(resource, null)
            retView.findViewById<TextView>(R.id.job_date).text = list[position].date_updated
            retView.findViewById<TextView>(R.id.job_name).text = list[position].id
            // TODO: Results index 0 ?
            retView.findViewById<TextView>(R.id.job_status).text = list[position].results[0].result_code
        } else {
            retView = convertView
        }

        return retView
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
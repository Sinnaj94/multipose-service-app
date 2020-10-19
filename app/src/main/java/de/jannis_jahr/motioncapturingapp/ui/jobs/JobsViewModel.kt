package de.jannis_jahr.motioncapturingapp.ui.jobs

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.jannis_jahr.motioncapturingapp.network.services.model.Job
import de.jannis_jahr.motioncapturingapp.utils.NetworkUtils.Companion.getService
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class JobsViewModel(
    private val sharedPreferences: SharedPreferences,
    private val posts: Boolean = false,
    private val resultCode: Int?) : ViewModel(){

    companion object {
        const val TAG = "DASHBOARD_VIEW_MODEL"
    }


    // Example: https://developer.android.com/topic/libraries/architecture/viewmodel
    private val jobs: MutableLiveData<List<Job>> by lazy {
        MutableLiveData<List<Job>>().also {
            loadJobs()
        }
    }

    val isRefreshing: MutableLiveData<Boolean> = MutableLiveData<Boolean>()

    fun getJobs(): LiveData<List<Job>> {
        return jobs
    }

    public fun loadJobs() {
        isRefreshing.value = true
        // Do an asynchronous operation to fetch users.
        val request = getService(sharedPreferences)

        val call = if(posts) {
            request!!.getPosts()
        } else {
            request!!.getJobs(resultCode)
        }

        call.enqueue(object: Callback<List<Job>> {
            override fun onResponse(call: Call<List<Job>>, response: Response<List<Job>>) {
                isRefreshing.value = false
                if(response.code() == 200) {
                    jobs.value = response.body()
                    Log.d(TAG, jobs.value.toString())
                }
            }

            override fun onFailure(call: Call<List<Job>>, t: Throwable) {
                isRefreshing.value = false
                t.printStackTrace()
            }

        })
    }

}

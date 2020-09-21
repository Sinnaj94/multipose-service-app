package de.jannis_jahr.motioncapturingapp.ui.dashboard

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import de.jannis_jahr.motioncapturingapp.network.services.MocapService
import de.jannis_jahr.motioncapturingapp.network.services.authentication.BasicAuthInterceptor
import de.jannis_jahr.motioncapturingapp.network.services.helpers.HostnameSharedPreferenceLiveData
import de.jannis_jahr.motioncapturingapp.network.services.helpers.TokenSharedPreferenceLiveData
import de.jannis_jahr.motioncapturingapp.network.services.model.Job
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.reflect.KProperty

class DashboardViewModel(private val sharedPreferences : SharedPreferences) : ViewModel() {

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
        // Get the token
        val token = sharedPreferences.getString("token", "")
        val host = sharedPreferences.getString("hostname", "")
        val client = OkHttpClient.Builder()
            .addInterceptor(BasicAuthInterceptor(token!!, ""))
            .build()
        val request = Retrofit.Builder()
            .baseUrl(host!!)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(MocapService::class.java)

        val call = request.getJobs()

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
            }

        })
    }
}

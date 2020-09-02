package de.jannis_jahr.motioncapturingapp.network

import android.util.Log
import de.jannis_jahr.motioncapturingapp.network.services.MocapService
import de.jannis_jahr.motioncapturingapp.network.services.ServiceBuilder
import de.jannis_jahr.motioncapturingapp.network.services.model.Status
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket

class NetworkDiscovery : Runnable {
    var serverRange = "192.168.178."
    var port = 5000
    var running = true
    val id = "motion_capturing_api"
    var listener : NetworkDiscoveryListener? = null
    override fun run() {
        scanSubNet(serverRange)
    }

    private fun scanSubNet(subnet: String) {
        val hosts = ArrayList<String>()
        var inetAddress: InetAddress? = null
        Log.d(TAG, "Starting a scan.")
        for (i in 1..255) {
            if(running) {
                listener?.notifyProgress(i)
                val adr = InetSocketAddress("$subnet$i", port)
                val socket = Socket()

                try {
                    socket.connect(adr, 200)
                    Log.d(TAG,"Connected to $adr")
                    validateNetwork(adr)
                } catch (e: Exception) {
                    Log.d(TAG,"$adr")
                }
            }
        }
    }

    private fun validateNetwork(adr: InetSocketAddress): Boolean {
        // TODO: Persist the address on connect.
        val host = "http://${adr.address}:${adr.port}"

        ServiceBuilder.buildRetrofit(host)
        Log.d(TAG, "Host: $host")
        val request = ServiceBuilder.buildService(MocapService::class.java)

        val call = request.getStatus()

        call.enqueue(object : Callback<Status>{
            override fun onResponse(call: Call<Status>, response: Response<Status>) {
                if (response.isSuccessful){
                    // Notify listener
                    if(response.body()?.id == id) {
                        listener?.notify(host)
                        running = false
                    }
                }
            }
            override fun onFailure(call: Call<Status>, t: Throwable) {
                Log.d(TAG,"Failure")
            }
        })
        return false
    }

    companion object {
        const val TAG = "NETWORK_DISCOVERY"
    }

}
package de.jannis_jahr.motioncapturingapp.network

import android.util.Log
import de.jannis_jahr.motioncapturingapp.network.services.MocapService
import de.jannis_jahr.motioncapturingapp.network.services.ServiceBuilder
import de.jannis_jahr.motioncapturingapp.network.services.model.APIStatus
import de.jannis_jahr.motioncapturingapp.preferences.ApplicationConstants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.net.InetAddress
import java.net.InetSocketAddress
import java.net.Socket

/**
 * Searches for an open port 80 in local network, with a given ID
 */
class NetworkDiscovery : Runnable {
    var serverRange = "192.168.178."
    var port = 80
    var running = true
    val id = "motion_capturing_api"
    var listener : NetworkDiscoveryListener? = null
    override fun run() {
        scanSubNet(serverRange)
    }

    private fun scanSubNet(subnet: String) {
        // Scan range 192.168.178.1 - 255
        val hosts = ArrayList<String>()
        var inetAddress: InetAddress? = null
        Log.d(TAG, "Starting a scan.")
        for (i in 1..255) {
            if(running) {
                // Notify any listeners about the current progress
                listener?.notifyProgress(i)
                val adr = InetSocketAddress("$subnet$i", port)
                val socket = Socket()

                try {
                    // Try to connect via socket
                    socket.connect(adr, 100)
                    Log.d(TAG,"Connected to $adr")
                    // Validate the network with the id
                    validateNetwork(adr)
                } catch (e: Exception) {
                    Log.d(TAG,"$adr")
                }
            }
        }
    }

    /**
     * Validates a network by calling the api
     */
    private fun validateNetwork(adr: InetSocketAddress): Boolean {
        val host = "http:/${adr.address}"
        val fullHost = "${host}${ApplicationConstants.BASE_ROUTE}"

        ServiceBuilder.buildRetrofit(fullHost)
        Log.d(TAG, "Host: $fullHost")
        val request = ServiceBuilder.buildService(MocapService::class.java)

        // Try to make a call to the api
        val call = request.getStatus()

        call.enqueue(object : Callback<APIStatus>{
            override fun onResponse(call: Call<APIStatus>, response: Response<APIStatus>) {
                if (response.isSuccessful){
                    // Notify listener
                    if(response.body()?.id == id) {
                        // The API was found!
                        listener?.notify(host)
                        running = false
                    }
                }
            }
            override fun onFailure(call: Call<APIStatus>, t: Throwable) {
                // This is another network with port 80 opened
                Log.d(TAG,"Failure")
            }
        })
        return false
    }

    companion object {
        const val TAG = "NETWORK_DISCOVERY"
    }

}
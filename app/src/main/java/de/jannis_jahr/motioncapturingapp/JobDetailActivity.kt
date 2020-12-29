package de.jannis_jahr.motioncapturingapp

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.navigation.*
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import de.jannis_jahr.motioncapturingapp.R
import de.jannis_jahr.motioncapturingapp.network.services.model.Job
import de.jannis_jahr.motioncapturingapp.preferences.ApplicationConstants
import de.jannis_jahr.motioncapturingapp.utils.NetworkUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * @deprecated
 * This is the Job Detail Activity which is opened when a job is clicked
 */
class JobDetailActivity : AppCompatActivity() {
    lateinit var navController:NavController
    var id : Int? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        title = intent.getStringExtra("title")!!
        id = intent.getIntExtra("id", -1)!!
        setContentView(R.layout.activity_job_detail)

        // Find the bottom navigation view, which can switch between fragments via
        val navView: BottomNavigationView = findViewById(R.id.nav_view_detail)
        val navController = findNavController(R.id.nav_host_fragment_detail)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_source_video, R.id.navigation_output_video
            )
        )

        // Inflate the navigation view
        val inflater = navController.navInflater
        val graph = inflater.inflate(R.navigation.mobile_navigation_detail)

        // Get the ID which was passed in an INTENT object
        val idArgument = NavArgument.Builder().setDefaultValue(intent.getIntExtra("id", -1)).build()
        graph.addArgument("id", idArgument)
        navController.graph = graph

        // Setup the action bar with the navigation controller
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        // Pass the id and title to the next fragment when navigation is changed
        navController.addOnDestinationChangedListener { _: NavController, dest: NavDestination, _: Bundle? ->
            dest.addArgument("id", idArgument)
            title = intent.getStringExtra("title")!!
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.setDisplayShowHomeEnabled(true)
        }

        // Show the back icon
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onBackPressed() {
        finish()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.job_detail_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> finish()
            R.id.post -> post()
        }
        return true
    }

    /**
     * Make the Job Public via API
     */
    fun post() {
        val sp = NetworkUtils.getService(getSharedPreferences(ApplicationConstants.PREFERENCES, Context.MODE_PRIVATE))
        val call = sp!!.postJob(id!!)
        call.enqueue(object : Callback<Job> {
            override fun onFailure(call: Call<Job>, t: Throwable) {

            }

            override fun onResponse(call: Call<Job>, response: Response<Job>) {
                if(response.code() == 200) {
                    Toast.makeText(applicationContext, "${response.body()!!.name} is now public.",
                    Toast.LENGTH_LONG).show()
                }
            }
        })
    }
}
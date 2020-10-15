package de.jannis_jahr.motioncapturingapp.ui.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.navigation.*
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import de.jannis_jahr.motioncapturingapp.R

class JobDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_job_detail)

        val navView: BottomNavigationView = findViewById(R.id.nav_view_detail)

        val navController = findNavController(R.id.nav_host_fragment_detail)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_source_video, R.id.navigation_output_video, R.id.navigation_operations
            )
        )
        val inflater = navController.navInflater
        val graph = inflater.inflate(R.navigation.mobile_navigation_detail)
        val idArgument = NavArgument.Builder().setDefaultValue(intent.getStringExtra("id")!!).build()
        graph.addArgument("id", idArgument)
        navController.graph = graph
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        navController.addOnDestinationChangedListener { _: NavController, dest: NavDestination, _: Bundle? ->
            dest.addArgument("id", idArgument)
        }


        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> finish()
        }
        return true
    }
}
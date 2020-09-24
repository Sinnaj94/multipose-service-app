package de.jannis_jahr.motioncapturingapp.ui.detail

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.navigation.NavGraph
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import de.jannis_jahr.motioncapturingapp.R

class JobDetailActivity : AppCompatActivity() {
    lateinit var id: String

    public fun getID() : String {
        return id
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_job_detail)

        val navView: BottomNavigationView = findViewById(R.id.nav_view_detail)

        val navController = findNavController(R.id.nav_host_fragment_detail)
        navController.setGraph(R.navigation.mobile_navigation_detail, intent.extras)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_user
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
        id = intent.getStringExtra("id")!!
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            android.R.id.home -> finish()
        }
        return true
    }
}
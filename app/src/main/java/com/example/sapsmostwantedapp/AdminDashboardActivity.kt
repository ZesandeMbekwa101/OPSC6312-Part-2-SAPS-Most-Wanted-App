package com.example.sapsmostwantedapp

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.sapsmostwantedapp.ui.fragments.ActivityLogsFragment
import com.example.sapsmostwantedapp.ui.fragments.AdminDashboardFragment
import com.example.sapsmostwantedapp.ui.fragments.ReportsViewFragment
import com.example.sapsmostwantedapp.ui.fragments.UserManagementFragment
import com.example.sapsmostwantedapp.ui.fragments.UserReportsFragment
import com.example.sapsmostwantedapp.ui.fragments.WantedPersonNotificationsFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AdminDashboardActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView
    private var adminUsername: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_admin_dashboard)

        adminUsername = intent.getStringExtra("admin_username")

        // Setup toolbar
        val toolbar = findViewById<androidx.appcompat.widget.Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = "Admin Dashboard"

        bottomNav = findViewById(R.id.adminBottomNav)

        // Load default fragment
        loadFragment(AdminDashboardFragment())

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_admin_dashboard -> {
                    loadFragment(UserManagementFragment())
                    true
                }
                R.id.nav_users -> {
                    loadFragment(UserManagementFragment())
                    true
                }
                R.id.nav_reports -> {
                    loadFragment(ReportsViewFragment())
                    true
                }
                R.id.nav_user_reports -> {
                    loadFragment(UserReportsFragment())
                    true
                }
                R.id.nav_wanted_notifications -> {
                    loadFragment(WantedPersonNotificationsFragment())
                    true
                }


                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.adminFragmentContainer, fragment)
            .commit()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.admin_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menu_logout -> {
                val intent = Intent(this, MainActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intent)
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}


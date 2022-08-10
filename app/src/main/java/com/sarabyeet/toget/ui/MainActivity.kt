package com.sarabyeet.toget.ui

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI.setupWithNavController
import androidx.navigation.ui.setupActionBarWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.sarabyeet.toget.R
import com.sarabyeet.toget.arch.ToGetViewModel
import com.sarabyeet.toget.db.AppDatabase

class MainActivity : AppCompatActivity() {

    lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController

        // Set up ViewModel
        val viewModel: ToGetViewModel by viewModels()
        viewModel.init(AppDatabase.getDatabase(this))

        // Set up top level destinations
        appBarConfiguration = AppBarConfiguration(setOf(
            R.id.homeFragment,
            R.id.profileFragment
        ))
        // Set up app top bar
        setupActionBarWithNavController(navController, appBarConfiguration)

        // Set up bottom bar
        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottomNavigation)
        setupWithNavController(bottomNavigationView, navController)

        // Adding destination changed listener to show/hide bottom bar
        navController.addOnDestinationChangedListener{ _: NavController, navDestination: NavDestination, _: Bundle? ->

            if (appBarConfiguration.topLevelDestinations.contains(navDestination.id)){
                bottomNavigationView.isVisible = true
            } else {
                bottomNavigationView.isGone = true
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}
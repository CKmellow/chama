package com.example.chamapp

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import com.example.chamapp.databinding.ActivityMainBinding
import com.example.chamapp.util.SessionManager
import com.google.android.material.navigation.NavigationView

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMainBinding
    private lateinit var sessionManager: SessionManager
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        sessionManager = SessionManager(this)

        setSupportActionBar(binding.appBarMain.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        navController = findNavController(R.id.nav_host_fragment_content_main)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            val authDestinations = setOf(
                R.id.splashFragment,
                R.id.welcomeFragment,
                R.id.loginFragment,
                R.id.signUpFragment,
                R.id.passwordResetSuccessFragment,
                R.id.resetPasswordEmailFragment
            )
            if (destination.id in authDestinations) {
                binding.appBarMain.toolbar.visibility = View.GONE
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
            } else {
                binding.appBarMain.toolbar.visibility = View.VISIBLE
                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
            }
        }

        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.homeFragment, R.id.nav_gallery, R.id.nav_slideshow, R.id.nav_transactions
            ), drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)

        // Set the custom listener to handle the special logout case.
        // This avoids conflicts with other setup methods.
        navView.setNavigationItemSelectedListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle the special case for logout
        if (item.itemId == R.id.nav_logout) {
            sessionManager.clearAuthToken()
            sessionManager.clearFirstName()
            navController.navigate(R.id.action_global_auth_nav)
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            return true
        }

        // For all other items, delegate to NavigationUI to handle the navigation.
        val handled = NavigationUI.onNavDestinationSelected(item, navController)
        if (handled) {
            // If NavigationUI handled it, close the drawer.
            binding.drawerLayout.closeDrawer(GravityCompat.START)
        }

        return handled
    }
}

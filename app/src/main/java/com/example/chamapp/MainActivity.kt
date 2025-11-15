package com.example.chamapp

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.chamapp.databinding.ActivityMainBinding
import com.example.chamapp.util.SessionManager

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // --- Setup NavController ---
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment_content_main) as androidx.navigation.fragment.NavHostFragment
        navController = navHostFragment.navController

        /*
        // --- Setup Bottom Navigation ---
        binding.bottomNavView.setupWithNavController(navController)

        // --- Floating Action Button Click Listener ---
        binding.fabCreateChama.setOnClickListener {
            navController.navigate(R.id.createChamaFragment)
        }

        // --- Handle Bottom Navigation Item Clicks for Logout ---
        binding.bottomNavView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_logout -> {
                    // Perform logout
                    val sessionManager = SessionManager(this)
                    sessionManager.clearSession()
                    // Navigate to the auth flow and clear back stack
                    navController.navigate(R.id.auth_nav) { // Assuming your auth flow is a nested graph
                        popUpTo(R.id.mobile_navigation) {
                            inclusive = true
                        }
                    }
                    true // Consume the event
                }
                // Handle other navigation items automatically
                else -> {
                    // Let the NavController handle the navigation for other items
                    navController.navigate(item.itemId)
                    true
                }
            }
        }

        // --- Show/Hide Bottom Bar based on Destination ---
        navController.addOnDestinationChangedListener { _, destination, _ ->
            val authDestinations = setOf(
                R.id.splashFragment,
                R.id.welcomeFragment,
                R.id.loginFragment,
                R.id.signUpFragment
                // Add any other auth-related fragments here
            )

            if (destination.id in authDestinations) {
                binding.bottomAppBar.visibility = View.GONE
                binding.fabCreateChama.visibility = View.GONE
            } else {
                binding.bottomAppBar.visibility = View.VISIBLE
                binding.fabCreateChama.visibility = View.VISIBLE
            }
        }
        */
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}

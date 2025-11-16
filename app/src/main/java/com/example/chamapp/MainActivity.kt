package com.example.chamapp

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.findNavController
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

        // --- Remove bottomNavView and fabCreateChama setup ---
        // Navigation logic should be handled via sidebar (drawer)
        // If you have a DrawerLayout and NavigationView, set them up here
        // Example:
        // binding.navView.setupWithNavController(navController)
        // Add hamburger menu logic if needed
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp() || super.onSupportNavigateUp()
    }
}

package com.example.chamapp.ui.auth

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.example.chamapp.R
import com.example.chamapp.api.LoginRequest
import com.example.chamapp.api.RetrofitClient
import com.example.chamapp.databinding.FragmentLoginBinding
import kotlinx.coroutines.launch

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Handle login
        binding.btnSignIn.setOnClickListener {
            val email = binding.etEmail?.text.toString().trim()
            val password = binding.etPassword?.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        // Navigate to Sign Up
        binding.tvSignUpLink.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }
    }

    private fun loginUser(email: String, password: String) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.getInstance(null).login(LoginRequest(email, password))
                if (response.isSuccessful && response.body() != null) {
                    val authResponse = response.body()!!
                    val token = authResponse.access_token

                    // Defensive check: Ensure the token is not null or blank
                    if (token.isNullOrBlank()) {
                        Toast.makeText(requireContext(), "Login successful, but authentication token was missing.", Toast.LENGTH_LONG).show()
                        return@launch // Stop execution here
                    }

                    // *** DEBUGGING: SHOW THE TOKEN ***
                    Toast.makeText(requireContext(), "Saving token: $token", Toast.LENGTH_LONG).show()

                    val user = authResponse.user
                    
                    // Save user name and the validated access token to SharedPreferences
                    val prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
                    val success = prefs.edit()
                        .putString("first_name", user?.first_name ?: "")
                        .putString("access_token", token)
                        .commit() // Use commit() to save synchronously

                    if (success) {
                        // Navigate to Home on successful login AND successful save
                        findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                    } else {
                        Toast.makeText(requireContext(), "Failed to save session. Please try again.", Toast.LENGTH_LONG).show()
                    }

                } else {
                    val errorBody = response.errorBody()?.string()
                    Toast.makeText(requireContext(), "Login failed: $errorBody", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

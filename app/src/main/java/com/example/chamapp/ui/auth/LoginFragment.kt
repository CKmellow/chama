package com.example.chamapp.ui.auth

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
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
            android.util.Log.d("LoginFragment", "Sign In button clicked with email: $email, password: $password")
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
// In LoginFragment.kt

    private fun loginUser(email: String, password: String) {
    android.util.Log.d("LoginFragment", "loginUser called with email: $email, password: $password")
    val sessionManager = com.example.chamapp.util.SessionManager(com.example.chamapp.App.appContext)
        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.instance.login(LoginRequest(email, password))

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        android.util.Log.d("LoginFragment", "Full login response body: $responseBody")
                        if (responseBody?.access_token != null) {
                            android.util.Log.d("LoginFragment", "Received token from backend: ${responseBody.access_token}")
                            sessionManager.saveAuthToken(responseBody.access_token)
                            val savedToken = sessionManager.getAuthToken()
                            android.util.Log.d("LoginFragment", "Token saved to SharedPreferences: $savedToken")
                        }
                        val authResponse = response.body()
                        val user = authResponse?.user // Then get the user from the body

                        // It's also a good idea to save the token here
                        val token = authResponse?.access_token
                        if (token != null) {
                            // Assuming you have a SessionManager like we discussed before
                            // val sessionManager = SessionManager(requireContext())
                            // sessionManager.saveAuthToken(token)
                        }

                        Toast.makeText(requireContext(), "Welcome ${user?.first_name}", Toast.LENGTH_SHORT).show()
                        findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                    } else {
                        // Handle non-successful (but not exception) responses, like 401 Unauthorized
                        val errorBody = response.errorBody()?.string()
                        Toast.makeText(requireContext(), "Login failed: $errorBody", Toast.LENGTH_LONG).show()
                    }
                }
            } catch (e: Exception) {
                // This block will now catch network errors (no connection)
                withContext(Dispatchers.Main) {
                    val errorMessage = when (e) {
                        is java.net.UnknownHostException -> "No internet connection"
                        else -> "An unexpected error occurred: ${e.message}"
                    }
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }



    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

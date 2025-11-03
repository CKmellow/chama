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
import com.example.chamapp.util.SessionManager
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

        // Handle login button click
        binding.btnSignIn.setOnClickListener {
            val email = binding.etEmail?.text.toString().trim()
            val password = binding.etPassword?.text.toString().trim()

            android.util.Log.d("LoginFragment", "Sign In clicked: email=$email")

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        // Navigate to Sign Up page
        binding.tvSignUpLink.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }
    }

    private fun loginUser(email: String, password: String) {
        val sessionManager = SessionManager(requireContext())

        lifecycleScope.launch(Dispatchers.IO) {
            try {
                val response = RetrofitClient.instance.login(LoginRequest(email, password))

                withContext(Dispatchers.Main) {
                    if (response.isSuccessful) {
                        val authResponse = response.body()
                        android.util.Log.d("LoginFragment", "Login response: $authResponse")

                        val token = authResponse?.access_token
                        val user = authResponse?.user

                        if (!token.isNullOrBlank()) {
                            sessionManager.saveAuthToken(token)
                            android.util.Log.d("LoginFragment", "Token saved: $token")
                        }

                        Toast.makeText(
                            requireContext(),
                            "Welcome ${user?.first_name ?: "User"}!",
                            Toast.LENGTH_SHORT
                        ).show()

                        // Navigate to Home screen
                        findNavController().navigate(R.id.action_loginFragment_to_homeFragment)
                    } else {
                        val error = response.errorBody()?.string()
                        Toast.makeText(requireContext(), "Login failed: $error", Toast.LENGTH_LONG).show()
                        android.util.Log.e("LoginFragment", "Login failed: $error")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    val errorMessage = when (e) {
                        is java.net.UnknownHostException -> "No internet connection"
                        else -> "Unexpected error: ${e.message}"
                    }
                    Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_SHORT).show()
                    android.util.Log.e("LoginFragment", "Error: ${e.message}", e)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

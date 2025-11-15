package com.example.chamapp.ui.auth

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.chamapp.R
import com.example.chamapp.data.AuthResult
import com.example.chamapp.databinding.FragmentLoginBinding
import com.example.chamapp.util.SessionManager

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSignIn.setOnClickListener {
            val email = binding.etEmail?.text?.toString()?.trim() ?: ""
            val password = binding.etPassword?.text?.toString()?.trim() ?: ""

            if (email.isNotEmpty() && password.isNotEmpty()) {
                authViewModel.login(email, password)
            } else {
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        binding.tvSignUpLink.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }

        authViewModel.loginResult.observe(viewLifecycleOwner) { result ->
            binding.progressBar?.isVisible = false
            binding.btnSignIn.isEnabled = true

            when (result) {
                is AuthResult.Loading -> {
                    binding.progressBar?.isVisible = true
                    binding.btnSignIn.isEnabled = false
                }

                is AuthResult.Success -> {
                    val user = result.data?.user
                    val name = user?.first_name ?: "User"

                    // Save token & user data (from remote functionality)
                    val sessionManager = SessionManager(requireContext())
                    sessionManager.saveAuthToken(result.data?.access_token ?: "")

                    val prefs = requireContext()
                        .getSharedPreferences("user_prefs", Context.MODE_PRIVATE)

                    user?.id?.let { prefs.edit().putString("user_id", it).apply() }
                    prefs.edit().putString("first_name", name).apply()

                    Toast.makeText(
                        requireContext(),
                        "Login Successful! Welcome $name",
                        Toast.LENGTH_LONG
                    ).show()

                    findNavController().navigate(R.id.action_loginFragment_to_main_app_nav)
                }

                is AuthResult.Error -> {
                    Toast.makeText(
                        requireContext(),
                        "Login Failed: ${result.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

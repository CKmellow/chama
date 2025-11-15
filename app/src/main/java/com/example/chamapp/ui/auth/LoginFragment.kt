package com.example.chamapp.ui.auth

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
                    val name = result.data?.user?.first_name ?: "User"
                    Toast.makeText(requireContext(), "Login Successful! Welcome $name", Toast.LENGTH_LONG).show()
                    findNavController().navigate(R.id.action_loginFragment_to_main_app_nav)
                }
                is AuthResult.Error -> {
                    // Show detailed error message and log it
                    val apiMessage = if (result is AuthResult.Error && result.message != null) result.message else "Unknown error"
                    Toast.makeText(requireContext(), "Login Failed: $apiMessage", Toast.LENGTH_LONG).show()
                    android.util.Log.e("LoginFragment", "Login failed: $apiMessage")
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

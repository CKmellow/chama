package com.example.chamapp.ui.auth

import android.os.Bundle
import android.text.SpannableString
import android.text.style.UnderlineSpan
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.chamapp.R
import com.example.chamapp.databinding.FragmentSignUpBinding

class SignUpFragment : Fragment() {

    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Make the "Sign In" text underlined
        val signInText = SpannableString("Sign in")
        signInText.setSpan(UnderlineSpan(), 0, signInText.length, 0)
        binding.tvSignInLink.text = signInText

        // Set up click listeners for navigation
        binding.tvSignInLink.setOnClickListener {
            // Navigate back to the Login screen
            findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
        }

        binding.btnSignUp.setOnClickListener {
            if (validateInput()) {
                val fullName = binding.etFullName.text.toString().trim()
                val email = binding.etEmail.text.toString().trim()
                val phoneNumber = "+254${binding.etPhoneNumber.text.toString().trim()}"
                val password = binding.etPassword.text.toString()
                viewModel.registerUser(fullName, email, phoneNumber, password)
            }
        }

        binding.ivBackButton.setOnClickListener {
            // Go back to the previous screen (Welcome)
            findNavController().popBackStack()
        }
    }

    private fun validateInput(): Boolean {
        val fullName = binding.etFullName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val phoneNumber = binding.etPhoneNumber.text.toString().trim()
        val password = binding.etPassword.text.toString()
        val verifyPassword = binding.etVerifyPassword.text.toString()

        if (fullName.isEmpty()) {
            binding.tilFullName.error = "Full name is required"
            return false
        } else {
            binding.tilFullName.error = null
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = "Invalid email format"
            return false
        } else {
            binding.tilEmail.error = null
        }

        val kenyanPhoneRegex = "^7[0-9]{8}$".toRegex()
        if (!kenyanPhoneRegex.matches(phoneNumber)) {
            binding.tilPhoneNumber.error = "Invalid Kenyan phone number"
            return false
        } else {
            binding.tilPhoneNumber.error = null
        }

        val passwordRegex = "^(?=.*[0-9])(?=.*[a-zA-Z])(?=.*[@#$%^&+=!]).{8,}$".toRegex()
        if (!passwordRegex.matches(password)) {
            binding.tilPassword.error = "Password must be 8+ characters with letters, numbers, and special characters"
            return false
        } else {
            binding.tilPassword.error = null
        }

        if (password != verifyPassword) {
            binding.tilVerifyPassword.error = "Passwords do not match"
            return false
        } else {
            binding.tilVerifyPassword.error = null
        }

        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
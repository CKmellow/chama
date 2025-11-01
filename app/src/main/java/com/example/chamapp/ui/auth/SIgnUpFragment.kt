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

        // Underline "Sign In"
        val signInText = SpannableString("Sign in")
        signInText.setSpan(UnderlineSpan(), 0, signInText.length, 0)
        binding.tvSignInLink.text = signInText

        // Navigate to login
        binding.tvSignInLink.setOnClickListener {
            findNavController().navigate(R.id.action_signUpFragment_to_loginFragment)
        }

        binding.btnSignUp.setOnClickListener {
            if (validateInput()) {
                val firstName = binding.etFirstName.text.toString().trim()
                val lastName = binding.etLastName.text.toString().trim()
                val email = binding.etEmail.text.toString().trim()
                val phoneNumber = binding.etPhoneNumber.text.toString().trim()
                val password = binding.etPassword.text.toString()

                viewModel.registerUser(firstName, lastName, email, phoneNumber, password)
            }
        }

        binding.ivBackButton.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun validateInput(): Boolean {
        val firstName = binding.etFirstName.text.toString().trim()
        val lastName = binding.etLastName.text.toString().trim()
        val email = binding.etEmail.text.toString().trim()
        val phoneNumber = binding.etPhoneNumber.text.toString().trim()
        val password = binding.etPassword.text.toString()
        val verifyPassword = binding.etVerifyPassword.text.toString()

        if (firstName.isEmpty()) {
            binding.tilFirstName.error = "First name is required"
            return false
        } else binding.tilFirstName.error = null

        if (lastName.isEmpty()) {
            binding.tilLastName.error = "Last name is required"
            return false
        } else binding.tilLastName.error = null

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmail.error = "Invalid email format"
            return false
        } else binding.tilEmail.error = null

        // Phone number: only check not empty
        if (phoneNumber.isEmpty()) {
            binding.tilPhoneNumber.error = "Phone number is required"
            return false
        } else binding.tilPhoneNumber.error = null

        // Password: accept anything
        if (password.isEmpty()) {
            binding.tilPassword.error = "Password is required"
            return false
        } else binding.tilPassword.error = null

        if (password != verifyPassword) {
            binding.tilVerifyPassword.error = "Passwords do not match"
            return false
        } else binding.tilVerifyPassword.error = null

        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

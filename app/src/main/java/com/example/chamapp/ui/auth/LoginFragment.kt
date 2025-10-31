package com.example.chamapp.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.chamapp.R
import com.example.chamapp.databinding.FragmentLoginBinding

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

        // Navigate to the main app when the sign-in button is clicked
        binding.btnSignIn.setOnClickListener { // FIXED: Changed from btnLogin to btnSignIn
            // TODO: Add validation logic here later
            // For now, let's assume login is successful and navigate to the main part of the app.
            // Replace 'action_global_main_flow' with your actual action ID to go to the main screen.
            // findNavController().navigate(R.id.action_global_main_flow)
        }

        // Navigate to the sign-up page
        binding.tvSignUpLink.setOnClickListener { // FIXED: Changed from tvGoToSignUp to tvSignUpLink
            findNavController().navigate(R.id.action_loginFragment_to_signUpFragment)
        }

        binding.tvForgotPassword.setOnClickListener {
            findNavController().navigate(R.id.action_loginFragment_to_resetPasswordEmailFragment)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

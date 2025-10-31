package com.example.chamapp.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.chamapp.R
import com.example.chamapp.databinding.FragmentWelcomeBinding

class WelcomeFragment : Fragment() {

    private var _binding: FragmentWelcomeBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentWelcomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        animateElements()
        setupClickListeners()
    }

    private fun animateElements() {
        // Animation for Icon Container
        binding.cvIconContainer.alpha = 0f
        binding.cvIconContainer.animate()
            .alpha(1f)
            .setDuration(800)
            .setStartDelay(200)
            .start()

        // Animation for Welcome Title
        binding.tvWelcome.translationY = 50f
        binding.tvWelcome.alpha = 0f
        binding.tvWelcome.animate()
            .translationY(0f)
            .alpha(1f)
            .setDuration(600)
            .setStartDelay(400)
            .start()

        // Animation for Subtitle
        binding.tvSubtitle.translationY = 50f
        binding.tvSubtitle.alpha = 0f
        binding.tvSubtitle.animate()
            .translationY(0f)
            .alpha(1f)
            .setDuration(600)
            .setStartDelay(500)
            .start()

        // Animate the "Create Account" button
        binding.btnGoToSignUp.translationY = 50f
        binding.btnGoToSignUp.alpha = 0f
        binding.btnGoToSignUp.animate()
            .translationY(0f)
            .alpha(1f)
            .setDuration(600)
            .setStartDelay(600)
            .start()

        // Animate the "Login" button
        binding.btnGoToLogin.translationY = 50f
        binding.btnGoToLogin.alpha = 0f
        binding.btnGoToLogin.animate()
            .translationY(0f)
            .alpha(1f)
            .setDuration(600)
            .setStartDelay(700) // Staggered start for a nice effect
            .start()
    }

    private fun setupClickListeners() {
        // "Login" button navigates to LoginFragment
        binding.btnGoToLogin.setOnClickListener {
            findNavController().navigate(R.id.action_welcomeFragment_to_loginFragment)
        }

        // "Create Account" button navigates to SignUpFragment
        binding.btnGoToSignUp.setOnClickListener {
            findNavController().navigate(R.id.action_welcomeFragment_to_signUpFragment)
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Important to avoid memory leaks
    }
}

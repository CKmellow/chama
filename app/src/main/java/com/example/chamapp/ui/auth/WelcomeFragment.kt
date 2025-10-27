package com.example.chamapp.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.graphics.alpha
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
        // Animation logic is the same as before
        binding.cvIconContainer.alpha = 0f
        binding.cvIconContainer.animate()
            .alpha(1f)
            .setDuration(800)
            .setStartDelay(200)
            .start()

        binding.tvWelcome.translationY = 50f
        binding.tvWelcome.alpha = 0f
        binding.tvWelcome.animate()
            .translationY(0f)
            .alpha(1f)
            .setDuration(600)
            .setStartDelay(400)
            .start()

        binding.tvSubtitle.translationY = 50f
        binding.tvSubtitle.alpha = 0f
        binding.tvSubtitle.animate()
            .translationY(0f)
            .alpha(1f)
            .setDuration(600)
            .setStartDelay(500)
            .start()

        binding.btnLogin.translationY = 50f
        binding.btnLogin.alpha = 0f
        binding.btnLogin.animate()
            .translationY(0f)
            .alpha(1f)
            .setDuration(600)
            .setStartDelay(600)
            .start()

        binding.tvDeleteAccount.alpha = 0f
        binding.tvDeleteAccount.animate()
            .alpha(1f)
            .setDuration(600)
            .setStartDelay(800)
            .start()
    }

    private fun setupClickListeners() {
        binding.btnLogin.setOnClickListener {
            // When login is clicked, navigate to the main app graph
            // This assumes the user is now "logged in"
            // We'll replace this with a real login screen later
            findNavController().navigate(R.id.action_global_mobile_navigation)
        }

        binding.tvDeleteAccount.setOnClickListener {
            Toast.makeText(requireContext(), "Account deletion feature coming soon", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Important to avoid memory leaks
    }
}

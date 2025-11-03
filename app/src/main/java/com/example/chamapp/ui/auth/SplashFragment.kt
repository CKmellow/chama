package com.example.chamapp.ui.auth

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.chamapp.R
import com.example.chamapp.databinding.FragmentSplashBinding

class SplashFragment : Fragment() {

    private var _binding: FragmentSplashBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSplashBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Motivational quotes, fun facts, or tips (no emojis)
        val messages = listOf(
            "Tip: You can view all your chamas in one place!",
            "Small savings grow into big dreams.",
            "Fun Fact: Chamas have helped millions achieve their goals!",
            "The journey of a thousand miles begins with a single step.",
            "Remember: Consistency is the key to financial success!",
            "Have a bright and productive day!"
        )
        val randomMessage = messages.random()
        binding.tvMotivation.text = randomMessage

        // Load the bounce animation
        val bounceAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.logo_bounce)

        // Delay navigation to allow time to read the quote
        val splashDuration = 2500L // 2.5 seconds
        binding.logoCard.postDelayed({
            // Start the animation on the logo card
            binding.logoCard.startAnimation(bounceAnimation)
        }, 400)

        // Set an animation listener to navigate when the animation ends
        bounceAnimation.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                // Wait for the splash duration before navigating
                binding.logoCard.postDelayed({
                    findNavController().navigate(R.id.action_splashFragment_to_welcomeFragment)
                }, splashDuration)
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

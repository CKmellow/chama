package com.example.chamapp.ui.invest

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.chamapp.databinding.FragmentInvestBinding
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.PlayerConstants

class InvestFragment : Fragment() {
    private var _binding: FragmentInvestBinding? = null
    private val binding get() = _binding!!
    private lateinit var errorTextViews: List<TextView>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInvestBinding.inflate(inflater, container, false)
        // Add error TextViews for fallback
        errorTextViews = listOf(
            TextView(requireContext()),
            TextView(requireContext()),
            TextView(requireContext())
        )
        errorTextViews.forEach {
            it.visibility = View.GONE
            it.text = "This video is unavailable. Please try again later."
            it.setTextColor(resources.getColor(android.R.color.holo_red_dark, null))
            it.textSize = 16f
            it.setPadding(0, 32, 0, 32)
        }
        // Add errorTextViews below each player
        val rootLinear = binding.rootLinear
        rootLinear.addView(errorTextViews[0], 1)
        rootLinear.addView(errorTextViews[1], 3)
        rootLinear.addView(errorTextViews[2], 5)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupYouTubePlayers()
        binding.ivBackArrow.setOnClickListener {
            // Navigate back to HomeFragment
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupYouTubePlayers() {
        val videoIds = listOf(
            "P_dh8kysjbQ",  // How to Start Investing for Beginners (Nate O’Brien)
            "F3QpgXBtDeo",  // Stock Market for Beginners (Ryan Scribner)
            "pVja5eO2OB0"   // What is a Mutual Fund (Fidelity)
        )

//        val videoIds = listOf(
//            "wxyARuPQVnQ",
//            "h8uJ1Zj-HnI",
//            "RbHVK_vGrGk"
//        )
        val players = listOf(binding.youtubePlayer1, binding.youtubePlayer2, binding.youtubePlayer3)
        for (i in players.indices) {
            lifecycle.addObserver(players[i])
            players[i].addYouTubePlayerListener(object : AbstractYouTubePlayerListener() {
                override fun onReady(youTubePlayer: YouTubePlayer) {
                    youTubePlayer.cueVideo(videoIds[i], 0f)
                }
                override fun onError(youTubePlayer: YouTubePlayer, error: PlayerConstants.PlayerError) {
                    players[i].visibility = View.GONE
                    errorTextViews[i].visibility = View.VISIBLE
                    Toast.makeText(requireContext(), "This video is unavailable.", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

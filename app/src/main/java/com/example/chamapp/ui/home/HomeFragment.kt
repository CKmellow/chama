package com.example.chamapp.ui.home

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chamapp.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // Simulate user role - set to true to see the treasurer's view
    private val isTreasurer = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Header
        binding.tvGreeting.text = "Welcome back, John!"
        binding.tvDate.text = "Nov 2, 2025"
        binding.tvNotificationBadge.text = "3"
        // Profile photo and notification bell are static for now

        // Dummy chama data
        val chamas = listOf(
            Chama("Umoja Investment Group", 12),
            Chama("Safari Savings", 8),
            Chama("Jirani Welfare", 15)
        )
        val adapter = ChamaAdapter(chamas)
        binding.rvChamas.layoutManager = LinearLayoutManager(requireContext())
        binding.rvChamas.adapter = adapter

        // Quick stats (dummy)
        binding.tvTotalSavingsAmount.text = "KES 150,000"
        binding.tvActiveLoansValue.text = "2 loans - KES 30,000"
        binding.tvUpcomingVotesValue.text = "3 pending"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
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
            Chama(
                name = "Umoja Investment Group",
                role = "Treasurer",
                myContributions = "KES 45,000",
                totalBalance = "KES 850,000",
                status = "Up-to-date",
                statusColor = "#388E3C", // Green
                nextMeeting = "Nov 15, 2025"
            ),
            Chama(
                name = "Safari Savings",
                role = "Member",
                myContributions = "KES 20,000",
                totalBalance = "KES 400,000",
                status = "Payment due soon",
                statusColor = "#FFA000", // Orange
                nextMeeting = "Nov 20, 2025"
            ),
            Chama(
                name = "Jirani Welfare",
                role = "Secretary",
                myContributions = "KES 10,000",
                totalBalance = "KES 120,000",
                status = "In arrears",
                statusColor = "#D32F2F", // Red
                nextMeeting = "Nov 25, 2025"
            )
        )
        val adapter = ChamaAdapter(chamas) { chama ->
            // Navigate to ChamaDashboardFragment with chama details as arguments
            val navController = androidx.navigation.fragment.findNavController(this)
            val action = com.example.chamapp.ui.home.HomeFragmentDirections.actionHomeFragmentToChamaDashboardFragment(
                chamaName = chama.name,
                role = chama.role,
                myContributions = chama.myContributions,
                totalBalance = chama.totalBalance,
                status = chama.status,
                statusColor = chama.statusColor,
                nextMeeting = chama.nextMeeting
            )
            navController.navigate(action)
        }
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
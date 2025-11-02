package com.example.chamapp.ui.chama

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.chamapp.databinding.FragmentChamaDashboardBinding

class ChamaDashboardFragment : Fragment() {
    private var _binding: FragmentChamaDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChamaDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        // Fix: Use SafeArgs to get arguments, ensure SafeArgs is set up and project is rebuilt
        val args = com.example.chamapp.ui.chama.ChamaDashboardFragmentArgs.fromBundle(requireArguments())
        binding.tvChamaName.text = args.chamaName
        binding.tvUserRole.text = "Your Role: ${args.role}"
        binding.tvMembersActiveSince.text = "Members: 25 | Active Since: Jan 2024" // Example static, replace with real data
        binding.tvChamaBalance.text = "Total Chama Balance:    ${args.totalBalance}"
        binding.tvChamaTotalContributions.text = "Total Contributions:    KES 1,200,000" // Example static
        binding.tvChamaActiveLoans.text = "Active Loans:           KES 350,000" // Example static
        binding.tvChamaAvailableLoans.text = "Available for Loans:    KES 500,000" // Example static
        binding.tvMyTotalContributions.text = "My Total Contributions: ${args.myContributions}"
        binding.tvMyThisMonth.text = "This Month:            KES 5,000 ✅" // Example static
        binding.tvMyNextPaymentDue.text = "Next Payment Due:      Dec 1, 2025" // Example static
        binding.tvMyActiveLoan.text = "My Active Loan:        KES 15,000" // Example static
        binding.tvMyNextInstallment.text = "Next Installment:      KES 2,500 (Nov 10)" // Example static
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

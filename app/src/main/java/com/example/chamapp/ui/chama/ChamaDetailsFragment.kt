package com.example.chamapp.ui.chama

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.chamapp.databinding.FragmentChamaDetailsBinding
import androidx.fragment.app.viewModels
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.chamapp.api.RetrofitClient
import com.example.chamapp.api.DepositRequest
import kotlinx.coroutines.launch
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.navigation.fragment.findNavController
import com.example.chamapp.R

class ChamaDetailsFragment : Fragment() {

    private var _binding: FragmentChamaDetailsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ChamaDetailsViewModel by viewModels()

    private lateinit var membersAdapter: MembersAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChamaDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val chamaId = arguments?.getString("chamaId")
        binding.ivBackArrow.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        // Deposit button logic
        binding.llDeposit.setOnClickListener {
            val depositAmount = 100.0 // Example, replace with actual input
            initiateStkPush(chamaId, depositAmount)
        }
        // My Contribution Records button logic
        binding.cardRecordContribution.setOnClickListener {
            // Manual navigation fallback
            val bundle = Bundle().apply { putString("chamaId", chamaId) }
            findNavController().navigate(
                com.example.chamapp.R.id.action_chamaDetailsFragment_to_myContributionsFragment,
                bundle
            )
        }
        // View all members button logic
        binding.tvViewAllMembers.setOnClickListener {
            fetchChamaMembers(chamaId)
        }
        // Fetch total contributions on load
        fetchTotalContributions(chamaId)
        binding.tvChamaName.text = getString(R.string.chama_id_debug, chamaId ?: "-")
        Toast.makeText(requireContext(), "ChamaDetailsFragment loaded", Toast.LENGTH_SHORT).show()
        if (chamaId == null) {
            binding.tvChamaName.text = getString(R.string.chama_not_found)
            return
        }

        viewModel.fetchChamaDetails(chamaId)
        android.util.Log.d("ChamaDetailsFragment", "Called fetchChamaDetails($chamaId)")

        viewModel.chamaDetails.observe(viewLifecycleOwner) { chama ->
            if (chama != null) {
                binding.tvChamaName.text = chama.chama_name
                // Use available views only
                // binding.tvUserRole.text = "Your Role: ${chama.role ?: "Member"}" // If you have role info
                // binding.tvChamaBalance.text = "KES ${chama.total_balance ?: "-"}"
                // You can add more assignments here if you have matching views
                android.util.Log.d("ChamaDetailsFragment", "chama loaded: $chama")
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMsg ->
            if (errorMsg != null) {
                binding.tvChamaName.text = errorMsg
                android.util.Log.d("ChamaDetailsFragment", "Error loading chama: $errorMsg")
            }
        }

        binding.rvMembers.layoutManager = LinearLayoutManager(requireContext())
        membersAdapter = MembersAdapter(emptyList())
        binding.rvMembers.adapter = membersAdapter
    }

    private fun initiateStkPush(chamaId: String?, amount: Double) {
        if (chamaId == null) return
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.depositToChama(DepositRequest(chamaId, amount))
                if (response.isSuccessful && response.body()?.message != null) {
                    Toast.makeText(requireContext(), "Deposit initiated: ${response.body()?.message}", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(requireContext(), "Deposit failed: ${response.body()?.error ?: response.message()}", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Deposit error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun fetchTotalContributions(chamaId: String?) {
        if (chamaId == null) return
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.getChamaTotalContributions(chamaId)
                if (response.isSuccessful && response.body() != null) {
                    val total = response.body()!!.total_amount ?: 0.0
                    val count = response.body()!!.total_transactions ?: 0
                    binding.tvChamaBalance.text = getString(R.string.chama_balance_format, total)
                    binding.tvTotalTransactions.text = getString(R.string.transactions_count, count)
                } else {
                    binding.tvChamaBalance.text = getString(R.string.error)
                }
            } catch (e: Exception) {
                binding.tvChamaBalance.text = getString(R.string.error_with_message, e.message ?: "Unknown")
            }
        }
    }

    private fun fetchUserContributions(chamaId: String?) {
        if (chamaId == null) return
        val bundle = Bundle().apply { putString("chamaId", chamaId) }
        findNavController().navigate(R.id.myContributionsFragment, bundle)
    }

    private fun fetchChamaMembers(chamaId: String?) {
        if (chamaId == null) return
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.getChamaMembers(chamaId)
                if (response.isSuccessful && response.body()?.members != null) {
                    val members = response.body()!!.members!!
                    membersAdapter = MembersAdapter(members)
                    binding.rvMembers.adapter = membersAdapter
                    binding.rvMembers.visibility = View.VISIBLE
                } else {
                    Toast.makeText(requireContext(), "Failed to load members", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error loading members: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

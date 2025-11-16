package com.example.chamapp.ui.chama

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chamapp.R
import com.example.chamapp.api.DepositRequest
import com.example.chamapp.api.RetrofitClient
import com.example.chamapp.databinding.FragmentChamaDashboardBinding
import kotlinx.coroutines.launch

class ChamaDashboardFragment : Fragment() {
    private var _binding: FragmentChamaDashboardBinding? = null
    private val binding get() = _binding!!

    private var membersList: List<String> = emptyList()
    private lateinit var membersAdapter: MembersAdapter

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
        val args = ChamaDashboardFragmentArgs.fromBundle(requireArguments())
        val chamaId = args.chamaId // Ensure this is passed in navigation
        binding.tvChamaName.text = args.chamaName
        binding.tvUserRole.text = getString(R.string.chama_role_format, args.role)
        val balanceDouble = args.totalBalance.toDoubleOrNull() ?: 0.0
        binding.tvChamaBalance.text = getString(R.string.chama_balance_format, balanceDouble)
        binding.ivBackArrow.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // Deposit button logic
        binding.llDeposit.setOnClickListener {
            val inputView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_input_amount, null)
            val editText = inputView.findViewById<android.widget.EditText>(R.id.et_amount)
            AlertDialog.Builder(requireContext())
                .setTitle("Enter deposit amount")
                .setView(inputView)
                .setPositiveButton("Prompt") { dialog, _ ->
                    val amountStr = editText.text.toString()
                    val depositAmount = amountStr.toDoubleOrNull()
                    if (depositAmount != null && depositAmount > 0) {
                        initiateStkPush(chamaId, depositAmount)
                    } else {
                        Toast.makeText(requireContext(), "Enter a valid amount", Toast.LENGTH_SHORT).show()
                    }
                    dialog.dismiss()
                }
                .setNegativeButton("Cancel") { dialog, _ -> dialog.dismiss() }
                .show()
        }

        // My Contribution Records button logic
        binding.cardRecordContribution.setOnClickListener {
            fetchUserContributions(chamaId)
        }

        // View all members button logic
        binding.tvViewAllMembers.setOnClickListener {
            fetchChamaMembers(chamaId)
        }

        // RecyclerView setup
        binding.rvChamaMembers.layoutManager = LinearLayoutManager(requireContext())
        membersAdapter = MembersAdapter(emptyList())
        binding.rvChamaMembers.adapter = membersAdapter

        // Fetch total contributions on load
        fetchTotalContributions(chamaId)
    }

    private fun initiateStkPush(chamaId: String, amount: Double) {
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

    private fun fetchTotalContributions(chamaId: String) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.getChamaTotalContributions(chamaId)
                if (response.isSuccessful && response.body() != null) {
                    val total = response.body()!!.total_amount ?: 0.0
                    val count = response.body()!!.total_transactions ?: 0
                    binding.tvChamaBalance.text = getString(R.string.chama_balance_format, total)
                    binding.tvTotalTransactions.text = "Transactions: $count"
                } else {
                    binding.tvChamaBalance.text = "Error"
                }
            } catch (e: Exception) {
                binding.tvChamaBalance.text = "Error: ${e.message}"
            }
        }
    }

    private fun fetchUserContributions(chamaId: String) {
        // Navigate to MyContributionsFragment, pass chamaId
        val action = ChamaDashboardFragmentDirections.actionChamaDashboardFragmentToMyContributionsFragment(chamaId)
        findNavController().navigate(action)
    }

    private fun fetchChamaMembers(chamaId: String) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.getChamaMembers(chamaId)
                if (response.isSuccessful && response.body()?.members != null) {
                    val members = response.body()!!.members!!
                    membersAdapter = MembersAdapter(members)
                    binding.rvChamaMembers.adapter = membersAdapter
                    binding.rvChamaMembers.visibility = View.VISIBLE
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

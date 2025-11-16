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
import android.app.AlertDialog
import android.widget.TextView

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
        val chamaName = arguments?.getString("chamaName")
        binding.ivBackArrow.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        // Deposit button logic
        binding.llDeposit.setOnClickListener {
            val inputView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_input_amount, null)
            val editText = inputView.findViewById<android.widget.EditText>(R.id.etAmount)
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
            val bundle = Bundle().apply { putString("chamaId", chamaId) }
            findNavController().navigate(
                com.example.chamapp.R.id.action_chamaDetailsFragment_to_myContributionsFragment,
                bundle
            )
        }
        fetchTotalContributions(chamaId)
        binding.tvChamaName.text = chamaName ?: getString(R.string.chama_not_found)
        if (chamaId == null) {
            binding.tvChamaName.text = getString(R.string.chama_not_found)
            return
        }
        binding.rvChamaMembers.layoutManager = LinearLayoutManager(requireContext())
        membersAdapter = MembersAdapter(emptyList()) { member ->
            showMemberDetailsDialog(member)
        }
        binding.rvChamaMembers.adapter = membersAdapter
        fetchChamaMembers(chamaId)
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
                    val body = response.body()!!
                    if (body.error != null) {
                        binding.tvChamaBalance.text = getString(R.string.error_with_message, body.error)
//                        binding.tvTotalTransactions.text = getString(R.string.transactions_count, 0)
                    } else {
                        val totalAmount = body.total_amount ?: 0.0
                        val totalTransactions = body.total_transactions ?: 0
                        binding.tvChamaBalance.text = getString(R.string.chama_balance_format, totalAmount)
//                        binding.tvTotalTransactions.text = getString(R.string.transactions_count, totalTransactions)
                    }
                } else {
                    binding.tvChamaBalance.text = getString(R.string.error)
//                    binding.tvTotalTransactions.text = getString(R.string.transactions_count, 0)
                }
            } catch (e: Exception) {
                binding.tvChamaBalance.text = getString(R.string.error_with_message, e.message ?: "Unknown")
//                binding.tvTotalTransactions.text = getString(R.string.transactions_count, 0)
            }
        }
    }

    private fun fetchUserContributions(chamaId: String?) {
        if (chamaId == null) return
        val bundle = Bundle().apply { putString("chamaId", chamaId) }
        findNavController().navigate(R.id.myContributionsFragment, bundle)
    }

    private fun fetchChamaMembers(chamaId: String) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.getChamaMembers(chamaId)
                if (response.isSuccessful && response.body()?.members != null) {
                    val members = response.body()!!.members!!
                    membersAdapter = MembersAdapter(members) { member ->
                        showMemberDetailsDialog(member)
                    }
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

    private fun showMemberDetailsDialog(member: com.example.chamapp.api.ChamaMemberRelation) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_member_details, null)
        dialogView.findViewById<TextView>(R.id.tv_member_name).text = member.name ?: ""
        dialogView.findViewById<TextView>(R.id.tv_member_role).text = member.role ?: ""
        dialogView.findViewById<TextView>(R.id.tv_member_email).text = member.email ?: ""
        dialogView.findViewById<TextView>(R.id.tv_member_phone).text = member.phoneNumber?.toString() ?: ""
        dialogView.findViewById<TextView>(R.id.tv_member_joined).text = member.joinedAt ?: ""
        dialogView.findViewById<TextView>(R.id.tv_member_status).text = member.status ?: ""
        AlertDialog.Builder(requireContext())
            .setTitle("Member Details")
            .setView(dialogView)
            .setPositiveButton("Close") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

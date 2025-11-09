package com.example.chamapp.ui.chama

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.chamapp.databinding.FragmentChamaDetailsBinding
import androidx.fragment.app.viewModels
import com.example.chamapp.ui.chama.ChamaDetailsViewModel

class ChamaDetailsFragment : Fragment() {

    private var _binding: FragmentChamaDetailsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChamaDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    private val viewModel: ChamaDetailsViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get chamaId from SafeArgs (update this if using SafeArgs)
        val chamaId = arguments?.getString("chamaId")
        if (chamaId == null) {
            binding.tvChamaName.text = "Chama not found"
            return
        }

        viewModel.fetchChamaDetails(chamaId)

        viewModel.chama.observe(viewLifecycleOwner) { chama ->
            if (chama != null) {
                binding.tvChamaName.text = chama.chama_name
                binding.tvChamaType.text = "Type: ${chama.chama_type ?: "-"}"
                binding.tvCreationDate.text = "Created: ${chama.created_at ?: "-"}"
                binding.tvMemberCount.text = "Members: " // TODO: Show actual member count
                binding.tvContributionAmount.text = "Contribution: KES ${chama.monthly_contribution_amount ?: "-"}"
                binding.tvContributionSchedule.text = "Schedule: ${chama.contribution_frequency ?: "-"}"
                binding.tvInterestRate.text = "Interest Rate: ${chama.loan_interest_rate ?: "-"}%"
                binding.tvMaxLoanAmount.text = "Max Loan: ${chama.max_loan_multiplier ?: "-"}x Savings"
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            if (errorMsg != null) {
                binding.tvChamaName.text = errorMsg
            }
        }

        // Set up button listeners
        binding.btnViewMembers.setOnClickListener {
            // TODO: Navigate to members list
        }
        binding.btnViewContributions.setOnClickListener {
            // TODO: Navigate to contributions history
        }
        binding.btnViewReports.setOnClickListener {
            // TODO: Navigate to financial reports
        }
        binding.btnEditChama.setOnClickListener {
            val dialogView = layoutInflater.inflate(com.example.chamapp.R.layout.dialog_edit_user_details, null)
            val etContribution = dialogView.findViewById<android.widget.EditText>(com.example.chamapp.R.id.etContributionAmount)
            val etRole = dialogView.findViewById<android.widget.EditText>(com.example.chamapp.R.id.etRole)

            androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setTitle("Edit My Details")
                .setView(dialogView)
                .setPositiveButton("Save") { _, _ ->
                    val newContribution = etContribution.text.toString().toDoubleOrNull()
                    val newRole = etRole.text.toString()
                    val chamaId = arguments?.getString("chamaId") ?: return@setPositiveButton
                    val prefs = requireContext().getSharedPreferences("user_prefs", android.content.Context.MODE_PRIVATE)
                    val userId = prefs.getString("user_id", null) ?: return@setPositiveButton
                    viewModel.updateMemberDetails(chamaId, userId, newContribution, newRole)
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        viewModel.updateResult.observe(viewLifecycleOwner) { resultMsg ->
            resultMsg?.let {
                android.widget.Toast.makeText(requireContext(), it, android.widget.Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
    _binding = null
    }
}

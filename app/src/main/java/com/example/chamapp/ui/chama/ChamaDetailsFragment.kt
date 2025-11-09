package com.example.chamapp.ui.chama

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.chamapp.databinding.FragmentChamaDetailsBinding

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

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
<<<<<<< Updated upstream
        // We will set up the UI and click listeners here later
=======

        // Get chamaId from SafeArgs (update this if using SafeArgs)

        val chamaId = arguments?.getString("chamaId")
        android.util.Log.d("ChamaDetailsFragment", "Received chamaId: $chamaId")
        binding.tvChamaType.text = "chamaId: $chamaId" // TEMP: show id in UI for debug
        android.widget.Toast.makeText(requireContext(), "ChamaDetailsFragment loaded", android.widget.Toast.LENGTH_SHORT).show()
        android.util.Log.d("ChamaDetailsFragment", "onViewCreated called, chamaId=$chamaId")
        if (chamaId == null) {
            binding.tvChamaName.text = "Chama not found"
            return
        }

        viewModel.fetchChamaDetails(chamaId)
        viewModel.fetchMyMembership(chamaId)

        android.util.Log.d("ChamaDetailsFragment", "Called fetchChamaDetails($chamaId)")

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
                android.util.Log.d("ChamaDetailsFragment", "chama loaded: $chama")
            }
        }

        viewModel.myMembership.observe(viewLifecycleOwner) { member ->
            if (member != null) {
                binding.tvMyRole.text = "My Role: ${member.role ?: "-"}"
                binding.tvMyContribution.text = "My Contribution: KES ${member.contribution_amount ?: "-"}"
                android.util.Log.d("ChamaDetailsFragment", "myMembership loaded: $member")
            }
        }

        viewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            if (errorMsg != null) {
                binding.tvChamaName.text = errorMsg
                android.util.Log.d("ChamaDetailsFragment", "Error loading chama: $errorMsg")
            }
        }

        // Set up button listeners
    binding.btnEditChama.visibility = android.view.View.VISIBLE
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
                    val memberId = viewModel.myMembership.value?.id ?: return@setPositiveButton
                    android.util.Log.d("ChamaDetailsFragment", "Calling updateMyMembership with memberId=$memberId, contribution=$newContribution, role=$newRole")
                    viewModel.updateMyMembership(memberId, newRole, newContribution)
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        viewModel.updateResult.observe(viewLifecycleOwner) { resultMsg ->
            resultMsg?.let {
                android.widget.Toast.makeText(requireContext(), it, android.widget.Toast.LENGTH_SHORT).show()
            }
        }
>>>>>>> Stashed changes
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

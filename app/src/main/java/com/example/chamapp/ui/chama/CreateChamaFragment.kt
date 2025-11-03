package com.example.chamapp.ui.chama

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.chamapp.R // Make sure this is imported
import com.example.chamapp.api.CreateChamaRequest
import com.example.chamapp.databinding.FragmentCreateChamaBinding

class CreateChamaFragment : Fragment() {

    private var _binding: FragmentCreateChamaBinding? = null
    private val binding get() = _binding!!

    // Get an instance of the ChamaViewModel
    private val viewModel: ChamaViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateChamaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSpinners()
        setupClickListeners()
        setupObservers()
    }

    private fun setupSpinners() {
        // Populate Chama Type Spinner
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.chama_types_array, // This must exist in res/values/strings.xml
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerChamaType.adapter = adapter
        }

        // Populate Contribution Schedule Spinner
        ArrayAdapter.createFromResource(
            requireContext(),
            R.array.contribution_schedules_array, // This must exist in res/values/strings.xml
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.spinnerContributionSchedule.adapter = adapter
        }
        val meetingFrequencyAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, resources.getStringArray(R.array.meeting_frequency_array))
        binding.spinnerMeetingFrequency.setAdapter(meetingFrequencyAdapter)

        val meetingDayAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, resources.getStringArray(R.array.meeting_day_array))
        binding.spinnerMeetingDay.setAdapter(meetingDayAdapter)    }

    private fun setupClickListeners() {
        binding.btnCreateChama.setOnClickListener {
            handleCreateChama()
        }
    }

    private fun handleCreateChama() {
        // 1. Collect and validate data from the form
        val chamaName = binding.etChamaName.text.toString().trim()
        val description = binding.etChamaDescription.text.toString().trim()
        val contributionAmount = binding.etContributionAmount.text.toString().toDoubleOrNull()
        val interestRate = binding.etInterestRate.text.toString().toDoubleOrNull()
        val maxLoanMultiple = binding.etMaxLoanMultiple.text.toString().toIntOrNull()
    val chamaType = binding.spinnerChamaType.selectedItem.toString().lowercase()
        val contributionSchedule = binding.spinnerContributionSchedule.selectedItem.toString()
        val contributionDueDay = binding.etContributionDueDay.text.toString().toIntOrNull()
        val loanMaxTerm = binding.etLoanMaxTermMonths.text.toString().toIntOrNull()
        val meetingFrequency = binding.spinnerMeetingFrequency.text.toString()
        val meetingDay = binding.spinnerMeetingDay.text.toString()
        if (chamaName.isEmpty() || description.isEmpty() || contributionAmount == null || interestRate == null || maxLoanMultiple == null) {
            Toast.makeText(requireContext(), "Please fill all fields correctly.", Toast.LENGTH_SHORT).show()
            return
        }

        // 2. Create the request object
        val request = CreateChamaRequest(
            chama_name = chamaName,
            description = description,
            contribution_amount = contributionAmount,
            interest_rate = interestRate,
            max_loan_multiple = maxLoanMultiple,
            chama_type = chamaType,
            contribution_schedule = contributionSchedule,
            contribution_due_day = contributionDueDay,
            loan_max_term_months = loanMaxTerm,
            meeting_frequency = meetingFrequency,
            meeting_day = meetingDay

        )

        setLoading(true)
        // 3. Call the ViewModel function
        viewModel.createChama(request)
    }

    private fun setupObservers() {
        // 4. Observe the result from the ViewModel
        viewModel.createResult.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { (isSuccess, message) ->
                setLoading(false)
                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
                if (isSuccess) {
                    // Navigate back to the previous screen on success

                    findNavController().popBackStack()
                }
            }
        }
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.isVisible = isLoading
        binding.btnCreateChama.isEnabled = !isLoading
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

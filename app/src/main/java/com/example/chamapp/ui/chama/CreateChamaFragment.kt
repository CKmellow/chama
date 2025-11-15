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
        setupStepNavigation()
        setupObservers()
        setupClickListeners()
        binding.ivBackArrow.setOnClickListener {
            // Navigate back to HomeFragment
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }


    private fun setupStepNavigation() {
        // Step 1 visible, others hidden
        binding.cardStep1.visibility = View.VISIBLE
        binding.llNavigationStep1.visibility = View.VISIBLE
        binding.cardStep2.visibility = View.GONE
        binding.llNavigationStep2.visibility = View.GONE
        binding.cardStep3.visibility = View.GONE
        binding.llNavigationStep3.visibility = View.GONE

        binding.btnNextStep1.setOnClickListener {
            if (!validateStep1()) return@setOnClickListener
            binding.cardStep1.visibility = View.GONE
            binding.llNavigationStep1.visibility = View.GONE
            binding.cardStep2.visibility = View.VISIBLE
            binding.llNavigationStep2.visibility = View.VISIBLE
        }
        // Back button navigation to homepage
        binding.btnCancelStep1.setOnClickListener {
            findNavController().navigate(R.id.action_createChamaFragment_to_homeFragment)
        }
        binding.btnNextStep2.setOnClickListener {
            if (!validateStep2()) return@setOnClickListener
            binding.cardStep2.visibility = View.GONE
            binding.llNavigationStep2.visibility = View.GONE
            binding.cardStep3.visibility = View.VISIBLE
            binding.llNavigationStep3.visibility = View.VISIBLE
        }
        binding.btnBackStep2.setOnClickListener {
            binding.cardStep2.visibility = View.GONE
            binding.llNavigationStep2.visibility = View.GONE
            binding.cardStep1.visibility = View.VISIBLE
            binding.llNavigationStep1.visibility = View.VISIBLE
        }
        binding.btnBackStep3.setOnClickListener {
            binding.cardStep3.visibility = View.GONE
            binding.llNavigationStep3.visibility = View.GONE
            binding.cardStep2.visibility = View.VISIBLE
            binding.llNavigationStep2.visibility = View.VISIBLE
        }
        binding.btnNextStep3.setOnClickListener {
            if (!validateStep3()) return@setOnClickListener
            binding.cardStep3.visibility = View.GONE
            binding.llNavigationStep3.visibility = View.GONE
            binding.cardStep4.visibility = View.VISIBLE
            binding.llNavigationStep4.visibility = View.VISIBLE
        }
        binding.btnBackStep4.setOnClickListener {
            binding.cardStep4.visibility = View.GONE
            binding.llNavigationStep4.visibility = View.GONE
            binding.cardStep3.visibility = View.VISIBLE
            binding.llNavigationStep3.visibility = View.VISIBLE
        }
        binding.btnCreateChamaFinal.setOnClickListener {
            if (!validateStep3()) return@setOnClickListener
            createChama()
        }
    }

    private fun setupSpinners() {
        // Chama Type
        val chamaTypeAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.chama_types_array,
            android.R.layout.simple_dropdown_item_1line
        )
        binding.spinnerChamaType.setAdapter(chamaTypeAdapter)
        binding.spinnerChamaType.setOnClickListener {
            binding.spinnerChamaType.showDropDown()
        }

        // Meeting Frequency
        val meetingFrequencyAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.meeting_frequency_array,
            android.R.layout.simple_dropdown_item_1line
        )
        binding.spinnerMeetingFrequency.setAdapter(meetingFrequencyAdapter)
        binding.spinnerMeetingFrequency.setOnClickListener {
            binding.spinnerMeetingFrequency.showDropDown()
        }

        // Meeting Day
        val meetingDayAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.meeting_day_array,
            android.R.layout.simple_dropdown_item_1line
        )
        binding.spinnerMeetingDay.setAdapter(meetingDayAdapter)
        binding.spinnerMeetingDay.setOnClickListener {
            binding.spinnerMeetingDay.showDropDown()
        }

        // Contribution Schedule (Spinner)
        val contributionScheduleAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.contribution_schedules_array,
            android.R.layout.simple_spinner_item
        )
        contributionScheduleAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerContributionSchedule.adapter = contributionScheduleAdapter
    }

    private fun navigateToMyChamas() {
        findNavController().navigate(R.id.action_createChamaFragment_to_myChamasFragment)
    }

    private fun createChama() {
        val name = binding.etChamaName.text.toString().trim()
        val description = binding.etChamaDescription.text.toString().trim()
        val chamaType = binding.spinnerChamaType.text.toString()
        val contributionAmount = binding.etContributionAmount.text.toString().toDoubleOrNull() ?: 0.0
        val contributionSchedule = if (binding.spinnerContributionSchedule.selectedItem != null) binding.spinnerContributionSchedule.selectedItem.toString() else ""
        val interestRate = binding.etInterestRate.text.toString().toDoubleOrNull() ?: 0.0
        val maxLoanMultiple = binding.etMaxLoanMultiple.text.toString().toDoubleOrNull() ?: 1.0 // changed to Double
        val contributionDueDay = binding.etContributionDueDay.text.toString().trim() // changed to String
        val loanMaxTermMonths = binding.etLoanMaxTermMonths.text.toString().toIntOrNull() ?: 1
        val meetingFrequency = binding.spinnerMeetingFrequency.text.toString()
        val meetingDay = binding.spinnerMeetingDay.text.toString()

        viewModel.createChama(
            CreateChamaRequest(
                name,
                description,
                chamaType,
                contributionAmount,
                contributionSchedule,
                interestRate,
                maxLoanMultiple,
                contributionDueDay,
                loanMaxTermMonths,
                meetingFrequency,
                meetingDay
            )
        )
    }

    private fun validateStep1(): Boolean {
        if (binding.etChamaName.text.isNullOrBlank()) {
            binding.tilChamaName.error = "Chama name required"
            return false
        } else binding.tilChamaName.error = null
        if (binding.etChamaDescription.text.isNullOrBlank()) {
            binding.tilChamaDescription.error = "Description required"
            return false
        } else binding.tilChamaDescription.error = null
        return true
    }
    private fun validateStep2(): Boolean {
        if (binding.etContributionAmount.text.isNullOrBlank()) {
            binding.tilContributionAmount.error = "Contribution required"
            return false
        } else binding.tilContributionAmount.error = null
        if (binding.etInterestRate.text.isNullOrBlank()) {
            binding.tilInterestRate.error = "Interest rate required"
            return false
        } else binding.tilInterestRate.error = null
        return true
    }
    private fun validateStep3(): Boolean {
        binding.btnCreateChamaFinal.isEnabled = true
        if (binding.etMaxLoanMultiple.text.isNullOrBlank()) {
            binding.tilMaxLoanMultiple.error = "Max loan multiple required"
            return false
        } else binding.tilMaxLoanMultiple.error = null
        if (binding.etContributionDueDay.text.isNullOrBlank()) {
            binding.tilContributionDueDay.error = "Contribution due day required"
            return false
        } else binding.tilContributionDueDay.error = null
        if (binding.etLoanMaxTermMonths.text.isNullOrBlank()) {
            binding.tilLoanMaxTermMonths.error = "Max loan term required"
            return false
        } else binding.tilLoanMaxTermMonths.error = null
        return true
    }

    private fun setupObservers() {
        viewModel.createResult.observe(viewLifecycleOwner) { event ->
            val result = event.getContentIfNotHandled() ?: return@observe
            binding.progressBar.isVisible = false
            binding.btnCreateChamaFinal.isEnabled = true
            val (success, message) = result
            if (success) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
                navigateToMyChamas()
            } else {
                Toast.makeText(requireContext(), "Failed: $message", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun setupClickListeners() {
        binding.ivBackArrow.setOnClickListener {
            findNavController().navigate(R.id.action_createChamaFragment_to_homeFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

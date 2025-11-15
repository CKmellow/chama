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

    private val viewModel: ChamaDetailsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChamaDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get chamaId from SafeArgs (update this if using SafeArgs)
        val chamaId = arguments?.getString("chamaId")
        android.util.Log.d("ChamaDetailsFragment", "Received chamaId: $chamaId")
        binding.tvChamaName.text = "ChamaId: $chamaId" // TEMP: show id in UI for debug
        android.widget.Toast.makeText(requireContext(), "ChamaDetailsFragment loaded", android.widget.Toast.LENGTH_SHORT).show()
        android.util.Log.d("ChamaDetailsFragment", "onViewCreated called, chamaId=$chamaId")
        if (chamaId == null) {
            binding.tvChamaName.text = "Chama not found"
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

        // Example: Set up click listener for tv_view_all_members if you want to navigate
        binding.tvViewAllMembers.setOnClickListener {
            // TODO: Implement navigation to members list
        }
        // ...existing code...
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

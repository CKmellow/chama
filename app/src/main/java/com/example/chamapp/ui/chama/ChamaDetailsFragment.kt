package com.example.chamapp.ui.chama

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.chamapp.databinding.FragmentChamaDetailsBinding

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

        val chamaId = arguments?.getString("chamaId")
        if (chamaId == null) {
            Toast.makeText(requireContext(), "Chama ID not found", Toast.LENGTH_SHORT).show()
            return
        }

        // Observe LiveData
        viewModel.chamaDetails.observe(viewLifecycleOwner) { chama ->
            if (chama != null) {
                binding.tvChamaName.text = chama.chama_name
                binding.tvChamaType.text = "Type: ${chama.chama_type ?: "-"}"
                // ... update other UI elements ...

                val members = chama.members
                if (members != null) {
                    val dialog = MembersListDialogFragment.newInstance(members)
                    dialog.show(parentFragmentManager, "MembersListDialogFragment")
                } else {
                    val dialog = MembersListDialogFragment.newInstance(emptyList())
                    dialog.show(parentFragmentManager, "MembersListDialogFragment")
                }
            }
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { errorMsg ->
            if (errorMsg != null) {
                Toast.makeText(requireContext(), errorMsg, Toast.LENGTH_LONG).show()
            }
        }

        // Set up button listeners
        binding.btnViewMembers.setOnClickListener {
            viewModel.fetchChamaDetails(chamaId)
        }
        
        // You can add other button listeners here if needed

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

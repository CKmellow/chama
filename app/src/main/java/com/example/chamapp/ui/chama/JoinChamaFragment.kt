package com.example.chamapp.ui.chama

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.chamapp.databinding.FragmentJoinChamaBinding
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.example.chamapp.api.ApiHelper

class JoinChamaFragment : Fragment() {
    private var _binding: FragmentJoinChamaBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentJoinChamaBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.ivBackArrow.setOnClickListener {
            // Navigate back to HomeFragment
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.btnJoinChama.setOnClickListener {
            val code = binding.etInvitationCode.text.toString().trim()
            if (code.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter invitation code", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            // Use findViewById for progressBar until data binding is regenerated
            val progressBar = view.findViewById<android.widget.ProgressBar>(com.example.chamapp.R.id.progressBar)
            progressBar?.visibility = View.VISIBLE
            lifecycleScope.launch {
                val result = ApiHelper.joinChama(code)
                progressBar?.visibility = View.GONE
                if (result.success) {
                    Toast.makeText(requireContext(), "Joined chama successfully!", Toast.LENGTH_LONG).show()
                    // Optionally navigate to MyChamasFragment
                } else {
                    Toast.makeText(requireContext(), "Failed: ${result.error}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

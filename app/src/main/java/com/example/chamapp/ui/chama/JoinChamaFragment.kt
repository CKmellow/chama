package com.example.chamapp.ui.chama

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.chamapp.databinding.FragmentJoinChamaBinding

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

        binding.btnJoinChama.setOnClickListener {
            val invitationCode = binding.etInvitationCode.text?.toString()?.trim()
            if (invitationCode.isNullOrEmpty()) {
                binding.tilInvitationCode.error = "Invitation code required"
                return@setOnClickListener
            } else {
                binding.tilInvitationCode.error = null
            }
            // TODO: Implement join logic here (e.g., call ViewModel, API, etc.)
            // For now, show a Toast
            android.widget.Toast.makeText(requireContext(), "Joining Chama with code: $invitationCode", android.widget.Toast.LENGTH_SHORT).show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

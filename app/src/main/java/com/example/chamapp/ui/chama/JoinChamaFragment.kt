package com.example.chamapp.ui.chama

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.chamapp.databinding.FragmentJoinChamaBinding
import com.example.chamapp.api.RetrofitClient
import kotlinx.coroutines.launch

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
        binding.btnAcceptInvitation.setOnClickListener {
            val inviteCode = binding.etInviteCode.text?.toString()?.trim()
            android.util.Log.d("JoinChamaFragment", "Attempting to join chama with invite code: $inviteCode")
            if (inviteCode.isNullOrEmpty()) {
                android.widget.Toast.makeText(requireContext(), "Please enter an invite code", android.widget.Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            lifecycleScope.launch {
                try {
                    val response = com.example.chamapp.api.RetrofitClient.instance.joinChama(mapOf("code" to inviteCode))
                    android.util.Log.d("JoinChamaFragment", "Response: success=${response.body()?.success}, message=${response.body()?.message}, error=${response.errorBody()?.string()}")
                    if (response.isSuccessful && response.body()?.success == true) {
                        android.widget.Toast.makeText(requireContext(), "Joined chama successfully!", android.widget.Toast.LENGTH_LONG).show()
                        // Optionally navigate to dashboard or chama details
                    } else {
                        val errorMsg = response.body()?.message ?: response.errorBody()?.string() ?: "Failed to join chama"
                        android.widget.Toast.makeText(requireContext(), errorMsg, android.widget.Toast.LENGTH_LONG).show()
                    }
                } catch (e: Exception) {
                    android.util.Log.e("JoinChamaFragment", "Error joining chama: ${e.message}", e)
                    android.widget.Toast.makeText(requireContext(), "Error: ${e.message}", android.widget.Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

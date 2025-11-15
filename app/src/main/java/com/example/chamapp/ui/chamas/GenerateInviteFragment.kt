package com.example.chamapp.ui.chamas
import androidx.navigation.fragment.findNavController
import androidx.lifecycle.lifecycleScope

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.chamapp.R
import kotlinx.coroutines.launch

class GenerateInviteFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_generate_invite, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val btnGenerate = view.findViewById<View>(R.id.btnGenerateInviteCode)
        btnGenerate.setOnClickListener {
            val progressBar = view.findViewById<View>(R.id.progressBarSync)
            progressBar.visibility = View.VISIBLE

            // Get chamaId from arguments
            val chamaId = arguments?.getString("chamaId")
            if (chamaId.isNullOrBlank()) {
                progressBar.visibility = View.GONE
                android.widget.Toast.makeText(requireContext(), "No chamaId provided", android.widget.Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

            // Fetch chama details first
            val viewModel = androidx.lifecycle.ViewModelProvider(requireActivity()).get(com.example.chamapp.ui.home.HomeViewModel::class.java)
            viewModel.fetchChamaDetails(chamaId)

            // Observe chama details ONCE and then remove observer
            viewModel.chamas.observe(viewLifecycleOwner) { chamas ->
                val chama = chamas?.find { it.id == chamaId }
                if (chama != null) {
                    view.postDelayed({
                        progressBar.visibility = View.GONE
                        val inviteCode = "CODE123" // Replace with actual code from backend
                        lifecycleScope.launch {
                            try {
                                android.util.Log.d("GenerateInviteFragment", "Posting invite code: $inviteCode, chamaId: ${chama.id}, chamaName: ${chama.chama_name}")
                                val response = com.example.chamapp.api.RetrofitClient.instance.storeInviteCode(
                                    mapOf(
                                        "code" to inviteCode,
                                        "chamaId" to chama.id,
                                        "chamaName" to (chama.chama_name ?: "")
                                    )
                                )
                                android.util.Log.d("GenerateInviteFragment", "Response: success=${response.isSuccessful}, body=${response.body()}, error=${response.errorBody()?.string()}")
                                if (response.isSuccessful) {
                                    android.util.Log.d("GenerateInviteFragment", "Invite code stored in Supabase with chama details.")
                                } else {
                                    android.util.Log.e("GenerateInviteFragment", "Failed to store invite code: ${response.errorBody()?.string()}")
                                }
                            } catch (e: Exception) {
                                android.util.Log.e("GenerateInviteFragment", "Error storing invite code: ${e.message}", e)
                            }
                        }
                        val bundle = Bundle().apply { putString("inviteCode", inviteCode) }
                        findNavController().navigate(R.id.action_generateInviteFragment_to_inviteCodeDisplayFragment, bundle)
                    }, 2000)
                    // Remove observer after first use
                    viewModel.chamas.removeObservers(viewLifecycleOwner)
                }
            }
        }
    }
}

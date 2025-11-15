package com.example.chamapp.ui.chama

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import com.example.chamapp.R
import com.example.chamapp.databinding.FragmentChamaDashboardBinding
class ChamaDashboardFragment : Fragment() {
    private val viewModel: com.example.chamapp.ui.home.HomeViewModel by activityViewModels()
    private var _binding: FragmentChamaDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChamaDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args = ChamaDashboardFragmentArgs.fromBundle(requireArguments())
        val chamaName = args.chamaName
        val chama = viewModel.chamas.value?.find { it.chama_name == chamaName }

        binding.tvChamaName.text = chama?.chama_name ?: "Umoja Investment Group"
        binding.tvChamaBalance.text = getString(R.string.chama_balance_format, chama?.totalBalance ?: 0.0)

        val sessionManager = com.example.chamapp.util.SessionManager(requireContext())
        val userId = sessionManager.getUserId()
        // Try to get role from users table (SessionManager)
        val userRoleFromSession = sessionManager.getUserName().let { (firstName, lastName) ->
            // You may want to fetch more user details if you store role in SessionManager
            // For now, fallback to role from chama members if not available
            null // Replace with actual role fetch if implemented
        }
        val myRole = userRoleFromSession ?: chama?.members?.find { it.userId == userId }?.role ?: "Member"
        binding.tvUserRole.text = getString(R.string.chama_role_format, myRole)

        val viewAllButton = binding.btnViewAllMembers
        viewAllButton.isEnabled = true
        viewAllButton.isClickable = true
        viewAllButton.alpha = 1.0f
        viewAllButton.visibility = View.VISIBLE

        viewAllButton.setOnClickListener {
            if (chama != null) {
                val bundle = Bundle().apply { putParcelable("chama", chama) }
                findNavController().navigate(R.id.action_chamaDashboardFragment_to_allMembersFragment, bundle)
            } else {
                android.widget.Toast.makeText(requireContext(), "Chama not found or not loaded yet.", android.widget.Toast.LENGTH_SHORT).show()
            }
        }

        val btnGenerateInviteCode = binding.btnGenerateInvitation
        val userRole = myRole.lowercase()
        btnGenerateInviteCode.visibility = if (userRole == "secretary" || userRole == "chairperson") View.VISIBLE else View.GONE
        btnGenerateInviteCode.setOnClickListener {
            btnGenerateInviteCode.isEnabled = false
            btnGenerateInviteCode.text = "Generating..."
            viewLifecycleOwner.lifecycleScope.launch {
                try {
                    val response = com.example.chamapp.api.RetrofitClient.instance.regenerateInviteCode(chama?.chama_name ?: "Unknown")
                    if (response.isSuccessful) {
                        val code = response.body()?.invitation_code ?: "(none)"
                        val dialog = android.app.AlertDialog.Builder(requireContext())
                            .setTitle("Chama Invite Code")
                            .setMessage("Share this code: $code")
                            .setPositiveButton("OK", null)
                            .create()
                        dialog.show()
                        btnGenerateInviteCode.text = "Generate Invite Code"
                    } else {
                        android.widget.Toast.makeText(requireContext(), "Failed: ${response.errorBody()?.string()}", android.widget.Toast.LENGTH_LONG).show()
                        btnGenerateInviteCode.text = "Generate Invite Code"
                    }
                } catch (e: Exception) {
                    android.widget.Toast.makeText(requireContext(), "Error: ${e.message}", android.widget.Toast.LENGTH_LONG).show()
                    btnGenerateInviteCode.text = "Generate Invite Code"
                }
                btnGenerateInviteCode.isEnabled = true
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

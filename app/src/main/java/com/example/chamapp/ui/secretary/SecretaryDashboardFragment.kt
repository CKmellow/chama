package com.example.chamapp.ui.secretary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch
import kotlinx.coroutines.Dispatchers
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chamapp.databinding.FragmentSecretaryDashboardBinding

class SecretaryDashboardFragment : Fragment() {
    companion object {
        private const val ARG_CHAMA_ID = "chamaId"

        fun newInstance(chamaId: String): SecretaryDashboardFragment {
            val fragment = SecretaryDashboardFragment()
            val args = Bundle()
            args.putString(ARG_CHAMA_ID, chamaId)
            fragment.arguments = args
            return fragment
        }
    }
    private val viewModel: com.example.chamapp.ui.chama.ChamaDetailsViewModel by viewModels()

    private var _binding: FragmentSecretaryDashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSecretaryDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.rvMembers.layoutManager = LinearLayoutManager(requireContext())

    // Fetch chamaId from arguments using companion object's constant
    val chamaId = arguments?.getString(ARG_CHAMA_ID) ?: ""
    android.util.Log.d("SecretaryDashboard", "Fetched chamaId: $chamaId")
    viewModel.fetchChamaDetails(chamaId)

        viewModel.chamaDetails.observe(viewLifecycleOwner) { chama ->
            android.util.Log.d("SecretaryDashboard", "chamaDetails.observe triggered, chama: $chama")
            if (chama != null) {
                android.util.Log.d("SecretaryDashboard", "Fetched chama: $chama")
                binding.tvChamaName.text = chama.chama_name
                val balance = chama.total_balance ?: chama.totalBalance ?: 0.0
                binding.tvTotalContributionLabel.text = "Total Balance: Ksh ${String.format("%,.2f", balance)}"
                val members = (chama.members ?: emptyList()).map { relation ->
                    Member(
                        name = listOfNotNull(relation.firstName, relation.lastName).joinToString(" ").ifBlank { relation.userId },
                        role = relation.role ?: "",
                        contribution = relation.contributionAmount?.toString() ?: "0",
                        status = relation.status ?: "",
                        email = relation.email,
                        phone = relation.phoneNumber,
                        profilePictureUrl = null // Add if available
                    )
                }
                android.util.Log.d("SecretaryDashboard", "Final members list: $members")
                binding.rvMembers.adapter = MemberAdapter(members)
            } else {
                binding.tvChamaName.text = "No chama found"
                android.util.Log.e("SecretaryDashboard", "chama is null or not found for id: $chamaId")
                binding.rvMembers.adapter = MemberAdapter(emptyList())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
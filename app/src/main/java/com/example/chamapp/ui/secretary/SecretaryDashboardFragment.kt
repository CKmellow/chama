package com.example.chamapp.ui.secretary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.chamapp.api.Chama
import com.example.chamapp.api.ChamaMemberRelation
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
    private val viewModel: com.example.chamapp.ui.chama.ChamaViewModel by viewModels()

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

        val chamaId = arguments?.getString(ARG_CHAMA_ID) ?: ""
        android.util.Log.d("SecretaryDashboard", "Fetched chamaId: $chamaId")
        viewModel.fetchChamas()

        viewModel.chamas.observe(viewLifecycleOwner) { chamasList: List<Chama> ->
            val chama = chamasList.firstOrNull { it.id == chamaId }
            android.util.Log.d("SecretaryDashboard", "Found chama: $chama")
            if (chama != null) {
                binding.tvChamaName.text = chama.chama_name ?: "No Name"
                val balance = chama.totalBalance ?: 0.0
                binding.tvTotalContributionLabel.text = "Total Balance: Ksh ${String.format("%,.2f", balance)}"
                val members = chama.members?.map { relation ->
                    Member(
                        name = listOfNotNull(relation.firstName, relation.lastName).joinToString(" ").ifBlank { relation.userId ?: "" },
                        role = relation.role ?: "",
                        contribution = relation.contributionAmount?.toString() ?: "0",
                        status = relation.status ?: "",
                        email = relation.email,
                        phone = relation.phoneNumber,
                        profilePictureUrl = null
                    )
                } ?: emptyList()
                binding.rvMembers.adapter = MemberAdapter(members)
            } else {
                binding.tvChamaName.text = "No chama found"
                binding.rvMembers.adapter = MemberAdapter(emptyList())
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package com.example.chamapp.ui.chamas

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chamapp.databinding.FragmentAllMembersBinding
import com.example.chamapp.ui.chama.MemberAdapter
import com.example.chamapp.data.Chama
import com.example.chamapp.data.ChamaMember
import com.example.chamapp.ui.home.HomeViewModel

class AllMembersFragment : Fragment() {
        // Helper to convert raw member maps to ChamaMember objects
        private fun convertRawMembers(membersList: List<*>): List<com.example.chamapp.data.ChamaMember> {
            return membersList.mapNotNull { memberMap ->
                if (memberMap is Map<*, *>) {
                    com.example.chamapp.data.ChamaMember(
                        id = memberMap["id"] as? String ?: "",
                        chamaId = memberMap["chama_id"] as? String ?: "",
                        userId = memberMap["user_id"] as? String ?: "",
                        role = memberMap["role"] as? String,
                        contributionAmount = (memberMap["contribution_amount"] as? Number)?.toDouble(),
                        joinedAt = memberMap["joined_at"] as? String,
                        status = memberMap["status"] as? String,
                        firstName = memberMap["first_name"] as? String,
                        lastName = memberMap["last_name"] as? String,
                        email = memberMap["email"] as? String,
                        phoneNumber = memberMap["phone_number"] as? String
                    )
                } else null
            }
        }
    private var _binding: FragmentAllMembersBinding? = null
    private val binding get() = _binding!!
    private val viewModel: HomeViewModel by activityViewModels()
    private var hasTriedFetchingDetails = false // Prevent multiple API calls

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = com.example.chamapp.databinding.FragmentAllMembersBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        android.util.Log.d("AllMembersFragment", "=== onViewCreated START ===")
        super.onViewCreated(view, savedInstanceState)
        binding.rvAllMembers.layoutManager = LinearLayoutManager(requireContext())
        binding.rvAllMembers.adapter = MemberAdapter(emptyList())
        binding.tvEmptyMembers.visibility = View.VISIBLE
        binding.rvAllMembers.visibility = View.GONE

        val chamaId = arguments?.getString("chamaId")
        android.util.Log.d("AllMembersFragment", "Received chamaId: $chamaId")

        // Force LiveData update to trigger observer
        android.util.Log.d("AllMembersFragment", "Calling viewModel.fetchChamas() to force LiveData update")
        viewModel.fetchChamas()

        if (chamaId.isNullOrEmpty()) {
            android.util.Log.w("AllMembersFragment", "No chamaId provided")
            return
        }

        viewModel.chamas.observe(viewLifecycleOwner) { chamas ->
            try {
                android.util.Log.d("AllMembersFragment", "Observer triggered. chamas: $chamas")
                val chama = chamas?.find { it.id == chamaId }
                if (chama == null) {
                    android.util.Log.e("AllMembersFragment", "No chama found for id: $chamaId")
                } else {
                    android.util.Log.d("AllMembersFragment", "Selected chama: ${chama.chama_name} (id: ${chama.id})")
                }
                val members = if (chama != null) convertRawMembers(chama.members ?: emptyList<Any>()) else emptyList()
                android.util.Log.d("AllMembersFragment", "Members extracted count: ${members.size}")
                members.forEachIndexed { idx, member ->
                    android.util.Log.d("AllMembersFragment", "Member[$idx]: $member")
                }
                binding.rvAllMembers.adapter = MemberAdapter(members)
                android.util.Log.d("AllMembersFragment", "Adapter updated with members: ${members.size}")
                if (members.isEmpty() && !hasTriedFetchingDetails) {
                    hasTriedFetchingDetails = true
                    android.util.Log.d("AllMembersFragment", "No members found, fetching chama details from API")
                    viewModel.fetchChamaDetails(chamaId)
                    return@observe
                }
                if (members.isEmpty()) {
                    binding.tvEmptyMembers.visibility = View.VISIBLE
                    binding.rvAllMembers.visibility = View.GONE
                } else {
                    binding.tvEmptyMembers.visibility = View.GONE
                    binding.rvAllMembers.visibility = View.VISIBLE
                }
            } catch (e: Exception) {
                android.util.Log.e("AllMembersFragment", "Error in observer: ${e.message}", e)
                e.printStackTrace()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}


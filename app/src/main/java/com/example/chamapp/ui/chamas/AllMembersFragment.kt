package com.example.chamapp.ui.chamas
import androidx.navigation.fragment.findNavController
import com.example.chamapp.R
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
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
                    chamaId = memberMap["chama_id"] as? String ?: memberMap["chamaId"] as? String ?: "",
                    userId = memberMap["user_id"] as? String ?: memberMap["userId"] as? String ?: "",
                    role = memberMap["role"] as? String,
                    contributionAmount = (memberMap["contribution_amount"] as? Number)?.toDouble()
                        ?: (memberMap["contributionAmount"] as? Number)?.toDouble(),
                    joinedAt = memberMap["joined_at"] as? String ?: memberMap["joinedAt"] as? String,
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
                binding.fabAddMember.setOnClickListener {
            // Get chamaId from arguments
            val chamaId = arguments?.getString("chamaId")
            if (chamaId.isNullOrBlank()) {
                android.widget.Toast.makeText(requireContext(), "No chamaId provided", android.widget.Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }
            val bundle = Bundle().apply { putString("chamaId", chamaId) }
            findNavController().navigate(R.id.action_allMembersFragment_to_generateInviteFragment, bundle)
        }
        android.util.Log.d("AllMembersFragment", "=== onViewCreated START ===")
        super.onViewCreated(view, savedInstanceState)
        binding.rvAllMembers.layoutManager = LinearLayoutManager(requireContext())
        val memberAdapter = MemberAdapter(emptyList())
        binding.rvAllMembers.adapter = memberAdapter
        binding.tvEmptyMembers.visibility = View.VISIBLE
        binding.rvAllMembers.visibility = View.GONE

        val chamaId = arguments?.getString("chamaId")
        android.util.Log.d("AllMembersFragment", "Received chamaId: $chamaId")

        // Force LiveData update to trigger observer
        android.util.Log.d("AllMembersFragment", "Calling viewModel.fetchChamas() to force LiveData update")
        val sessionManager = com.example.chamapp.util.SessionManager(requireContext())
        val token = sessionManager.getAuthToken() ?: ""
        android.util.Log.d("AllMembersFragment", "Fetched token: $token")
            viewModel.fetchMyChamas(token)

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
                // Prefer chama.chama_members if present, else fallback to chama.members
                val rawMembers: List<Any> = when {
                    chama != null && chama.chama_members is List<*> -> chama.chama_members as List<Any>
                    chama != null && chama.members is List<*> -> chama.members as List<Any>
                    else -> emptyList()
                }
                val members = convertRawMembers(rawMembers)
                android.util.Log.d("AllMembersFragment", "Members extracted count: ${members.size}")
                // Fetch first/last name for each member from backend if missing
                lifecycleScope.launch {
                    val updatedMembers = members.map { member ->
                        if (member.firstName.isNullOrBlank() && member.userId.isNotBlank()) {
                            try {
                                val response = com.example.chamapp.api.RetrofitClient.instance.getUserById(member.userId)
                                if (response.isSuccessful) {
                                    val user = response.body()?.user
                                    member.copy(
                                        firstName = user?.first_name,
                                        lastName = user?.last_name
                                    )
                                } else member
                            } catch (e: Exception) {
                                member
                            }
                        } else member
                    }
                    memberAdapter.apply {
                        val field = this::class.java.getDeclaredField("members")
                        field.isAccessible = true
                        field.set(this, updatedMembers)
                        notifyDataSetChanged()
                    }
                    android.util.Log.d("AllMembersFragment", "Adapter updated with members: ${updatedMembers.size}")
                    if (updatedMembers.isEmpty() && !hasTriedFetchingDetails) {
                        hasTriedFetchingDetails = true
                        android.util.Log.d("AllMembersFragment", "No members found, fetching chama details from API")
                        viewModel.fetchChamaDetails(chamaId)
                        return@launch
                    }
                    if (updatedMembers.isEmpty()) {
                        binding.tvEmptyMembers.visibility = View.VISIBLE
                        binding.rvAllMembers.visibility = View.GONE
                    } else {
                        binding.tvEmptyMembers.visibility = View.GONE
                        binding.rvAllMembers.visibility = View.VISIBLE
                    }
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
    private fun inviteMember(chamaId: String, memberId: String) {
        android.util.Log.d("AllMembersFragment", "Inviting member: $memberId to chama: $chamaId")
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = com.example.chamapp.api.RetrofitClient.instance.inviteMember(
                    chamaId,
                    com.example.chamapp.api.InviteMemberRequest(memberId)
                )
                if (response.isSuccessful) {
                    val code = response.body()?.message ?: "Invite sent!"
                    android.widget.Toast.makeText(requireContext(), "Invite code: $code", android.widget.Toast.LENGTH_LONG).show()
                } else {
                    android.widget.Toast.makeText(requireContext(), "Failed to invite: ${response.errorBody()?.string()}", android.widget.Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                android.widget.Toast.makeText(requireContext(), "Error: ${e.message}", android.widget.Toast.LENGTH_LONG).show()
            }
        }
    }
}


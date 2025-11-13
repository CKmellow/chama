package com.example.chamapp.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import kotlin.text.toDoubleOrNull
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chamapp.MainActivity
import com.example.chamapp.R
import com.example.chamapp.ui.home.Chama
import com.example.chamapp.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupHeader()
        setupRecyclerView()
        setupButtons()
        observeViewModel()

        // Remove token check from user_prefs, always try to fetch chamas
        viewModel.fetchChamas(null)
    }

    private fun setupHeader() {
    val prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    val userName = prefs.getString("first_name", "User") ?: "User"
    val userId = prefs.getString("user_id", null)
    android.util.Log.d("HomeFragment", "setupHeader: user_id from prefs = $userId")
    binding.tvGreeting.text = "Welcome back, $userName!"
    binding.tvDate.text = "Nov 2, 2025" // Temporary static date
    binding.tvNotificationBadge.text = "3" // Example badge count
    }

    private fun setupRecyclerView() {
        binding.rvChamas.layoutManager = LinearLayoutManager(requireContext())
        binding.rvChamas.adapter = ChamaAdapter(emptyList()) { chama ->
            val prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
            val currentUserId = prefs.getString("user_id", null)
            // Assume chama.members is List<ChamaMemberRelation> or similar
            val member = chama.members?.find { it.userId == currentUserId }
            val bundle = Bundle().apply {
                putString("chamaId", chama.id)
            }
            // Log request details and role
            android.util.Log.d("HomeFragment", "Pressed chama: id=${chama.id}, name=${chama.name}, currentUserId=$currentUserId")
            android.util.Log.d("HomeFragment", "Members: ${chama.members}")
            if (member != null) {
                android.util.Log.d("HomeFragment", "Found member: $member")
                val role = member.role?.lowercase()
                android.util.Log.d("HomeFragment", "User role: $role")
            } else {
                android.util.Log.d("HomeFragment", "Current user is not a member of this chama.")
            }
            val navController = findNavController()
            val currentDest = navController.currentDestination?.id
            if (currentDest == R.id.homeFragment) {
<<<<<<< Updated upstream
                navController.navigate(R.id.action_homeFragment_to_chamaDetailsFragment, bundle)
=======
                if (member != null) {
                    val role = member.role?.lowercase()
                    if (role == "secretary") {
                        navController.navigate(R.id.action_homeFragment_to_secretaryDashboardFragment, bundle)
                    } else if (role == "member") {
                        navController.navigate(R.id.action_homeFragment_to_chamaDetailsFragment, bundle)
                    } else {
                        Toast.makeText(requireContext(), "Unknown role: $role", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Sorry not a member", Toast.LENGTH_SHORT).show()
                }
>>>>>>> Stashed changes
            } else {
                Toast.makeText(requireContext(), "Navigation failed: not on HomeFragment", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupButtons() {
        binding.btnCreateChama.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_createChamaFragment)
        }
    }

    private fun observeViewModel() {
        viewModel.chamas.observe(viewLifecycleOwner, Observer { response ->
            android.util.Log.d("HomeFragment", "Raw chamas API response: $response")
            android.util.Log.d("HomeFragment", "Raw response object type: ${response?.javaClass?.name}")
            val chamasList = response as? List<*>
            android.util.Log.d("HomeFragment", "chamasList type: ${chamasList?.javaClass?.name}")
            android.util.Log.d("HomeFragment", "chamasList contents: $chamasList")
            if (chamasList.isNullOrEmpty()) {
                binding.tvNoChamas.visibility = View.VISIBLE
                binding.rvChamas.visibility = View.GONE
                binding.tvNoChamas.text = "No chamas available."
            } else {
                binding.tvNoChamas.visibility = View.GONE
                binding.rvChamas.visibility = View.VISIBLE
                val apiChamas = chamasList.filterIsInstance<com.example.chamapp.api.Chama>()
                val uiChamas = apiChamas.map { apiChama ->
                    Chama(
                        id = apiChama.id,
                        name = apiChama.chama_name,
                        role = apiChama.role ?: "",
                        myContributions = apiChama.myContributions?.toString() ?: "0",
                        totalBalance = apiChama.totalBalance?.toString() ?: "0",
                        status = apiChama.status ?: "",
                        statusColor = apiChama.statusColor ?: "#388E3C",
                        nextMeeting = apiChama.nextMeeting ?: "",
                        members = apiChama.members
                    )
                }
                android.util.Log.d("HomeFragment", "Mapped uiChamas: $uiChamas")
                (binding.rvChamas.adapter as ChamaAdapter).updateChamas(uiChamas)
                val totalSavings = uiChamas.sumOf { it.totalBalance.toDoubleOrNull() ?: 0.0 }
                binding.tvTotalSavingsAmount.text = "KES ${"%,.0f".format(totalSavings)}"
                binding.tvActiveLoansValue.text = "2 loans - KES 30,000"
                binding.tvUpcomingVotesValue.text = "3 pending"
            }
        })
        viewModel.error.observe(viewLifecycleOwner, Observer { errorMessage ->
            errorMessage?.let {
                binding.tvNoChamas.text = it
                binding.tvNoChamas.visibility = View.VISIBLE
                binding.rvChamas.visibility = View.GONE
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

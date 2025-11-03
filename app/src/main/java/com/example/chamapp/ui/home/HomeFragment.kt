package com.example.chamapp.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chamapp.R
import com.example.chamapp.api.Chama
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

        val prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val token = prefs.getString("access_token", null)

        if (token == null) {
            Toast.makeText(requireContext(), "Please log in again.", Toast.LENGTH_LONG).show()
            findNavController().navigate(R.id.auth_nav)
            return
        }

        viewModel.fetchChamas(token)
    }

    private fun setupHeader() {
        val prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val userName = prefs.getString("first_name", "User") ?: "User"
        binding.tvGreeting.text = "Welcome back, $userName!"
        binding.tvDate.text = "Nov 2, 2025" // Temporary static date
        binding.tvNotificationBadge.text = "3" // Example badge count
    }

    private fun setupRecyclerView() {
        binding.rvChamas.layoutManager = LinearLayoutManager(requireContext())
        binding.rvChamas.adapter = ChamaAdapter(emptyList()) { chama ->
            val action = HomeFragmentDirections.actionHomeFragmentToChamaDashboardFragment(
                chama.name,
                chama.role,
                chama.myContributions.toString(),
                chama.totalBalance.toString(),
                chama.status,
                chama.statusColor,
                chama.nextMeeting
            )
            findNavController().navigate(action)
        }
    }

    private fun setupButtons() {
        binding.btnCreateChama.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_createChamaFragment)
        }

        // Sidebar menu click (from ft-sidebar)
        binding.ivMenu.setOnClickListener {
            val drawerLayout = activity?.findViewById<DrawerLayout>(R.id.drawer_layout)
            drawerLayout?.openDrawer(GravityCompat.START)
        }
    }

    private fun observeViewModel() {
        viewModel.chamas.observe(viewLifecycleOwner, Observer { chamas ->
            if (chamas.isNullOrEmpty()) {
                binding.tvNoChamas.visibility = View.VISIBLE
                binding.rvChamas.visibility = View.GONE
                binding.tvNoChamas.text = "No chamas available."
            } else {
                binding.tvNoChamas.visibility = View.GONE
                binding.rvChamas.visibility = View.VISIBLE

                // Map API model to UI model
                val uiChamas = chamas.map { apiChama ->
                    com.example.chamapp.ui.home.Chama(
                        name = apiChama.chama_name,
                        role = apiChama.chama_type ?: "",
                        myContributions = apiChama.monthly_contribution_amount?.toString() ?: "-",
                        totalBalance = apiChama.total_balance?.toString() ?: "-",
                        status = apiChama.description ?: "-",
                        statusColor = "#388E3C",
                        nextMeeting = apiChama.contribution_due_day ?: "-"
                    )
                }

                (binding.rvChamas.adapter as ChamaAdapter).updateChamas(uiChamas)

                // Optional: show summary stats at the top
                val totalSavings = uiChamas.sumOf { it.totalBalance.toDoubleOrNull() ?: 0.0 }
                binding.tvTotalSavingsAmount.text = "KES ${"%,.0f".format(totalSavings)}"
                binding.tvActiveLoansValue.text = "2 loans - KES 30,000"
                binding.tvUpcomingVotesValue.text = "3 pending"
            }
        })

        // Preserve error observer from main
        viewModel.error.observe(viewLifecycleOwner, Observer { errorMessage ->
            errorMessage?.let {
                binding.tvNoChamas.text = it
                binding.tvNoChamas.visibility = View.VISIBLE
                binding.rvChamas.visibility = View.GONE
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        })

        // Keep dummy stats as fallback
        binding.tvTotalSavingsAmount.text = "KES 150,000"
        binding.tvActiveLoansValue.text = "2 loans - KES 30,000"
        binding.tvUpcomingVotesValue.text = "3 pending"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

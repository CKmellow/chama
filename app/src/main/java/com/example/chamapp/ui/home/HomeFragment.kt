package com.example.chamapp.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chamapp.R
import com.example.chamapp.api.Chama
import com.example.chamapp.databinding.FragmentHomeBinding
import com.example.chamapp.util.SessionManager

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

        viewModel.fetchChamas(null)
    }

    private fun setupHeader() {
        val sessionManager = SessionManager(requireContext())
        val (firstName, lastName) = sessionManager.getUserName()
        val fullName = listOfNotNull(firstName, lastName).joinToString(" ").ifBlank { "User" }

        // The XML IDs are `tv_greeting` and `tv_greeting_subtext`.
        // View Binding converts these to the properties `tvGreeting` and `tvGreetingSubtext`.
        // This code is now correct and matches the layout file.
        binding.tvGreeting.text = "👋 Hi, $fullName"
        binding.tvGreetingSubtext.text = "Welcome back to your Chama dashboard"

        binding.ivNotificationBell.setOnClickListener {
            // TODO: Navigate to notifications screen
        }
    }

    private fun setupRecyclerView() {
        binding.rvChamas.layoutManager = LinearLayoutManager(requireContext())
        if (binding.rvChamas.adapter == null) {
            binding.rvChamas.adapter = ChamaAdapter(emptyList()) { chama ->
                try {
                    val action = HomeFragmentDirections.actionHomeFragmentToChamaDashboardFragment(
                        chama.chama_name ?: "-",
                        chama.role ?: "-",
                        chama.myContributions?.toString() ?: "-",
                        chama.totalBalance?.toString() ?: "-",
                        chama.status ?: "-",
                        chama.statusColor ?: "#388E3C",
                        chama.nextMeeting ?: "-"
                    )
                    findNavController().navigate(action)
                } catch (e: Exception) {
                    Toast.makeText(requireContext(), "Navigation error: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun setupButtons() {
        binding.cardMySavings.setOnClickListener {
            // TODO: Navigate to My Savings
        }
        binding.cardAnalytics.setOnClickListener {
            // TODO: Navigate to Analytics
        }
        binding.cardMyChamas.setOnClickListener {
            // TODO: Navigate to My Chamas list
        }
        binding.cardUpdates.setOnClickListener {
            // TODO: Navigate to Updates
        }
    }

    private fun observeViewModel() {
        viewModel.chamas.observe(viewLifecycleOwner, Observer { chamas ->
            try {
                if (chamas.isNullOrEmpty()) {
                    val dummyChamas = listOf(
                        Chama(
                            id = 0,
                            chama_name = "Test Chama",
                            description = "A test chama for UI check",
                            chama_type = null,
                            invitation_code = null,
                            is_invitation_code_active = null,
                            monthly_contribution_amount = 1000.0,
                            contribution_frequency = null,
                            contribution_due_day = "Tomorrow",
                            loan_interest_rate = null,
                            max_loan_multiplier = null,
                            loan_max_term_months = null,
                            meeting_frequency = null,
                            meeting_day = null,
                            created_by = null,
                            created_at = null,
                            updated_at = null,
                            is_active = null,
                            total_balance = null,
                            role = "Member",
                            myContributions = 1000.0,
                            totalBalance = 20000.0,
                            status = "Active",
                            statusColor = "#388E3C",
                            nextMeeting = "Tomorrow"
                        )
                    )
                    binding.rvChamas.visibility = View.VISIBLE
                    (binding.rvChamas.adapter as? ChamaAdapter)?.updateChamas(dummyChamas)
                    Toast.makeText(requireContext(), "No chamas found, showing dummy data", Toast.LENGTH_SHORT).show()
                } else {
                    binding.rvChamas.visibility = View.VISIBLE
                    (binding.rvChamas.adapter as? ChamaAdapter)?.updateChamas(chamas)
                    Toast.makeText(requireContext(), "Loaded ${chamas.size} chamas", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Data error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        })

        viewModel.error.observe(viewLifecycleOwner, Observer { errorMessage ->
            errorMessage?.let {
                // Handle error display
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

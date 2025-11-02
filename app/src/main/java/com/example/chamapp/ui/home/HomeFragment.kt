package com.example.chamapp.ui.home

import android.content.Context
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

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // Use the by viewModels() delegate to get a reference to the ViewModel
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

        // Setup UI components
        setupHeader()
        setupRecyclerView()

        // Observe LiveData from the ViewModel
        observeViewModel()

        // Fetch the token and trigger the data fetch
        val prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val token = prefs.getString("access_token", null) // CORRECT KEY: "access_token"

        if (token == null) {
            // Handle case where user is not logged in, maybe navigate to login
            Toast.makeText(requireContext(), "Authentication token not found. Please log in.", Toast.LENGTH_LONG).show()
            // findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
            return
        }

        // Tell the ViewModel to fetch the chamas
        viewModel.fetchChamas(token)
    }

    private fun setupHeader() {
        val prefs = requireContext().getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
        val userName = prefs.getString("first_name", "User") ?: "User"
        binding.tvGreeting.text = "Welcome back, $userName!"
        binding.tvDate.text = "Nov 2, 2025" // Example date
    }

    private fun setupRecyclerView() {
        // Initialize the adapter with an empty list. It will be updated by the ViewModel.
        binding.rvChamas.layoutManager = LinearLayoutManager(requireContext())
        binding.rvChamas.adapter = ChamaAdapter(emptyList()) { chama ->
            // Handle chama item click
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

    private fun observeViewModel() {
        viewModel.chamas.observe(viewLifecycleOwner, Observer { chamas ->
            if (chamas.isNullOrEmpty()) {
                binding.tvNoChamas.visibility = View.VISIBLE
                binding.rvChamas.visibility = View.GONE
            } else {
                binding.tvNoChamas.visibility = View.GONE
                binding.rvChamas.visibility = View.VISIBLE
                // Map API Chama to UI Chama before updating adapter
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
            }
        })

        viewModel.error.observe(viewLifecycleOwner, Observer { errorMessage ->
            errorMessage?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
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

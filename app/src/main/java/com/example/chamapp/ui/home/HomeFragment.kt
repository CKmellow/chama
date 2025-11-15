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
import com.example.chamapp.data.Chama
import com.example.chamapp.databinding.FragmentHomeBinding
import com.example.chamapp.util.SessionManager
import androidx.navigation.NavOptions

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var sessionManager: SessionManager

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

        sessionManager = SessionManager(requireContext())

        setupGreeting()
        setupRecyclerView()
        setupObservers()

        // Fetch chamas using token
        viewModel.fetchChamas(sessionManager.getAuthToken() ?: "")

        val cardSavings = view.findViewById<View>(R.id.card_savings)
        val cardAnalytics = view.findViewById<View>(R.id.card_analytics)
        val cardChamas = view.findViewById<View>(R.id.card_chamas)
        val cardUpdates = view.findViewById<View>(R.id.card_updates)
        val imgNotificationBell = view.findViewById<View>(R.id.img_notification_bell)
        val drawerLayout = view.findViewById<androidx.drawerlayout.widget.DrawerLayout>(R.id.drawer_layout)
        val hamburgerMenu = view.findViewById<View>(R.id.iv_hamburger_menu)
        hamburgerMenu.setOnClickListener {
            drawerLayout?.openDrawer(androidx.core.view.GravityCompat.START)
        }
        imgNotificationBell.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_notificationsFragment)
        }
        cardSavings.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_mySavingsFragment)
        }
        cardAnalytics.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_analyticsFragment)
        }
        cardChamas.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_myChamasFragment)
        }
        cardUpdates.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_updatesFragment)
        }

        // Sidebar menu navigation
        binding.navView.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_join_chama -> {
                    findNavController().navigate(R.id.action_homeFragment_to_joinChamaFragment)
                    true
                }
                R.id.nav_my_chamas -> {
                    findNavController().navigate(R.id.action_homeFragment_to_myChamasFragment)
                    true
                }
                R.id.nav_invest -> {
                    findNavController().navigate(R.id.action_homeFragment_to_investFragment)
                    true
                }
                R.id.nav_create_chama -> {
                    findNavController().navigate(R.id.action_homeFragment_to_createChamaFragment)
                    true
                }
                R.id.nav_profile -> {
                    findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
                    true
                }
                R.id.nav_logout -> {
                    // Clear user session
                    sessionManager.clearSession()

                    // Navigate to splash screen and clear the entire back stack
                    val navOptions = NavOptions.Builder()
                        .setPopUpTo(R.id.nav_graph, true)
                        .build()
                    findNavController().navigate(R.id.splashFragment, null, navOptions)
                    true
                }
                else -> false
            }
        }
    }

    private fun setupGreeting() {
        val username = sessionManager.getUserName()
        binding.tvGreetingHi.setText(R.string.greeting_hi)
        binding.tvGreetingName.text = username.first ?: ""
        binding.tvSubtext.setText(R.string.greeting_subtext)
    }

    private fun setupRecyclerView() {
        binding.rvChamas.layoutManager = LinearLayoutManager(requireContext())
        if (binding.rvChamas.adapter == null) {
            binding.rvChamas.adapter = ChamaAdapter(emptyList())
        }
    }

    private fun setupObservers() {
        viewModel.chamas.observe(viewLifecycleOwner, Observer { chamas ->
            try {
                if (chamas.isNullOrEmpty()) {
                    val dummy: List<Chama> = listOf(
                        Chama(
                            id = "0",
                            name = "Test Chama",
                            role = "Member",
                            myContributions = "1000",
                            totalBalance = "20000",
                            status = "Active",
                            statusColor = "#388E3C",
                            nextMeeting = "Tomorrow",
                            members = null
                        )
                    )
                    binding.rvChamas.visibility = View.VISIBLE
                    (binding.rvChamas.adapter as? ChamaAdapter)?.updateChamas(dummy)
                    Toast.makeText(requireContext(), "No chamas found, showing dummy data", Toast.LENGTH_SHORT).show()
                } else {
                    binding.rvChamas.visibility = View.VISIBLE
                    val mapped: List<Chama> = chamas.map { apiChama ->
                        Chama(
                            id = apiChama.id,
                            name = apiChama.chama_name,
                            role = apiChama.role,
                            myContributions = apiChama.myContributions?.toString(),
                            totalBalance = apiChama.totalBalance?.toString(),
                            status = apiChama.status,
                            statusColor = apiChama.statusColor,
                            nextMeeting = apiChama.nextMeeting,
                            members = apiChama.members?.map { member ->
                                com.example.chamapp.data.ChamaMemberRelation(
                                    userId = member.userId,
                                    firstName = member.firstName,
                                    lastName = member.lastName,
                                    role = member.role,
                                    contributionAmount = member.contributionAmount,
                                    status = member.status,
                                    email = member.email,
                                    phoneNumber = member.phoneNumber
                                )
                            }
                        )
                    }
                    (binding.rvChamas.adapter as? ChamaAdapter)?.updateChamas(mapped)
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Data error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        })
        viewModel.error.observe(viewLifecycleOwner, Observer { error ->
            error?.let {
                // TODO: handle error UI
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

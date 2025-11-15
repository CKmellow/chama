package com.example.chamapp.ui.chama

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.fragment.app.activityViewModels
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
        binding.tvChamaName.text = args.chamaName
        binding.tvUserRole.text = getString(
            R.string.chama_role_format,
            args.role
        )
        binding.tvChamaBalance.text = getString(
            R.string.chama_balance_format,
            args.totalBalance
        )
        
        // Store chamaName for later use
        val chamaNameArg = args.chamaName
        android.util.Log.d("ChamaDashboardFragment", "onViewCreated called with chamaName: $chamaNameArg")
        
        // Find button by ID directly - more reliable than binding sometimes
        val viewAllButton = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_view_all_members)
        android.util.Log.d("ChamaDashboardFragment", "Button from findViewById: ${viewAllButton != null}")
        android.util.Log.d("ChamaDashboardFragment", "Button from binding: ${binding.btnViewAllMembers != null}")
        
        if (viewAllButton == null) {
            android.util.Log.e("ChamaDashboardFragment", "CRITICAL: Button not found! Cannot set click listener.")
            return
        }
        
        // Enable button immediately so clicks can be registered
        viewAllButton.isEnabled = true
        viewAllButton.isClickable = true
        viewAllButton.alpha = 1.0f
        viewAllButton.visibility = View.VISIBLE
        android.util.Log.d("ChamaDashboardFragment", "Button enabled and visible. Button exists: ${viewAllButton != null}")
        
        // Set up multiple listeners to ensure we catch the click
        // OnClickListener
        viewAllButton.setOnClickListener {
            // Show toast immediately to confirm click is registered
            android.widget.Toast.makeText(requireContext(), "Button clicked!", android.widget.Toast.LENGTH_SHORT).show()
            android.util.Log.d("ChamaDashboardFragment", "=== BUTTON CLICKED ===")
            android.util.Log.d("ChamaDashboardFragment", "Searching for chama with name: $chamaNameArg")
            
            // Find the chama from the current LiveData value
            val currentChamas = viewModel.chamas.value
            android.util.Log.d("ChamaDashboardFragment", "Current chamas list: $currentChamas")
            android.util.Log.d("ChamaDashboardFragment", "Chamas list size: ${currentChamas?.size ?: 0}")
            
            // Try matching by name first, then by ID
            var chama = currentChamas?.find { it.chama_name == chamaNameArg }
            if (chama == null) {
                // Maybe chamaNameArg is actually an ID
                chama = currentChamas?.find { it.id == chamaNameArg }
                android.util.Log.d("ChamaDashboardFragment", "Tried matching by ID. Found: $chama")
            } else {
                android.util.Log.d("ChamaDashboardFragment", "Matched by name. Found: $chama")
            }
            
            // Log all chama names and IDs for debugging
            currentChamas?.forEachIndexed { index, ch ->
                android.util.Log.d("ChamaDashboardFragment", "Chama[$index]: id='${ch.id}', name='${ch.chama_name}'")
            }
            
            if (chama != null) {
                android.util.Log.d("ChamaDashboardFragment", "btnViewAllMembers clicked. Passing chama: $chama")
                try {
                    val bundle = Bundle().apply { putParcelable("chama", chama) }
                    android.util.Log.d("ChamaDashboardFragment", "Attempting navigation to allMembersFragment")
                    findNavController().navigate(R.id.action_chamaDashboardFragment_to_allMembersFragment, bundle)
                    android.util.Log.d("ChamaDashboardFragment", "Navigation successful!")
                } catch (e: Exception) {
                    android.util.Log.e("ChamaDashboardFragment", "Navigation error: ${e.message}", e)
                    e.printStackTrace()
                    android.widget.Toast.makeText(requireContext(), "Navigation error: ${e.message}", android.widget.Toast.LENGTH_LONG).show()
                }
            } else {
                android.util.Log.e("ChamaDashboardFragment", "Chama not found when button clicked. chamaNameArg: $chamaNameArg")
                android.util.Log.e("ChamaDashboardFragment", "Available chamas: ${currentChamas?.map { "${it.id}:${it.chama_name}" }}")
                
                // Try to navigate anyway with just the chamaNameArg as a fallback
                if (currentChamas != null && currentChamas.isNotEmpty()) {
                    android.util.Log.d("ChamaDashboardFragment", "Chamas available but not matched. Using first chama as fallback.")
                    val fallbackChama = currentChamas.first()
                    try {
                        val bundle = Bundle().apply { putParcelable("chama", fallbackChama) }
                        findNavController().navigate(R.id.action_chamaDashboardFragment_to_allMembersFragment, bundle)
                        android.widget.Toast.makeText(requireContext(), "Navigating to members list", android.widget.Toast.LENGTH_SHORT).show()
                    } catch (e: Exception) {
                        android.util.Log.e("ChamaDashboardFragment", "Fallback navigation error: ${e.message}", e)
                        android.widget.Toast.makeText(requireContext(), "Navigation error: ${e.message}", android.widget.Toast.LENGTH_LONG).show()
                    }
                } else {
                    android.widget.Toast.makeText(requireContext(), "Chama details not loaded yet. Please wait.", android.widget.Toast.LENGTH_SHORT).show()
                }
            }
        }
        
        // Also set onTouchListener as backup
        viewAllButton.setOnTouchListener { v, event ->
            if (event.action == android.view.MotionEvent.ACTION_DOWN) {
                android.util.Log.d("ChamaDashboardFragment", "=== BUTTON TOUCHED ===")
                android.widget.Toast.makeText(requireContext(), "Button touched!", android.widget.Toast.LENGTH_SHORT).show()
            }
            false // Let onClickListener handle it
        }
        
        // Also set it on the binding version just in case
        binding.btnViewAllMembers.setOnClickListener {
            android.widget.Toast.makeText(requireContext(), "Binding button clicked!", android.widget.Toast.LENGTH_SHORT).show()
            viewAllButton.performClick() // Trigger the main click handler
        }
        
        android.util.Log.d("ChamaDashboardFragment", "All click listeners set on button")
        
        // Observe chamas to enable/disable the button
        android.util.Log.d("ChamaDashboardFragment", "Setting up observer for chamas.")
        viewModel.chamas.observe(viewLifecycleOwner) { chamas ->
            android.util.Log.d("ChamaDashboardFragment", "Observer triggered. chamas: $chamas")
            android.util.Log.d("ChamaDashboardFragment", "Chamas list size: ${chamas?.size ?: 0}")
            
            if (chamas != null) {
                // Log all chama names and IDs
                chamas.forEachIndexed { index, ch ->
                    android.util.Log.d("ChamaDashboardFragment", "Chama[$index]: id='${ch.id}', name='${ch.chama_name}'")
                }
            }
            
            // Try matching by name first, then by ID
            var chama = chamas?.find { it.chama_name == chamaNameArg }
            if (chama == null) {
                chama = chamas?.find { it.id == chamaNameArg }
                android.util.Log.d("ChamaDashboardFragment", "Looking for chama with name/ID: $chamaNameArg. Tried ID match. Found: $chama")
            } else {
                android.util.Log.d("ChamaDashboardFragment", "Looking for chama with name: $chamaNameArg. Matched by name. Found: $chama")
            }
            
            // Update button reference in observer as well
            val buttonInObserver = view?.findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_view_all_members) ?: binding.btnViewAllMembers
            
            if (chama != null) {
                buttonInObserver.isEnabled = true
                android.util.Log.d("ChamaDashboardFragment", "btnViewAllMembers ENABLED for chama: $chama")
            } else {
                // Keep button enabled even if chama not found - let click handler deal with it
                buttonInObserver.isEnabled = true
                android.util.Log.e("ChamaDashboardFragment", "Chama not found, but keeping button enabled. chamaNameArg: $chamaNameArg")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

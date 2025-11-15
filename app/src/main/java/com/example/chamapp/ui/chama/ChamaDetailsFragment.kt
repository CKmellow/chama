
package com.example.chamapp.ui.chama
import androidx.navigation.fragment.findNavController
import com.example.chamapp.R
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.example.chamapp.databinding.FragmentChamaDetailsBinding

class ChamaDetailsFragment : Fragment() {
    private val viewModel: com.example.chamapp.ui.home.HomeViewModel by activityViewModels()

    private var _binding: FragmentChamaDetailsBinding? = null
    private val binding get() = _binding!!
    private var isNavigating = false // Prevent multiple navigations

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentChamaDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Get chamaId from arguments
        val chamaId = arguments?.getString("chamaId")
        android.util.Log.d("ChamaDetailsFragment", "onViewCreated called with chamaId: $chamaId")
        
        // Find button by ID directly - more reliable than binding sometimes
        val viewAllButton = view.findViewById<com.google.android.material.button.MaterialButton>(R.id.btn_view_all_members)
        android.util.Log.d("ChamaDetailsFragment", "Button from findViewById: ${viewAllButton != null}")
        android.util.Log.d("ChamaDetailsFragment", "Button from binding: ${binding.btnViewAllMembers != null}")
        
        if (viewAllButton == null) {
            android.util.Log.e("ChamaDetailsFragment", "CRITICAL: Button not found! Cannot set click listener.")
            return
        }
        
        // Enable button immediately so clicks can be registered
        viewAllButton.isEnabled = true
        viewAllButton.isClickable = true
        viewAllButton.alpha = 1.0f
        viewAllButton.visibility = View.VISIBLE
        android.util.Log.d("ChamaDetailsFragment", "Button enabled and visible. Button exists: ${viewAllButton != null}")
        
        // Set up the click listener ONCE - remove all other listeners
        viewAllButton.setOnClickListener {
            // Prevent multiple clicks
            if (isNavigating) {
                android.util.Log.d("ChamaDetailsFragment", "Already navigating, ignoring click")
                return@setOnClickListener
            }
            
            try {
                android.util.Log.d("ChamaDetailsFragment", "=== BUTTON CLICKED ===")
                isNavigating = true
                
                // Get chamaId to pass
                val idToPass = chamaId ?: viewModel.chamas.value?.firstOrNull()?.id
                
                if (!idToPass.isNullOrEmpty()) {
                    android.util.Log.d("ChamaDetailsFragment", "Navigating with chamaId: $idToPass")
                    try {
                        val bundle = Bundle().apply {
                            putString("chamaId", idToPass)
                        }
                        findNavController().navigate(R.id.action_chamaDetailsFragment_to_allMembersFragment, bundle)
                        android.util.Log.d("ChamaDetailsFragment", "Navigation successful!")
                        // Disable button to prevent further clicks
                        viewAllButton.isEnabled = false
                    } catch (e: Exception) {
                        isNavigating = false // Reset on error
                        android.util.Log.e("ChamaDetailsFragment", "Navigation error: ${e.message}", e)
                        e.printStackTrace()
                    }
                } else {
                    isNavigating = false // Reset
                    android.util.Log.e("ChamaDetailsFragment", "No chamaId available")
                }
            } catch (e: Exception) {
                isNavigating = false // Reset on error
                android.util.Log.e("ChamaDetailsFragment", "Error in click handler: ${e.message}", e)
                e.printStackTrace()
            }
        }
        
        // REMOVED duplicate listeners - only one onClickListener is needed
        // Do NOT set listeners on binding.btnViewAllMembers or use onTouchListener
        
        android.util.Log.d("ChamaDetailsFragment", "Click listener set on button (single listener only)")
        
        // Observe chamas to enable/disable the button (but keep it enabled)
        viewModel.chamas.observe(viewLifecycleOwner) { chamas ->
            // Find the actual chama object by id
            val chama = chamas?.find { it.id == chamaId }
            binding.tvChamaName.text = chama?.chama_name ?: "Unknown"
            binding.tvChamaBalance.text = getString(R.string.chama_balance_format, chama?.totalBalance ?: 0.0)
            viewAllButton.isEnabled = true
            binding.btnViewAllMembers.isEnabled = true
            if (chama != null) {
                android.util.Log.d("ChamaDetailsFragment", "btnViewAllMembers enabled for chama: $chama")
            } else {
                android.util.Log.e("ChamaDetailsFragment", "Chama not found, but keeping button enabled. chamaId: $chamaId")
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

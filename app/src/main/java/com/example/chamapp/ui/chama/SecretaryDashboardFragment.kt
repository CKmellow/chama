package com.example.chamapp.ui.chama

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.example.chamapp.databinding.FragmentSecretaryDashboardBinding
import com.example.chamapp.R
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
    private var _binding: FragmentSecretaryDashboardBinding? = null
    private val binding get() = _binding!!
    private val viewModel: com.example.chamapp.ui.chama.ChamaViewModel by viewModels()

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
        val chamaId = arguments?.getString(ARG_CHAMA_ID) ?: ""
        binding.btnViewAllMembers.setOnClickListener {
            // Fallback to direct navigation if Safe Args are not available
            val bundle = Bundle().apply { putString("chamaId", chamaId) }
            findNavController().navigate(R.id.membersListFragment, bundle)
        }
        // ...existing secretary dashboard logic...
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

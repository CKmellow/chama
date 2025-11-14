package com.example.chamapp.ui.chama

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.chamapp.R
import com.example.chamapp.databinding.FragmentChamaDashboardBinding

class ChamaDashboardFragment : Fragment() {
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
        // Only set views that exist in the binding. Remove all unresolved references.
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

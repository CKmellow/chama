package com.example.chamapp.ui.chama

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chamapp.databinding.FragmentMyChamasBinding
import com.example.chamapp.R

import androidx.fragment.app.viewModels
import com.example.chamapp.ui.home.HomeViewModel
import com.example.chamapp.ui.home.ChamaAdapter
import com.example.chamapp.util.SessionManager

class MyChamasFragment : Fragment() {
    private var _binding: FragmentMyChamasBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeViewModel by viewModels()
    private lateinit var chamaAdapter: ChamaAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyChamasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvMyChamas.layoutManager = LinearLayoutManager(requireContext())
        chamaAdapter = ChamaAdapter(emptyList()) { chama ->
            // Handle chama click if needed
        }
        binding.rvMyChamas.adapter = chamaAdapter

        val sessionManager = SessionManager(requireContext())
        val token = sessionManager.getAuthToken() ?: ""
        viewModel.fetchMyChamas(token)
            viewModel.chamas.observe(viewLifecycleOwner) { chamas ->
                chamaAdapter.updateChamas(chamas ?: emptyList())
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

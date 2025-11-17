package com.example.chamapp.ui.chama

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chamapp.R
import com.example.chamapp.databinding.FragmentMyChamasBinding
import com.example.chamapp.ui.home.ChamaAdapter

class MyChamasFragment : Fragment() {
    private var _binding: FragmentMyChamasBinding? = null
    private val binding get() = _binding!!
    private val viewModel: MyChamasViewModel by viewModels()
    private lateinit var adapter: ChamaAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyChamasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        adapter = ChamaAdapter(emptyList()) { chama ->
            val role = chama.role?.lowercase() ?: "user"
            if (role in listOf("secretary", "treasurer", "chairperson", "chairman")) {
                val action = MyChamasFragmentDirections.actionMyChamasFragmentToChamaDashboardFragment(
                    chama.id ?: "",
                    chama.name ?: "",
                    role,
                    chama.myContributions ?: "",
                    chama.totalBalance ?: "",
                    chama.status ?: "",
                    chama.statusColor ?: "",
                    chama.nextMeeting ?: ""
                )
                findNavController().navigate(action)
            } else {
                val action = MyChamasFragmentDirections.actionMyChamasFragmentToChamaDetailsFragment(
                    chama.id ?: "",
                    chama.name ?: "",
                    role
                )
                findNavController().navigate(action)
            }
        }
        binding.rvMyChamas.layoutManager = LinearLayoutManager(requireContext())
        binding.rvMyChamas.adapter = adapter

        viewModel.chamas.observe(viewLifecycleOwner) { chamas ->
            adapter.updateChamas(chamas)
        }
        viewModel.loading.observe(viewLifecycleOwner) { isLoading ->
            binding.rvMyChamas.visibility = if (isLoading) View.GONE else View.VISIBLE
            // Optionally show a loading indicator
        }
        viewModel.error.observe(viewLifecycleOwner) { errorMsg ->
            errorMsg?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
            }
        }
        viewModel.fetchChamas()

        binding.ivBackArrow.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

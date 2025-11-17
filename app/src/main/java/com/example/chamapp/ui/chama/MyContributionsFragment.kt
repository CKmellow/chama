package com.example.chamapp.ui.chama

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chamapp.databinding.FragmentMyContributionsBinding
import com.example.chamapp.api.RetrofitClient
import com.example.chamapp.util.SessionManager
import kotlinx.coroutines.launch

class MyContributionsFragment : Fragment() {
    private var _binding: FragmentMyContributionsBinding? = null
    private val binding get() = _binding!!
    private var chamaId: String? = null
    private var userId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyContributionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        chamaId = arguments?.getString("chamaId")
        userId = SessionManager(requireContext()).getUserId()
        binding.ivBackArrow.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
        binding.rvContributions.layoutManager = LinearLayoutManager(requireContext())
        fetchContributions()
    }

    private fun fetchContributions() {
        val cId = chamaId ?: return
        val uId = userId ?: return
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.getUserContributions(uId, cId)
                if (response.isSuccessful && response.body()?.contributions != null) {
                    val contributions = response.body()!!.contributions!!
                    binding.rvContributions.adapter = ContributionsAdapter(contributions)
                } else {
                    Toast.makeText(requireContext(), "No contributions found", Toast.LENGTH_LONG).show()
                }
            } catch (e: Exception) {
                Toast.makeText(requireContext(), "Error: ${e.message}", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

// TODO: Implement ContributionsAdapter for RecyclerView

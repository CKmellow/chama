package com.example.chamapp.ui.secretary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chamapp.databinding.FragmentSecretaryDashboardBinding

class SecretaryDashboardFragment : Fragment() {

    private var _binding: FragmentSecretaryDashboardBinding? = null
    private val binding get() = _binding!!

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

        // Set up the RecyclerView
        binding.rvMembers.layoutManager = LinearLayoutManager(requireContext())

        // Set up static data for now
        binding.tvChamaName.text = "Sample Chama"
        val members = listOf("Member 1", "Member 2", "Member 3")
        binding.rvMembers.adapter = MemberAdapter(members)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
package com.example.chamapp.ui.chama

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chamapp.databinding.FragmentMembersListBinding
import com.example.chamapp.data.ChamaMember
import com.example.chamapp.api.Chama
import com.example.chamapp.ui.home.HomeViewModel
import com.example.chamapp.ui.chama.MemberAdapter
import com.example.chamapp.R

class MembersListFragment : Fragment() {
    companion object {
        private const val ARG_CHAMA_ID = "chamaId"
        fun newInstance(chamaId: String): MembersListFragment {
            val fragment = MembersListFragment()
            val args = Bundle()
            args.putString(ARG_CHAMA_ID, chamaId)
            fragment.arguments = args
            return fragment
        }
    }

    private val viewModel: com.example.chamapp.ui.home.HomeViewModel by viewModels()

    private var _binding: FragmentMembersListBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMembersListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.rvMembersList.layoutManager = LinearLayoutManager(requireContext())

        val chamaId = arguments?.getString(ARG_CHAMA_ID) ?: ""
        viewModel.chamas.observe(viewLifecycleOwner) { chamasList ->
            val chama = chamasList?.firstOrNull { it.id == chamaId }
            // Chama does not have members property. Show empty list or fetch members separately.
            binding.rvMembersList.adapter = MemberAdapter(emptyList())
            // TODO: Fetch members for this chama if needed
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

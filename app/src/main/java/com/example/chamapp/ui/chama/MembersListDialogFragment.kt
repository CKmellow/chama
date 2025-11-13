package com.example.chamapp.ui.chama

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chamapp.api.ChamaMemberRelation
import com.example.chamapp.databinding.DialogMembersListBinding

class MembersListDialogFragment : DialogFragment() {

    private var _binding: DialogMembersListBinding? = null
    private val binding get() = _binding!!

    private var members: List<ChamaMemberRelation>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            members = it.getParcelableArrayList(ARG_MEMBERS)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogMembersListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (members.isNullOrEmpty()) {
            binding.tvNoMembers.visibility = View.VISIBLE
            binding.rvMembers.visibility = View.GONE
        } else {
            binding.tvNoMembers.visibility = View.GONE
            binding.rvMembers.visibility = View.VISIBLE
            binding.rvMembers.layoutManager = LinearLayoutManager(context)
            binding.rvMembers.adapter = MembersAdapter(members!!)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_MEMBERS = "members"

        fun newInstance(members: List<ChamaMemberRelation>): MembersListDialogFragment {
            val fragment = MembersListDialogFragment()
            val args = Bundle()
            args.putParcelableArrayList(ARG_MEMBERS, ArrayList(members))
            fragment.arguments = args
            return fragment
        }
    }
}

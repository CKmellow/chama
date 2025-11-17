package com.example.chamapp.ui.analytics
import com.example.chamapp.databinding.ItemAnalyticsMemberBinding

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chamapp.databinding.FragmentAnalyticsBinding
import com.example.chamapp.ui.chamas.AnalyticsAdapter
import com.example.chamapp.api.RetrofitClient
import com.example.chamapp.api.ChamaMemberRelation
import kotlinx.coroutines.launch

class AnalyticsFragment : Fragment() {
    private var _binding: FragmentAnalyticsBinding? = null
    private val binding get() = _binding!!

    // Removed adapter for RecyclerView
    private var chamaId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAnalyticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        chamaId = arguments?.getString("chamaId")
        if (chamaId != null) {
            fetchChamaMembers(chamaId!!)
        }
    }

    private fun fetchChamaMembers(chamaId: String) {
        lifecycleScope.launch {
            try {
                val response = RetrofitClient.instance.getChamaMembers(chamaId)
                if (response.isSuccessful && response.body()?.members != null) {
                    val members: List<ChamaMemberRelation> = response.body()!!.members!!
                    val uniqueMembers = members.distinctBy { it.id }

                    // Dynamically add user cards below the wavy appbar
                    val container = binding.userCardsContainer
                    container.removeAllViews() // Clear once before adding new cards
                    val inflater = LayoutInflater.from(requireContext())
                    uniqueMembers.forEach { member ->
                        val cardBinding = ItemAnalyticsMemberBinding.inflate(inflater, container, false)
                        cardBinding.tvMemberName.text = member.name ?: "Unknown"
                        cardBinding.tvContributionAmount.text = "Contribution: Loading..."
                        container.addView(cardBinding.root)

                        // Fetch and display contribution amount for each member
                        val chamaIdForApi = member.id
                        val userIdForApi = member.userId
                        lifecycleScope.launch {
                            try {
                                val contribResponse = RetrofitClient.instance.getUserContributions(userIdForApi, chamaIdForApi)
                                var contributionText = "N/A"
                                if (contribResponse.isSuccessful && contribResponse.body()?.contributions != null) {
                                    val total = contribResponse.body()!!.contributions?.sumOf { it.amount ?: 0.0 } ?: 0.0
                                    contributionText = total.toString()
                                }
                                cardBinding.tvContributionAmount.text = "Contribution: $contributionText"
                            } catch (e: Exception) {
                                cardBinding.tvContributionAmount.text = "Contribution: N/A"
                            }
                        }
                    }
                } else {
                    // Log or handle failure if needed
                }
            } catch (e: Exception) {
                // Log or handle error if needed
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

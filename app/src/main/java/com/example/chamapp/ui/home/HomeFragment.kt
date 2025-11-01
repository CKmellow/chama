package com.example.chamapp.ui.home

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.chamapp.databinding.FragmentHomeBinding
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    // Simulate user role - set to true to see the treasurer's view
    private val isTreasurer = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (isTreasurer) {
            showTreasurerDashboard()
        }
    }

    private fun showTreasurerDashboard() {
        binding.cardTotalBalance.visibility = View.VISIBLE
        binding.cardRecentTransactions.visibility = View.VISIBLE
        binding.cardPendingLoans.visibility = View.VISIBLE
        binding.cardMembersBehind.visibility = View.VISIBLE

        setupBarChart()
    }

    private fun setupBarChart() {
        val entries = ArrayList<BarEntry>()
        entries.add(BarEntry(1f, 1000f))
        entries.add(BarEntry(2f, 2000f))
        entries.add(BarEntry(3f, 1500f))
        entries.add(BarEntry(4f, 2500f))
        entries.add(BarEntry(5f, 1800f))

        val dataSet = BarDataSet(entries, "Recent Transactions")
        dataSet.color = Color.BLUE
        dataSet.valueTextColor = Color.BLACK

        val barData = BarData(dataSet)
        binding.barChart.data = barData
        binding.barChart.setFitBars(true)
        binding.barChart.invalidate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
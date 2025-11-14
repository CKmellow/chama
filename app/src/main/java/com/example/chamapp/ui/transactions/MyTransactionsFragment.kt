package com.example.chamapp.ui.transactions

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.chamapp.databinding.FragmentMyTransactionsBinding

class MyTransactionsFragment : Fragment() {

    private var _binding: FragmentMyTransactionsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: MyTransactionsViewModel by viewModels()
    private lateinit var transactionsAdapter: TransactionsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyTransactionsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        observeViewModel()

        // Trigger the data fetch
        viewModel.fetchTransactions()
    }

    private fun setupRecyclerView() {
        transactionsAdapter = TransactionsAdapter(emptyList())
        binding.transactionsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = transactionsAdapter
        }
    }

    private fun observeViewModel() {
        viewModel.transactions.observe(viewLifecycleOwner) { transactions ->
            if (transactions.isEmpty()) {
                binding.errorTextView.text = "You have no transactions yet."
                binding.errorTextView.isVisible = true
            } else {
                transactionsAdapter.updateData(transactions)
                binding.errorTextView.isVisible = false
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.isVisible = isLoading
            // Hide recycler view while loading to prevent showing old data
            binding.transactionsRecyclerView.isVisible = !isLoading
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            if (error != null) {
                binding.errorTextView.text = error
                binding.errorTextView.isVisible = true
                binding.transactionsRecyclerView.isVisible = false
                Toast.makeText(context, error, Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

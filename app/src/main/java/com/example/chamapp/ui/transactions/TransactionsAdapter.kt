package com.example.chamapp.ui.transactions

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chamapp.R
import com.example.chamapp.api.Transaction
import java.text.SimpleDateFormat
import java.util.Locale

class TransactionsAdapter(private var transactions: List<Transaction>) : RecyclerView.Adapter<TransactionsAdapter.TransactionViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_transaction, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        val transaction = transactions[position]
        holder.bind(transaction)
    }

    override fun getItemCount() = transactions.size

    fun updateData(newTransactions: List<Transaction>) {
        transactions = newTransactions
        notifyDataSetChanged()
    }

    class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val amountTextView: TextView = itemView.findViewById(R.id.amountTextView)
        private val dateTextView: TextView = itemView.findViewById(R.id.dateTextView)

        // Adjusted date format to handle potential timezone 'Z'
        private val inputFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
        private val outputFormat = SimpleDateFormat("dd MMM yyyy, h:mm a", Locale.getDefault())

        fun bind(transaction: Transaction) {
            amountTextView.text = String.format(Locale.getDefault(), "Ksh %.2f", transaction.amount)
            try {
                val date = inputFormat.parse(transaction.contributed_at)
                dateTextView.text = date?.let { outputFormat.format(it) } ?: transaction.contributed_at
            } catch (e: Exception) {
                // Fallback if parsing fails
                dateTextView.text = transaction.contributed_at
            }
        }
    }
}

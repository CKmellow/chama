package com.example.chamapp.ui.chama

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chamapp.R
import com.example.chamapp.api.Contribution

class ContributionsAdapter(private val contributions: List<Contribution>) : RecyclerView.Adapter<ContributionsAdapter.ContributionViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ContributionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_contribution, parent, false)
        return ContributionViewHolder(view)
    }

    override fun onBindViewHolder(holder: ContributionViewHolder, position: Int) {
        val item = contributions[position]
        holder.tvAmount.text = "KES ${item.amount ?: 0.0}"
        holder.tvDate.text = item.contributed_at ?: "-"
        holder.tvStatus.text = item.status ?: "-"
    }

    override fun getItemCount(): Int = contributions.size

    class ContributionViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvAmount: TextView = view.findViewById(R.id.tv_amount)
        val tvDate: TextView = view.findViewById(R.id.tv_date)
        val tvStatus: TextView = view.findViewById(R.id.tv_status)
    }
}


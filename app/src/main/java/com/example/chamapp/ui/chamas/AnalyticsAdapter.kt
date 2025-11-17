package com.example.chamapp.ui.chamas

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chamapp.R
import com.example.chamapp.api.ChamaMemberRelation
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AnalyticsAdapter(private var members: List<ChamaMemberRelation>) : RecyclerView.Adapter<AnalyticsAdapter.AnalyticsViewHolder>() {
    class AnalyticsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.tvMemberName)
        val contribution: TextView = itemView.findViewById(R.id.tvContributionAmount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AnalyticsViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_analytics_member, parent, false)
        return AnalyticsViewHolder(view)
    }

    override fun onBindViewHolder(holder: AnalyticsViewHolder, position: Int) {
        val member = members[position]
        holder.name.text = member.name ?: "Unknown"
        holder.contribution.text = "Contribution: Loading..."
        val chamaId = member.id ?: return
        val userId = member.userId ?: return
        kotlinx.coroutines.GlobalScope.launch(kotlinx.coroutines.Dispatchers.Main) {
            try {
                val contribResponse = com.example.chamapp.api.RetrofitClient.instance.getUserContributions(userId, chamaId)
                var contributionText = "N/A"
                if (contribResponse.isSuccessful && contribResponse.body()?.contributions != null) {
                    val total = contribResponse.body()!!.contributions?.sumOf { it.amount ?: 0.0 } ?: 0.0
                    contributionText = total.toString()
                }
                holder.contribution.text = "Contribution: $contributionText"
            } catch (e: Exception) {
                holder.contribution.text = "Contribution: N/A"
            }
        }
    }

    override fun getItemCount(): Int = members.size

    fun updateData(newMembers: List<ChamaMemberRelation>) {
        members = newMembers
        notifyDataSetChanged()
    }
}

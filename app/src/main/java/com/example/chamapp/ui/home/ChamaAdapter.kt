package com.example.chamapp.ui.home

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chamapp.R
import com.example.chamapp.data.Chama

class ChamaAdapter(
    private var chamas: List<Chama>,
    private val onChamaClick: ((Chama) -> Unit)? = null
) : RecyclerView.Adapter<ChamaAdapter.ChamaViewHolder>() {

    fun updateChamas(newChamas: List<Chama>) {
        chamas = newChamas
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChamaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.chama_card_item, parent, false)
        return ChamaViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChamaViewHolder, position: Int) {
        val chama = chamas[position]
        holder.bind(chama)
        holder.itemView.setOnClickListener {
            onChamaClick?.invoke(chama)
        }
    }

    override fun getItemCount() = chamas.size

    class ChamaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(chama: Chama) {
            val nameView = itemView.findViewById<TextView>(R.id.tvChamaName)
            nameView.text = chama.name ?: "Unnamed Chama"
            val roleView = itemView.findViewById<TextView>(R.id.tvUserRole)
            roleView.text = chama.role ?: "Member"
            val myContributions = itemView.findViewById<TextView>(R.id.tvMyContributions)
            myContributions.text = "My Contributions: KES ${chama.myContributions ?: "-"}"
            val chamaBalance = itemView.findViewById<TextView>(R.id.tvChamaBalance)
            chamaBalance.text = "Chama Balance: KES ${chama.totalBalance ?: "-"}"
            val nextMeeting = itemView.findViewById<TextView>(R.id.tvNextMeeting)
            nextMeeting.text = "Next Meeting: ${chama.nextMeeting ?: "-"}"
            val statusView = itemView.findViewById<TextView>(R.id.tvStatus)
            statusView.text = chama.status ?: "-"
            val color = try {
                Color.parseColor(chama.statusColor ?: "#388E3C")
            } catch (e: Exception) {
                Color.WHITE
            }
            statusView.setTextColor(color)
        }
    }
}

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
    private var chamas: List<Chama>
) : RecyclerView.Adapter<ChamaAdapter.ChamaViewHolder>() {

    fun updateChamas(newChamas: List<Chama>) {
        chamas = newChamas
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChamaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_chama, parent, false)
        return ChamaViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChamaViewHolder, position: Int) {
        val chama = chamas[position]
        holder.bind(chama)
    }

    override fun getItemCount() = chamas.size

    class ChamaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        fun bind(chama: Chama) {
            val nameView = itemView.findViewById<TextView>(R.id.tv_chama_name)
            nameView.text = chama.name ?: "Unnamed Chama"
            val descView = itemView.findViewById<TextView>(R.id.tv_chama_description)
            descView.text = "Members: ${chama.members?.size ?: 0}"
            // Add more fields as needed

            val myContributions: TextView = itemView.findViewById(R.id.tv_your_contribution)
            myContributions.text = "KES ${chama.myContributions ?: "-"}"
            val nextMeeting: TextView = itemView.findViewById(R.id.tv_next_meeting)
            nextMeeting.text = "Next meeting: ${chama.nextMeeting ?: "-"}"
            val chamaStatus: TextView = itemView.findViewById(R.id.tv_chama_status)
            chamaStatus.text = chama.status ?: "-"

            val color = try {
                Color.parseColor(chama.statusColor ?: "#388E3C")
            } catch (e: Exception) {
                Color.BLACK
            }
            chamaStatus.setTextColor(color)
        }
    }
}

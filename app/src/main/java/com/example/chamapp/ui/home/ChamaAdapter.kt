package com.example.chamapp.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chamapp.R

// A dummy data class for now. This would be replaced by your actual Chama model.
data class Chama(val name: String, val memberCount: Int)

class ChamaAdapter(private val chamas: List<Chama>) : RecyclerView.Adapter<ChamaAdapter.ChamaViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChamaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chama, parent, false)
        return ChamaViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChamaViewHolder, position: Int) {
        val chama = chamas[position]
        holder.bind(chama)
    }

    override fun getItemCount() = chamas.size

    class ChamaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val chamaName: TextView = itemView.findViewById(R.id.tvChamaName)
        private val chamaMembers: TextView = itemView.findViewById(R.id.tvChamaMembers)

        fun bind(chama: Chama) {
            chamaName.text = chama.name
            chamaMembers.text = "${chama.memberCount} Members"
        }
    }
}

package com.example.chamapp.ui.home

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chamapp.R

// This data class now matches the detailed view in item_chama.xml
data class Chama(
    val name: String,
    val role: String,
    val myContributions: String,
    val totalBalance: String,
    val status: String,
    val statusColor: String, // To control the color of the status text
    val nextMeeting: String
)

class ChamaAdapter(
    private val chamas: List<Chama>,
    private val onChamaClick: (Chama) -> Unit
) : RecyclerView.Adapter<ChamaAdapter.ChamaViewHolder>() {

    private var chamaList: List<Chama> = chamas

    fun updateChamas(newChamas: List<Chama>) {
        chamaList = newChamas
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChamaViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_chama, parent, false)
        return ChamaViewHolder(view)
    }

    override fun onBindViewHolder(holder: ChamaViewHolder, position: Int) {
        val chama = chamaList[position]
        holder.bind(chama)
        holder.itemView.setOnClickListener { onChamaClick(chama) }
    }

    override fun getItemCount() = chamaList.size

    class ChamaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        // All TextViews from the new item_chama.xml layout
        private val chamaName: TextView = itemView.findViewById(R.id.tvChamaName)
        private val chamaRole: TextView = itemView.findViewById(R.id.tvChamaRole)
        private val myContributions: TextView = itemView.findViewById(R.id.tvMyContributions)
        private val totalBalance: TextView = itemView.findViewById(R.id.tvTotalBalance)
        private val chamaStatus: TextView = itemView.findViewById(R.id.tvChamaStatus)
        private val nextMeeting: TextView = itemView.findViewById(R.id.tvNextMeeting)
        private val btnJoin: Button = itemView.findViewById(R.id.btnJoinChama)

        fun bind(chama: Chama) {
            chamaName.text = chama.name
            chamaRole.text = chama.role
            myContributions.text = chama.myContributions
            totalBalance.text = chama.totalBalance
            chamaStatus.text = chama.status
            nextMeeting.text = chama.nextMeeting

            // Set the status color dynamically
            try {
                chamaStatus.setTextColor(Color.parseColor(chama.statusColor))
            } catch (e: IllegalArgumentException) {
                // Fallback to a default color if the string is invalid
                chamaStatus.setTextColor(Color.BLACK)
            }

            btnJoin.setOnClickListener {
                // TODO: Implement join chama logic here
            }
        }
    }
}

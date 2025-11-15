package com.example.chamapp.ui.home

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chamapp.R
import com.example.chamapp.api.ChamaMemberRelation

// Merged model (keeps their extended fields, your nullable-safe values)
data class Chama(
    val id: String,
    val name: String,
    val role: String? = null,
    val myContributions: String? = null,
    val totalBalance: String? = null,
    val status: String? = null,
    val statusColor: String? = "#388E3C",
    val nextMeeting: String? = null,
    val members: List<ChamaMemberRelation>? = null
)

class ChamaAdapter(
    private var chamas: List<Chama>,
    private val onChamaClick: (Chama) -> Unit
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
        holder.itemView.setOnClickListener { onChamaClick(chama) }
    }

    override fun getItemCount() = chamas.size

    class ChamaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        // ✔ Using YOUR layout element IDs
        private val chamaName: TextView = itemView.findViewById(R.id.tv_chama_name)
        private val chamaDescription: TextView = itemView.findViewById(R.id.tv_chama_description)
        private val myContributions: TextView = itemView.findViewById(R.id.tv_your_contribution)
        private val nextMeeting: TextView = itemView.findViewById(R.id.tv_next_meeting)
        private val chamaStatus: TextView = itemView.findViewById(R.id.tv_chama_status)

        fun bind(chama: Chama) {
            // ✔ Mapping their model fields into your UI structure
            chamaName.text = chama.name
            chamaDescription.text = chama.role ?: chama.totalBalance ?: "Chama Member"
            myContributions.text = "KES ${chama.myContributions ?: "-"}"
            nextMeeting.text = "Next meeting: ${chama.nextMeeting ?: "-"}"
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

package com.example.chamapp.ui.chama

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chamapp.R
import com.example.chamapp.data.ChamaMember

class MemberAdapter(private val members: List<ChamaMember>) : RecyclerView.Adapter<MemberAdapter.MemberViewHolder>() {
    init {
        android.util.Log.d("MemberAdapter", "Members passed to adapter: $members")
    }

    class MemberViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvName: TextView = itemView.findViewById(R.id.tv_member_name)
        val tvRole: TextView = itemView.findViewById(R.id.tv_member_role)
        val ivMenu: View = itemView.findViewById(R.id.iv_member_menu)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_member, parent, false)
        return MemberViewHolder(view)
    }

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        val member = members[position]
        val name = listOfNotNull(member.firstName, member.lastName).joinToString(" ")
        holder.tvName.text = if (name.isNotBlank()) {
            name
        } else if (!member.email.isNullOrBlank()) {
            member.email
        } else if (!member.userId.isNullOrBlank()) {
            "Member ${member.userId.take(8)}"
        } else {
            "Member"
        }
        holder.tvRole.text = member.role ?: "Member"
        // ...existing code...
    }

    override fun getItemCount(): Int = members.size
}

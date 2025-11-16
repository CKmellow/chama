package com.example.chamapp.ui.chama

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chamapp.R
import com.example.chamapp.api.ChamaMemberRelation

class MembersAdapter(private val members: List<ChamaMemberRelation>) : RecyclerView.Adapter<MembersAdapter.MemberViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_member, parent, false)
        return MemberViewHolder(view)
    }

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        val member = members[position]
        holder.bind(member)
    }

    override fun getItemCount() = members.size

    class MemberViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameTextView: TextView = itemView.findViewById(R.id.tv_member_name)
        private val emailTextView: TextView = itemView.findViewById(R.id.tv_member_email)
        private val roleTextView: TextView = itemView.findViewById(R.id.tv_member_role)
        private val phoneTextView: TextView = itemView.findViewById(R.id.tv_member_phone)
        private val statusTextView: TextView = itemView.findViewById(R.id.tv_member_status)

        fun bind(member: ChamaMemberRelation) {
            nameTextView.text = member.name ?: ""
            emailTextView.text = member.email ?: ""
            roleTextView.text = member.role ?: ""
            phoneTextView.text = member.phoneNumber?.toString() ?: ""
            statusTextView.text = member.status ?: ""
        }
    }
}

package com.example.chamapp.ui.secretary

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chamapp.R

data class Member(
    val name: String,
    val role: String,
    val contribution: String,
    val status: String,
    val email: String?,
    val phone: String?,
    val profilePictureUrl: String?
)

class MemberAdapter(private val members: List<Member>) : RecyclerView.Adapter<MemberAdapter.MemberViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MemberViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_member, parent, false)
        return MemberViewHolder(view)
    }

    override fun onBindViewHolder(holder: MemberViewHolder, position: Int) {
        val member = members[position]
        holder.memberName.text = member.name
        holder.memberRole.text = member.role
        holder.memberContribution.text = member.contribution
        holder.memberStatus.text = member.status
        holder.memberEmail.text = member.email ?: ""
        holder.memberPhone.text = member.phone ?: ""
        // TODO: Load profilePictureUrl into ImageView (holder.memberProfilePic) using Glide/Picasso
    }

    override fun getItemCount() = members.size

    class MemberViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val memberName: TextView = itemView.findViewById(R.id.tv_member_name)
    val memberRole: TextView = itemView.findViewById(R.id.tv_member_role)
    val memberContribution: TextView = itemView.findViewById(R.id.tv_member_contribution)
    val memberStatus: TextView = itemView.findViewById(R.id.tv_member_status)
    val memberEmail: TextView = itemView.findViewById(R.id.tv_member_email)
    val memberPhone: TextView = itemView.findViewById(R.id.tv_member_phone)
    // val memberProfilePic: ImageView = itemView.findViewById(R.id.iv_member_profile_pic)
    }
}
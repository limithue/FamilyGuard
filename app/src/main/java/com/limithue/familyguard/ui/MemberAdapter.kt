package com.limithue.familyguard.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.limithue.familyguard.data.model.FamilyMember
import com.limithue.familyguard.databinding.ItemMemberBinding

class MemberAdapter(
    private val onItemClick: (FamilyMember) -> Unit = {}
) : RecyclerView.Adapter<MemberAdapter.ViewHolder>() {

    private var items: List<FamilyMember> = emptyList()

    fun submitList(list: List<FamilyMember>) {
        items = list
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: ItemMemberBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(member: FamilyMember) {
            binding.tvAvatar.text = member.avatarText
            binding.tvMemberName.text = member.name
            binding.tvMemberRole.text = when (member.role) {
                FamilyMember.ROLE_ADMIN -> "管理员"
                FamilyMember.ROLE_CARED -> "被照护人"
                else -> "成员"
            } + " · 电量 ${member.battery}%"
            binding.tvMemberStatus.text = if (member.isOnline) "在线" else "离线"
            binding.tvMemberStatus.setTextColor(
                itemView.context.getColor(
                    if (member.isOnline) com.limithue.familyguard.R.color.accent
                    else com.limithue.familyguard.R.color.text_secondary
                )
            )
            binding.root.setOnClickListener { onItemClick(member) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemMemberBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position])

    override fun getItemCount(): Int = items.size
}

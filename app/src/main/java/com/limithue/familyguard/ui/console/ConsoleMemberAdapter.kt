package com.limithue.familyguard.ui.console

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.limithue.familyguard.R
import com.limithue.familyguard.data.model.FamilyMember
import com.limithue.familyguard.databinding.ItemConsoleMemberBinding

class ConsoleMemberAdapter(
    private val onRoleClick: (FamilyMember) -> Unit,
    private val onRemoveClick: (FamilyMember) -> Unit
) : RecyclerView.Adapter<ConsoleMemberAdapter.ViewHolder>() {

    private var items: List<FamilyMember> = emptyList()

    fun submitList(list: List<FamilyMember>) {
        items = list
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: ItemConsoleMemberBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(member: FamilyMember) {
            binding.tvCmAvatar.text = member.avatarText
            binding.tvCmName.text = member.name
            binding.tvCmRole.text = when (member.role) {
                FamilyMember.ROLE_ADMIN -> "管理员"
                FamilyMember.ROLE_CARED -> "被照护人"
                else -> "普通成员"
            } + (if (member.isOnline) " · 在线" else " · 离线")
            binding.btnCmRole.setOnClickListener { onRoleClick(member) }
            binding.btnCmRemove.setOnClickListener { onRemoveClick(member) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemConsoleMemberBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position])

    override fun getItemCount(): Int = items.size
}

package com.limithue.familyguard.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.limithue.familyguard.R
import com.limithue.familyguard.data.model.Alert
import com.limithue.familyguard.databinding.ItemAlertBinding

class AlertAdapter(
    private val onHandleClick: (Alert) -> Unit
) : RecyclerView.Adapter<AlertAdapter.ViewHolder>() {

    private var items: List<Alert> = emptyList()

    fun submitList(list: List<Alert>) {
        items = list
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: ItemAlertBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(alert: Alert) {
            binding.tvAlertType.text = when (alert.type) {
                Alert.TYPE_FENCE -> "电子围栏"
                Alert.TYPE_NIGHT -> "夜归提醒"
                Alert.TYPE_STILL -> "长时间未动"
                Alert.TYPE_OFFLINE -> "设备离线"
                Alert.TYPE_SOS -> "紧急求助"
                else -> "安全提醒"
            }
            binding.tvAlertContent.text = alert.content
            binding.tvAlertTime.text = android.text.format.DateFormat.format(
                "yyyy-MM-dd HH:mm", alert.timestamp
            ).toString()
            binding.tvAlertStatus.text = if (alert.isHandled) "已处理" else "待处理"
            binding.tvAlertStatus.setTextColor(
                itemView.context.getColor(
                    if (alert.isHandled) R.color.accent else R.color.danger
                )
            )
            val levelColor = when (alert.level) {
                Alert.LEVEL_URGENT -> R.color.danger
                Alert.LEVEL_IMPORTANT -> R.color.warning
                else -> R.color.primary
            }
            binding.viewLevel.setBackgroundColor(itemView.context.getColor(levelColor))
            binding.btnHandle.visibility = if (alert.isHandled) android.view.View.GONE else android.view.View.VISIBLE
            binding.btnHandle.setOnClickListener { onHandleClick(alert) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemAlertBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position])

    override fun getItemCount(): Int = items.size
}

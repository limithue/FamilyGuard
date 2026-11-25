package com.limithue.familyguard.ui.console

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.limithue.familyguard.R
import com.limithue.familyguard.data.model.Geofence
import com.limithue.familyguard.databinding.ItemConsoleGeofenceBinding

class ConsoleGeofenceAdapter(
    private val onToggleClick: (Geofence) -> Unit,
    private val onRemoveClick: (Geofence) -> Unit
) : RecyclerView.Adapter<ConsoleGeofenceAdapter.ViewHolder>() {

    private var items: List<Geofence> = emptyList()

    fun submitList(list: List<Geofence>) {
        items = list
        notifyDataSetChanged()
    }

    inner class ViewHolder(val binding: ItemConsoleGeofenceBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(geofence: Geofence) {
            binding.tvCgName.text = geofence.name
            binding.tvCgDetail.text = "半径 ${geofence.radius.toInt()}m · " +
                "(${String.format("%.4f", geofence.latitude)}, ${String.format("%.4f", geofence.longitude)})"
            binding.btnCgToggle.text = if (geofence.enabled) "停用" else "启用"
            binding.btnCgToggle.setOnClickListener { onToggleClick(geofence) }
            binding.btnCgRemove.setOnClickListener { onRemoveClick(geofence) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemConsoleGeofenceBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) = holder.bind(items[position])

    override fun getItemCount(): Int = items.size
}

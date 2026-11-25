package com.limithue.familyguard.ui.console

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.limithue.familyguard.R
import com.limithue.familyguard.data.model.FamilyMember
import com.limithue.familyguard.data.model.Geofence
import com.limithue.familyguard.data.repository.AppRepository
import com.limithue.familyguard.databinding.DialogAddGeofenceBinding
import com.limithue.familyguard.databinding.DialogAddMemberBinding
import com.limithue.familyguard.databinding.FragmentConsoleBinding
import com.limithue.familyguard.util.SessionManager
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

/**
 * 管理员控制台。
 * 仅管理员身份可执行成员、围栏与数据维护操作。
 */
class ConsoleFragment : Fragment() {

    private var _binding: FragmentConsoleBinding? = null
    private val binding get() = _binding!!

    private lateinit var repository: AppRepository
    private lateinit var session: SessionManager
    private lateinit var memberAdapter: ConsoleMemberAdapter
    private lateinit var geofenceAdapter: ConsoleGeofenceAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentConsoleBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        repository = AppRepository.getInstance(requireContext())
        session = SessionManager.getInstance(requireContext())

        setupStatCards()
        setupAdapters()
        observeData()
        refreshStats()
        updatePermissionUi()

        binding.btnAddMember.setOnClickListener { showAddMemberDialog() }
        binding.btnAddGeofence.setOnClickListener { showAddGeofenceDialog() }
        binding.btnClearHandled.setOnClickListener { clearHandledAlerts() }
        binding.btnClearLocations.setOnClickListener { clearLocations() }
    }

    private fun isAdmin(): Boolean = session.isAdmin()

    private fun updatePermissionUi() {
        val admin = isAdmin()
        binding.tvAdminBadge.visibility = if (admin) View.VISIBLE else View.GONE
        binding.btnAddMember.isEnabled = admin
        binding.btnAddGeofence.isEnabled = admin
        binding.btnClearHandled.isEnabled = admin
        binding.btnClearLocations.isEnabled = admin
        binding.tvAdminName.text = if (admin) "李晓晨" else "普通成员"
    }

    private fun requireAdmin(): Boolean {
        if (!isAdmin()) {
            Toast.makeText(requireContext(), R.string.console_no_permission, Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

    private fun setupStatCards() {
        setStat(R.id.stat_members, "0", getString(R.string.console_stat_members))
        setStat(R.id.stat_admins, "0", getString(R.string.console_stat_admins))
        setStat(R.id.stat_alerts, "0", getString(R.string.console_stat_alerts))
        setStat(R.id.stat_locations, "0", getString(R.string.console_stat_locations))
    }

    private fun setStat(id: Int, value: String, label: String) {
        val card = requireView().findViewById<View>(id)
        card.findViewById<android.widget.TextView>(R.id.tv_stat_value).text = value
        card.findViewById<android.widget.TextView>(R.id.tv_stat_label).text = label
    }

    private fun setupAdapters() {
        memberAdapter = ConsoleMemberAdapter(
            onRoleClick = { showRoleDialog(it) },
            onRemoveClick = { confirmRemoveMember(it) }
        )
        binding.recyclerConsoleMembers.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = memberAdapter
        }

        geofenceAdapter = ConsoleGeofenceAdapter(
            onToggleClick = { toggleGeofence(it) },
            onRemoveClick = { confirmRemoveGeofence(it) }
        )
        binding.recyclerConsoleGeofences.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = geofenceAdapter
        }
    }

    private fun observeData() {
        viewLifecycleOwner.lifecycleScope.launch {
            repository.members.collectLatest { list ->
                memberAdapter.submitList(list)
                refreshStats()
            }
        }
        viewLifecycleOwner.lifecycleScope.launch {
            repository.geofences.collectLatest { geofenceAdapter.submitList(it) }
        }
    }

    private fun refreshStats() {
        viewLifecycleOwner.lifecycleScope.launch {
            setStat(R.id.stat_members, repository.countMembers().toString(), getString(R.string.console_stat_members))
            setStat(R.id.stat_admins, repository.countAdmins().toString(), getString(R.string.console_stat_admins))
            setStat(R.id.stat_alerts, repository.countAlerts().toString(), getString(R.string.console_stat_alerts))
            setStat(R.id.stat_locations, repository.countLocations().toString(), getString(R.string.console_stat_locations))
        }
    }

    // ---------- 成员管理 ----------

    private fun showAddMemberDialog() {
        if (!requireAdmin()) return
        val bindingDialog = DialogAddMemberBinding.inflate(layoutInflater)
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.console_dialog_add_member)
            .setView(bindingDialog.root)
            .setNegativeButton(R.string.console_dialog_cancel, null)
            .setPositiveButton(R.string.console_dialog_confirm) { _, _ ->
                val name = bindingDialog.etMemberName.text.toString().trim()
                if (name.isEmpty()) {
                    toast("请输入姓名")
                    return@setPositiveButton
                }
                viewLifecycleOwner.lifecycleScope.launch {
                    repository.addMember(
                        FamilyMember(
                            name = name,
                            phone = bindingDialog.etMemberPhone.text.toString().trim(),
                            role = FamilyMember.ROLE_MEMBER,
                            avatarText = name.take(1)
                        )
                    )
                    toast(getString(R.string.console_member_added))
                }
            }
            .show()
    }

    private fun showRoleDialog(member: FamilyMember) {
        if (!requireAdmin()) return
        val roles = arrayOf("管理员", "普通成员", "被照护人")
        val roleValues = arrayOf(
            FamilyMember.ROLE_ADMIN,
            FamilyMember.ROLE_MEMBER,
            FamilyMember.ROLE_CARED
        )
        val current = roleValues.indexOf(member.role).coerceAtLeast(0)
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("设置 ${member.name} 的角色")
            .setSingleChoiceItems(roles, current) { dialog, which ->
                viewLifecycleOwner.lifecycleScope.launch {
                    if (member.role == FamilyMember.ROLE_ADMIN &&
                        roleValues[which] != FamilyMember.ROLE_ADMIN &&
                        repository.countAdmins() <= 1
                    ) {
                        toast(getString(R.string.console_last_admin_error))
                        dialog.dismiss()
                        return@launch
                    }
                    repository.updateMemberRole(member.id, roleValues[which])
                    toast(getString(R.string.console_role_updated))
                    dialog.dismiss()
                }
            }
            .setNegativeButton(R.string.console_dialog_cancel, null)
            .show()
    }

    private fun confirmRemoveMember(member: FamilyMember) {
        if (!requireAdmin()) return
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.console_dialog_delete_confirm)
            .setMessage("移除成员「${member.name}」？")
            .setNegativeButton(R.string.console_dialog_cancel, null)
            .setPositiveButton(R.string.console_dialog_confirm) { _, _ ->
                viewLifecycleOwner.lifecycleScope.launch {
                    if (member.role == FamilyMember.ROLE_ADMIN && repository.countAdmins() <= 1) {
                        toast(getString(R.string.console_last_admin_error))
                        return@launch
                    }
                    repository.removeMember(member.id)
                    toast(getString(R.string.console_member_removed))
                }
            }
            .show()
    }

    // ---------- 围栏管理 ----------

    private fun showAddGeofenceDialog() {
        if (!requireAdmin()) return
        val bindingDialog = DialogAddGeofenceBinding.inflate(layoutInflater)
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.console_dialog_add_geofence)
            .setView(bindingDialog.root)
            .setNegativeButton(R.string.console_dialog_cancel, null)
            .setPositiveButton(R.string.console_dialog_confirm) { _, _ ->
                val name = bindingDialog.etGeofenceName.text.toString().trim()
                if (name.isEmpty()) {
                    toast("请输入围栏名称")
                    return@setPositiveButton
                }
                val radius = bindingDialog.etGeofenceRadius.text.toString().toFloatOrNull() ?: 500f
                val lat = bindingDialog.etGeofenceLat.text.toString().toDoubleOrNull() ?: 25.0389
                val lng = bindingDialog.etGeofenceLng.text.toString().toDoubleOrNull() ?: 102.7183
                viewLifecycleOwner.lifecycleScope.launch {
                    repository.addGeofence(
                        Geofence(name = name, latitude = lat, longitude = lng, radius = radius)
                    )
                    toast(getString(R.string.console_geofence_added))
                }
            }
            .show()
    }

    private fun toggleGeofence(geofence: Geofence) {
        if (!requireAdmin()) return
        viewLifecycleOwner.lifecycleScope.launch {
            repository.updateGeofence(geofence.copy(enabled = !geofence.enabled))
        }
    }

    private fun confirmRemoveGeofence(geofence: Geofence) {
        if (!requireAdmin()) return
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.console_dialog_delete_confirm)
            .setMessage("删除围栏「${geofence.name}」？")
            .setNegativeButton(R.string.console_dialog_cancel, null)
            .setPositiveButton(R.string.console_dialog_confirm) { _, _ ->
                viewLifecycleOwner.lifecycleScope.launch {
                    repository.removeGeofence(geofence.id)
                    toast(getString(R.string.console_geofence_removed))
                }
            }
            .show()
    }

    // ---------- 数据维护 ----------

    private fun clearHandledAlerts() {
        if (!requireAdmin()) return
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("清理告警")
            .setMessage("删除所有已处理的告警记录？")
            .setNegativeButton(R.string.console_dialog_cancel, null)
            .setPositiveButton(R.string.console_dialog_confirm) { _, _ ->
                viewLifecycleOwner.lifecycleScope.launch {
                    repository.clearHandledAlerts()
                    refreshStats()
                    toast(getString(R.string.console_data_cleared))
                }
            }
            .show()
    }

    private fun clearLocations() {
        if (!requireAdmin()) return
        MaterialAlertDialogBuilder(requireContext())
            .setTitle("清空轨迹")
            .setMessage("删除所有本地轨迹点？此操作不可恢复。")
            .setNegativeButton(R.string.console_dialog_cancel, null)
            .setPositiveButton(R.string.console_dialog_confirm) { _, _ ->
                viewLifecycleOwner.lifecycleScope.launch {
                    repository.clearLocations()
                    refreshStats()
                    toast(getString(R.string.console_data_cleared))
                }
            }
            .show()
    }

    private fun toast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

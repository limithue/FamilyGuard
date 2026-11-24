package com.limithue.familyguard.ui.settings

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.limithue.familyguard.R
import com.limithue.familyguard.databinding.FragmentSettingsBinding
import com.limithue.familyguard.service.LocationService
import com.limithue.familyguard.util.PermissionHelper

class SettingsFragment : Fragment() {

    private var _binding: FragmentSettingsBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        refreshPermissionStatus()

        binding.tvPermissionStatus.setOnClickListener {
            PermissionHelper.requestLocationPermissions(requireActivity())
        }

        binding.btnToggleService.setOnClickListener {
            if (!PermissionHelper.hasLocationPermission(requireActivity())) {
                PermissionHelper.requestLocationPermissions(requireActivity())
                return@setOnClickListener
            }
            toggleService()
        }
    }

    private fun toggleService() {
        val running = LocationService.isRunning()
        if (running) {
            LocationService.stop(requireContext())
            Toast.makeText(requireContext(), R.string.service_stopped, Toast.LENGTH_SHORT).show()
        } else {
            LocationService.start(requireContext())
            Toast.makeText(requireContext(), R.string.service_started, Toast.LENGTH_SHORT).show()
        }
        refreshServiceStatus()
    }

    private fun refreshPermissionStatus() {
        val granted = PermissionHelper.hasLocationPermission(requireActivity())
        binding.tvPermissionStatus.text = getString(
            if (granted) R.string.settings_location_status_granted
            else R.string.settings_location_status_denied
        )
        binding.tvPermissionStatus.setTextColor(
            requireContext().getColor(if (granted) R.color.accent else R.color.danger)
        )
    }

    private fun refreshServiceStatus() {
        val running = LocationService.isRunning()
        binding.tvServiceStatus.text = getString(
            if (running) R.string.settings_service_running else R.string.settings_service_stopped
        )
        binding.btnToggleService.text = getString(
            if (running) R.string.settings_service_stop else R.string.settings_service_start
        )
    }

    override fun onResume() {
        super.onResume()
        refreshPermissionStatus()
        refreshServiceStatus()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val granted = grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }
        Toast.makeText(
            requireContext(),
            if (granted) R.string.permission_granted else R.string.permission_denied,
            Toast.LENGTH_SHORT
        ).show()
        refreshPermissionStatus()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}

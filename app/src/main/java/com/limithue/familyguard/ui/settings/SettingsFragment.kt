package com.limithue.familyguard.ui.settings

import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.limithue.familyguard.R
import com.limithue.familyguard.databinding.FragmentSettingsBinding
import com.limithue.familyguard.service.LocationService
import com.limithue.familyguard.util.AppearancePrefs
import com.limithue.familyguard.util.GlassTint
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

        binding.rowPermission.setOnClickListener {
            PermissionHelper.requestLocationPermissions(requireActivity())
        }

        binding.switchService.setOnCheckedChangeListener { checked ->
            if (checked && !PermissionHelper.hasLocationPermission(requireActivity())) {
                binding.switchService.isChecked = false
                PermissionHelper.requestLocationPermissions(requireActivity())
                return@setOnCheckedChangeListener
            }
            if (checked) {
                LocationService.start(requireContext())
                Toast.makeText(requireContext(), R.string.service_started, Toast.LENGTH_SHORT).show()
            } else {
                LocationService.stop(requireContext())
                Toast.makeText(requireContext(), R.string.service_stopped, Toast.LENGTH_SHORT).show()
            }
            refreshServiceStatus()
        }

        binding.seekGlass.progress = AppearancePrefs.get(requireContext())
        binding.seekGlass.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (!fromUser) return
                AppearancePrefs.set(requireContext(), progress)
                GlassTint.apply(requireActivity().findViewById(android.R.id.content), progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

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
        binding.switchService.isChecked = running
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

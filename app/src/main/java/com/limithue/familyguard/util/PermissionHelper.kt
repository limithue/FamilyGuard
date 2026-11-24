package com.limithue.familyguard.util

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity

object PermissionHelper {

    private val LOCATION_PERMISSIONS = mutableListOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ).apply {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            add(Manifest.permission.POST_NOTIFICATIONS)
        }
    }.toTypedArray()

    fun hasLocationPermission(activity: FragmentActivity): Boolean {
        return LOCATION_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(activity, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun requestLocationPermissions(activity: FragmentActivity, requestCode: Int = 1001) {
        ActivityCompat.requestPermissions(activity, LOCATION_PERMISSIONS, requestCode)
    }
}

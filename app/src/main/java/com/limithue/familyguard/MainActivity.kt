package com.limithue.familyguard

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.limithue.familyguard.data.repository.AppRepository
import com.limithue.familyguard.databinding.ActivityMainBinding
import com.limithue.familyguard.ui.alert.AlertFragment
import com.limithue.familyguard.ui.family.FamilyFragment
import com.limithue.familyguard.ui.home.HomeFragment
import com.limithue.familyguard.ui.settings.SettingsFragment
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        seedData()
        setupBottomNav(savedInstanceState)
    }

    private fun seedData() {
        lifecycleScope.launch {
            AppRepository.getInstance(this@MainActivity).seedDemoDataIfNeeded()
        }
    }

    private fun setupBottomNav(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            switchFragment(HomeFragment())
        }

        binding.bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> switchFragment(HomeFragment())
                R.id.nav_family -> switchFragment(FamilyFragment())
                R.id.nav_alert -> switchFragment(AlertFragment())
                R.id.nav_settings -> switchFragment(SettingsFragment())
            }
            true
        }
    }

    private fun switchFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}

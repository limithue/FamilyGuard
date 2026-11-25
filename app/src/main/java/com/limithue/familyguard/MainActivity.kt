package com.limithue.familyguard

import android.os.Bundle
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.view.animation.OvershootInterpolator
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.limithue.familyguard.data.repository.AppRepository
import com.limithue.familyguard.databinding.ActivityMainBinding
import com.limithue.familyguard.ui.NavActionHandler
import com.limithue.familyguard.ui.alert.AlertFragment
import com.limithue.familyguard.ui.console.ConsoleFragment
import com.limithue.familyguard.ui.family.FamilyFragment
import com.limithue.familyguard.ui.home.HomeFragment
import com.limithue.familyguard.ui.settings.SettingsFragment
import com.limithue.familyguard.util.AppearancePrefs
import com.limithue.familyguard.util.GlassTint
import com.limithue.familyguard.util.IosNavController
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: IosNavController
    private var currentFragment: Fragment? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navController = IosNavController(
            binding.navBarContainer,
            binding.tvNavCompact,
            binding.tvNavLarge,
            binding.btnNavAction
        )
        val statusBarHeight = resources.getDimensionPixelSize(
            resources.getIdentifier("status_bar_height", "dimen", "android")
        )
        binding.topNav.setPadding(0, statusBarHeight, 0, 0)

        // 顶栏测量完成后按真实高度给内容留白，避免大标题压住内容
        binding.topNav.post {
            navController.setTopInset(binding.topNav.height + (10 * resources.displayMetrics.density).toInt())
            currentFragment?.view?.let {
                navController.attachScroll(it, resources.displayMetrics.density)
            }
        }

        seedData()
        setupBottomNav(savedInstanceState)
        applyGlassTint()
    }

    private fun seedData() {
        lifecycleScope.launch {
            AppRepository.getInstance(this@MainActivity).seedDemoDataIfNeeded()
        }
    }

    private fun setupBottomNav(savedInstanceState: Bundle?) {
        if (savedInstanceState == null) {
            switchFragment(HomeFragment(), R.id.nav_home)
        }

        binding.bottomNav.setOnItemSelectedListener { item ->
            switchFragment(
                when (item.itemId) {
                    R.id.nav_family -> FamilyFragment()
                    R.id.nav_alert -> AlertFragment()
                    R.id.nav_console -> ConsoleFragment()
                    R.id.nav_settings -> SettingsFragment()
                    else -> HomeFragment()
                },
                item.itemId
            )
            true
        }
    }

    private fun switchFragment(fragment: Fragment, itemId: Int) {
        currentFragment = fragment
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.ios_liquid_in, R.anim.ios_liquid_out)
            .replace(R.id.fragment_container, fragment)
            .commit()
        supportFragmentManager.executePendingTransactions()

        val (titleRes, iconRes) = when (itemId) {
            R.id.nav_home -> R.string.nav_home to R.drawable.ic_nav_sos
            R.id.nav_family -> R.string.nav_family to R.drawable.ic_nav_add
            R.id.nav_alert -> R.string.nav_alert to R.drawable.ic_nav_add
            R.id.nav_console -> R.string.console_title to null
            else -> R.string.settings_title to null
        }
        val handler = fragment as? NavActionHandler
        navController.configure(
            getString(titleRes),
            iconRes,
            if (iconRes != null && handler != null) ({ handler.onNavAction() }) else null
        )
        fragment.view?.let { navController.attachScroll(it, resources.displayMetrics.density) }
        applyGlassTint()
        bounceTab(itemId)
    }

    /** tab 选中时图标弹性放大，模拟液态玻璃的形变反馈 */
    private fun bounceTab(itemId: Int) {
        val index = when (itemId) {
            R.id.nav_home -> 0
            R.id.nav_family -> 1
            R.id.nav_alert -> 2
            R.id.nav_console -> 3
            else -> 4
        }
        val menuView = binding.bottomNav.getChildAt(0) as? ViewGroup ?: return
        val itemView = menuView.getChildAt(index) ?: return
        itemView.animate().cancel()
        itemView.animate()
            .scaleX(1.16f).scaleY(1.16f)
            .setDuration(110)
            .setInterpolator(DecelerateInterpolator())
            .withEndAction {
                itemView.animate()
                    .scaleX(1f).scaleY(1f)
                    .setDuration(240)
                    .setInterpolator(OvershootInterpolator(2.2f))
                    .start()
            }
            .start()
    }

    private fun applyGlassTint() {
        GlassTint.apply(binding.root, AppearancePrefs.get(this))
    }
}

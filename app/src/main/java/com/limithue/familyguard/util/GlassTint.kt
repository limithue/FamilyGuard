package com.limithue.familyguard.util

import android.view.View
import android.view.ViewGroup

/** 将 Liquid Glass 浓度应用 to 所有标记 android:tag="glass" 的玻璃容器 */
object GlassTint {
    fun apply(root: View, level: Int) {
        // 0 = 通透（背景光晕清晰可见），100 = 磨砂（乳白雾面，几乎不透）
        val alpha = 0.10f + (level.coerceIn(0, 100) / 100f) * 0.90f
        applyToView(root, alpha)
    }

    private fun applyToView(view: View, alpha: Float) {
        if (view.tag == "glass") {
            view.background?.mutate()?.alpha = (alpha * 255).toInt().coerceIn(0, 255)
        }
        if (view is ViewGroup) {
            for (i in 0 until view.childCount) applyToView(view.getChildAt(i), alpha)
        }
    }
}

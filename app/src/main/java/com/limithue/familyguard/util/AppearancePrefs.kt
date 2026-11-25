package com.limithue.familyguard.util

import android.content.Context

/** iOS 27 风格的 Liquid Glass 浓度偏好（0 = 更通透，100 = 更着色） */
object AppearancePrefs {
    private const val PREF_NAME = "familyguard_appearance"
    private const val KEY_GLASS_LEVEL = "glass_level"

    fun get(context: Context): Int =
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .getInt(KEY_GLASS_LEVEL, 50)

    fun set(context: Context, level: Int) {
        context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            .edit().putInt(KEY_GLASS_LEVEL, level).apply()
    }
}

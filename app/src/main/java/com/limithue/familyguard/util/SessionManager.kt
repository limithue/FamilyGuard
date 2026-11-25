package com.limithue.familyguard.util

import android.content.Context
import androidx.core.content.edit

/** 管理当前登录用户身份（演示版：默认 id=1 即管理员李晓晨） */
class SessionManager(context: Context) {

    private val prefs = context.applicationContext.getSharedPreferences("familyguard_session", Context.MODE_PRIVATE)

    var currentUserId: Long
        get() = prefs.getLong(KEY_USER_ID, DEFAULT_ADMIN_ID)
        set(value) = prefs.edit { putLong(KEY_USER_ID, value) }

    fun isAdmin(): Boolean = prefs.getBoolean(KEY_IS_ADMIN, true)

    fun setAdmin(isAdmin: Boolean) = prefs.edit { putBoolean(KEY_IS_ADMIN, isAdmin) }

    companion object {
        private const val KEY_USER_ID = "current_user_id"
        private const val KEY_IS_ADMIN = "is_admin"
        const val DEFAULT_ADMIN_ID = 1L

        @Volatile
        private var INSTANCE: SessionManager? = null

        fun getInstance(context: Context): SessionManager = INSTANCE ?: synchronized(this) {
            SessionManager(context).also { INSTANCE = it }
        }
    }
}

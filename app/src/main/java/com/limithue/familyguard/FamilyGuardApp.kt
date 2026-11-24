package com.limithue.familyguard

import android.app.Application
import com.limithue.familyguard.data.repository.AppRepository

class FamilyGuardApp : Application() {

    val repository: AppRepository by lazy { AppRepository.getInstance(this) }

    override fun onCreate() {
        super.onCreate()
    }
}

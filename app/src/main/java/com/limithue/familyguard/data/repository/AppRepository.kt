package com.limithue.familyguard.data.repository

import android.content.Context
import com.limithue.familyguard.data.db.AppDatabase
import com.limithue.familyguard.data.model.Alert
import com.limithue.familyguard.data.model.FamilyMember
import com.limithue.familyguard.data.model.LocationRecord
import kotlinx.coroutines.flow.Flow

class AppRepository(context: Context) {

    private val db = AppDatabase.getInstance(context)

    val members: Flow<List<FamilyMember>> = db.familyDao().getAll()
    val alerts: Flow<List<Alert>> = db.alertDao().getAll()
    val unhandledCount: Flow<Int> = db.alertDao().unhandledCount()

    suspend fun countMembers(): Int = db.familyDao().count()

    suspend fun addMember(member: FamilyMember): Long = db.familyDao().insert(member)

    suspend fun updateMember(member: FamilyMember) = db.familyDao().update(member)

    suspend fun saveLocation(record: LocationRecord): Long = db.locationDao().insert(record)

    suspend fun getRecentLocations(limit: Int = 200): List<LocationRecord> =
        db.locationDao().getRecent(limit)

    suspend fun addAlert(alert: Alert): Long = db.alertDao().insert(alert)

    suspend fun markAlertHandled(id: Long) = db.alertDao().markHandled(id)

    suspend fun clearAlerts() = db.alertDao().clearAll()

    /** 首次启动注入演示数据 */
    suspend fun seedDemoDataIfNeeded() {
        if (countMembers() > 0) return
        addMember(
            FamilyMember(
                name = "李晓晨",
                phone = "13800000001",
                role = FamilyMember.ROLE_ADMIN,
                avatarText = "李",
                isOnline = true,
                battery = 86,
                latitude = 25.0389,
                longitude = 102.7183
            )
        )
        addMember(
            FamilyMember(
                name = "父亲",
                phone = "13800000002",
                role = FamilyMember.ROLE_CARED,
                avatarText = "父",
                isOnline = true,
                battery = 64,
                latitude = 25.0401,
                longitude = 102.7102
            )
        )
        addMember(
            FamilyMember(
                name = "母亲",
                phone = "13800000003",
                role = FamilyMember.ROLE_CARED,
                avatarText = "母",
                isOnline = false,
                battery = 41,
                latitude = 25.0420,
                longitude = 102.7210
            )
        )
    }

    companion object {
        @Volatile
        private var INSTANCE: AppRepository? = null

        fun getInstance(context: Context): AppRepository {
            return INSTANCE ?: synchronized(this) {
                AppRepository(context.applicationContext).also { INSTANCE = it }
            }
        }
    }
}

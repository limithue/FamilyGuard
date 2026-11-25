package com.limithue.familyguard.data.repository

import android.content.Context
import com.limithue.familyguard.data.db.AppDatabase
import com.limithue.familyguard.data.model.Alert
import com.limithue.familyguard.data.model.FamilyMember
import com.limithue.familyguard.data.model.Geofence
import com.limithue.familyguard.data.model.LocationRecord
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class AppRepository(context: Context) {

    private val db = AppDatabase.getInstance(context)

    val members: Flow<List<FamilyMember>> = db.familyDao().getAll()
    val alerts: Flow<List<Alert>> = db.alertDao().getAll()
    val geofences: Flow<List<Geofence>> = db.geofenceDao().getAll()
    val unhandledCount: Flow<Int> = db.alertDao().unhandledCount()

    suspend fun countMembers(): Int = db.familyDao().count()

    suspend fun countAdmins(): Int = db.familyDao().countByRole(FamilyMember.ROLE_ADMIN)

    suspend fun countLocations(): Int = db.locationDao().count()

    suspend fun countAlerts(): Int = db.alertDao().count()

    suspend fun addMember(member: FamilyMember): Long = db.familyDao().insert(member)

    suspend fun updateMember(member: FamilyMember) = db.familyDao().update(member)

    suspend fun removeMember(id: Long) = db.familyDao().deleteById(id)

    suspend fun updateMemberRole(id: Long, role: String) = db.familyDao().updateRole(id, role)

    suspend fun saveLocation(record: LocationRecord): Long = db.locationDao().insert(record)

    suspend fun getRecentLocations(limit: Int = 200): List<LocationRecord> =
        db.locationDao().getRecent(limit)

    suspend fun clearLocations() = db.locationDao().clearAll()

    suspend fun addAlert(alert: Alert): Long = db.alertDao().insert(alert)

    suspend fun markAlertHandled(id: Long) = db.alertDao().markHandled(id)

    suspend fun clearAlerts() = db.alertDao().clearAll()

    suspend fun clearHandledAlerts() = db.alertDao().clearHandled()

    suspend fun addGeofence(geofence: Geofence): Long = db.geofenceDao().insert(geofence)

    suspend fun updateGeofence(geofence: Geofence) = db.geofenceDao().update(geofence)

    suspend fun removeGeofence(id: Long) = db.geofenceDao().deleteById(id)

    /** 首次启动注入演示数据 */
    suspend fun seedDemoDataIfNeeded() {
        val firstRun = countMembers() == 0
        if (firstRun) {
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
        // 首次运行补充默认围栏（幂等：按名称判断）
        if (db.geofenceDao().getAll().first().isEmpty()) {
            addGeofence(
                Geofence(
                    name = "家（安全区域）",
                    latitude = 25.0389,
                    longitude = 102.7183,
                    radius = 500f,
                    enabled = true
                )
            )
            addGeofence(
                Geofence(
                    name = "学校",
                    latitude = 25.0601,
                    longitude = 102.7100,
                    radius = 800f,
                    enabled = true
                )
            )
        }
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
